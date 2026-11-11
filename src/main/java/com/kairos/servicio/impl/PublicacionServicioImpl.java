package com.kairos.servicio.impl;

import com.kairos.dto.PublicacionFormDTO;
import com.kairos.enums.EstadoPublicacion;
import com.kairos.excepciones.OperacionNoAutorizadaException;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Categoria;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Subcategoria;
import com.kairos.modelo.Usuario;
import com.kairos.repositorio.PublicacionRepositorio;
import com.kairos.servicio.IAlmacenamientoServicio;
import com.kairos.servicio.ICategoriaServicio;
import com.kairos.servicio.IPublicacionServicio;
import com.kairos.servicio.IReporteServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PublicacionServicioImpl implements IPublicacionServicio {

    private final PublicacionRepositorio publicacionRepositorio;
    private final ICategoriaServicio categoriaServicio;
    private final IReporteServicio reporteServicio;
    private final IAlmacenamientoServicio almacenamientoServicio;

    public PublicacionServicioImpl(PublicacionRepositorio publicacionRepositorio,
                                   ICategoriaServicio categoriaServicio,
                                   IReporteServicio reporteServicio,
                                   IAlmacenamientoServicio almacenamientoServicio) {
        this.publicacionRepositorio = publicacionRepositorio;
        this.categoriaServicio = categoriaServicio;
        this.reporteServicio = reporteServicio;
        this.almacenamientoServicio = almacenamientoServicio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerPublicacionesActivas() {
        return publicacionRepositorio.findByEstadoOrderByFechaCreacionDesc(EstadoPublicacion.ACTIVA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> buscarPublicaciones(String texto, Long categoriaId, Long subcategoriaId, String orden) {
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();

        List<Publicacion> resultados =
                publicacionRepositorio.buscar(
                        List.of(EstadoPublicacion.ACTIVA, EstadoPublicacion.VENCIDA),
                        filtro,
                        categoriaId,
                        subcategoriaId);

        // El feed se ordena en memoria para no parametrizar el ORDER BY y mantener el repositorio simple.
        Comparator<Publicacion> porFecha = Comparator
                .comparing(Publicacion::getFechaCreacion,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        Comparator<Publicacion> comparador = switch (orden == null ? "" : orden) {
            case "precio-asc" -> Comparator.comparing(Publicacion::getPrecioOferta,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "precio-desc" -> Comparator.comparing(Publicacion::getPrecioOferta,
                    Comparator.nullsLast(Comparator.reverseOrder()));
            case "descuento" -> Comparator
                    .comparingInt(Publicacion::getPorcentajeDescuento)
                    .reversed()
                    .thenComparing(porFecha);
            default -> porFecha;
        };

        return resultados.stream().sorted(comparador).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Publicacion obtenerPorId(Long id) {
        return publicacionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> obtenerRelacionadas(Publicacion publicacion, int limite) {
        if (publicacion == null || publicacion.getCategoria() == null) {
            return List.of();
        }
        Long categoriaId = publicacion.getCategoria().getId();
        return publicacionRepositorio
                .findByEstadoOrderByFechaCreacionDesc(EstadoPublicacion.ACTIVA)
                .stream()
                .filter(p -> !Objects.equals(p.getId(), publicacion.getId()))
                .filter(p -> p.getCategoria() != null
                        && Objects.equals(p.getCategoria().getId(), categoriaId))
                .limit(Math.max(limite, 0))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Publicacion guardarPublicacion(PublicacionFormDTO dto, Usuario autor) {
        if (autor == null) {
            throw new OperacionNoAutorizadaException("Debes iniciar sesión para publicar una oferta.");
        }

        // 1. Resolver la taxonomía y comprobar que la subcategoría pertenece a la categoría elegida.
        Categoria categoria = categoriaServicio.obtenerPorId(dto.getCategoriaId());
        Subcategoria subcategoria = categoriaServicio.obtenerSubcategoriaPorId(dto.getSubcategoriaId());
        if (subcategoria.getCategoria() == null
                || !Objects.equals(subcategoria.getCategoria().getId(), categoria.getId())) {
            throw new OperacionNoAutorizadaException(
                    "La subcategoría seleccionada no pertenece a la categoría indicada.");
        }

        // 2. Almacenar las imágenes y construir la lista delimitada por comas.
        List<String> rutas = dto.getImagenes() == null
                ? List.of()
                : dto.getImagenes().stream()
                        .filter(archivo -> archivo != null && !archivo.isEmpty())
                        .map(almacenamientoServicio::guardarArchivo)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        // 3. Crear la publicación en estado PENDIENTE para revisión del administrador
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo(dto.getTitulo().trim());
        publicacion.setPrecioOferta(dto.getPrecioOferta());
        publicacion.setPrecioOriginal(dto.getPrecioOriginal());
        publicacion.setUrlOferta(dto.getUrlOferta().trim());
        publicacion.setDescripcion(dto.getDescripcion().trim());
        publicacion.setImagenesUrls(String.join(",", rutas));
        publicacion.setEstado(EstadoPublicacion.PENDIENTE);
        publicacion.setReportesCount(0);
        publicacion.setUsuario(autor);
        publicacion.setCategoria(categoria);
        publicacion.setSubcategoria(subcategoria);

        return publicacionRepositorio.save(publicacion);
    }

    @Override
    @Transactional
    public void reportarPublicacion(Long publicacionId, Usuario usuario) {
        reporteServicio.registrarReporte(publicacionId, usuario);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long publicacionId, EstadoPublicacion estado) {
        Publicacion publicacion = obtenerPorId(publicacionId);
        publicacion.setEstado(estado);
        publicacionRepositorio.save(publicacion);
    }

    @Override
    @Transactional
    public void aprobar(Long publicacionId) {
        cambiarEstado(publicacionId, EstadoPublicacion.ACTIVA);
    }

    @Override
    @Transactional
    public void rechazar(Long publicacionId) {
        cambiarEstado(publicacionId, EstadoPublicacion.RECHAZADA);
    }

    @Override
    @Transactional
    public void marcarVencida(Long publicacionId) {
        cambiarEstado(publicacionId, EstadoPublicacion.VENCIDA);
    }

    @Override
    @Transactional
    public void inhabilitar(Long publicacionId) {
        cambiarEstado(publicacionId, EstadoPublicacion.INACTIVA);
    }

    @Override
    @Transactional
    public void reactivar(Long publicacionId) {
        Publicacion publicacion = obtenerPorId(publicacionId);

        // 1. Limpiar el historial de reportes para que el contador vuelva a 0 de forma coherente.
        reporteServicio.eliminarReportesDePublicacion(publicacionId);

        // 2. Devolver la oferta al feed público.
        publicacion.setEstado(EstadoPublicacion.ACTIVA);
        publicacion.setReportesCount(0);
        publicacionRepositorio.save(publicacion);
    }

    @Override
    @Transactional
    public void eliminar(Long publicacionId) {
        Publicacion publicacion = obtenerPorId(publicacionId);

        // 1. Borrar primero los reportes para respetar la integridad referencial.
        reporteServicio.eliminarReportesDePublicacion(publicacionId);

        // 2. Borrar los archivos físicos asociados a la oferta.
        publicacion.getListaImagenes().forEach(almacenamientoServicio::eliminarArchivo);

        // 3. Hard delete de la publicación
        publicacionRepositorio.delete(publicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> listarTodas() {
        return publicacionRepositorio.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> listarPorEstado(EstadoPublicacion estado) {
        return publicacionRepositorio.findByEstadoOrderByFechaCreacionDesc(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> listarPorUsuario(Long usuarioId) {
        return publicacionRepositorio.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTodas() {
        return publicacionRepositorio.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoPublicacion estado) {
        return publicacionRepositorio.countByEstado(estado);
    }
}

