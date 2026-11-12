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
import com.kairos.servicio.PrecioServicio;

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
    private final PrecioServicio precioServicio;

    public PublicacionServicioImpl(PublicacionRepositorio publicacionRepositorio,
                                   ICategoriaServicio categoriaServicio,
                                   IReporteServicio reporteServicio,
                                   IAlmacenamientoServicio almacenamientoServicio,
                                    PrecioServicio precioServicio) {
        this.publicacionRepositorio = publicacionRepositorio;
        this.categoriaServicio = categoriaServicio;
        this.reporteServicio = reporteServicio;
        this.almacenamientoServicio = almacenamientoServicio;
        this.precioServicio = precioServicio;
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

        Comparator<Publicacion> comparador = 
        
                switch (orden == null ? "" : orden) {

            case "precio-asc" -> Comparator.comparing(Publicacion::getPrecioOferta,
                    Comparator.nullsLast(Comparator.naturalOrder()));

            case "precio-desc" -> Comparator.comparing(Publicacion::getPrecioOferta,
                    Comparator.nullsLast(Comparator.reverseOrder()));
                    
            case "descuento" -> Comparator
                    .comparingInt((Publicacion publicacion) -> precioServicio.calcularPorcentajeDescuento(publicacion))
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

        // crea la publicacion para que la revise el administrador
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

        // limpia el historial de reporte
        reporteServicio.eliminarReportesDePublicacion(publicacionId);

        // muestra la oferta en el feed público.
        publicacion.setEstado(EstadoPublicacion.ACTIVA);
        publicacion.setReportesCount(0);
        publicacionRepositorio.save(publicacion);
    }

    @Override
    @Transactional
    public void eliminar(Long publicacionId) {
        Publicacion publicacion = obtenerPorId(publicacionId);

        reporteServicio.eliminarReportesDePublicacion(publicacionId);

        //borra las imagenes 
        publicacion.getListaImagenes().forEach(almacenamientoServicio::eliminarArchivo);

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


}

