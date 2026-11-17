package com.kairos.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.kairos.enums.EstadoPublicacion;
import com.kairos.enums.RolUsuario;
import com.kairos.modelo.Categoria;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Subcategoria;
import com.kairos.modelo.Usuario;
import com.kairos.repositorio.CategoriaRepositorio;
import com.kairos.repositorio.PublicacionRepositorio;
import com.kairos.repositorio.SubcategoriaRepositorio;
import com.kairos.repositorio.UsuarioRepositorio;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepositorio categoriaRepositorio;
    private final SubcategoriaRepositorio subcategoriaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PublicacionRepositorio publicacionRepositorio;
    private final boolean seedDemo;

    public DatosIniciales(CategoriaRepositorio categoriaRepositorio,
                          SubcategoriaRepositorio subcategoriaRepositorio,
                          UsuarioRepositorio usuarioRepositorio,
                          PublicacionRepositorio publicacionRepositorio,
                          @Value("${kairos.seed-demo:true}") boolean seedDemo) {
        this.categoriaRepositorio = categoriaRepositorio;
        this.subcategoriaRepositorio = subcategoriaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.publicacionRepositorio = publicacionRepositorio;
        this.seedDemo = seedDemo;
    }

    @Override
    public void run(String... args) {
        sembrarTaxonomia();
        sembrarUsuarios();
        if (seedDemo) {
            sembrarPublicacionesDemo();
        }
    }

    private void sembrarTaxonomia() {
        if (categoriaRepositorio.count() > 0) {
            return;
        }

        Categoria electronicos = new Categoria("Electrónicos de Consumo",
                "Dispositivos, hardware y tecnología de consumo.");
        electronicos.agregarSubcategoria(new Subcategoria("Smartphones & Accesorios"));
        electronicos.agregarSubcategoria(new Subcategoria("Computadoras & Laptops"));
        electronicos.agregarSubcategoria(new Subcategoria("Tablets & E-Readers"));
        electronicos.agregarSubcategoria(new Subcategoria("Televisores & Audio"));
        electronicos.agregarSubcategoria(new Subcategoria("Consolas & Gaming"));

        Categoria software = new Categoria("Software y Aplicaciones",
                "Licencias, SaaS, cursos y herramientas digitales.");
        software.agregarSubcategoria(new Subcategoria("Productividad & SaaS"));
        software.agregarSubcategoria(new Subcategoria("Licencias OS & Dev"));
        software.agregarSubcategoria(new Subcategoria("Apps Móviles"));
        software.agregarSubcategoria(new Subcategoria("Juegos / Videojuegos"));
        software.agregarSubcategoria(new Subcategoria("Cursos & Educación Digital"));

        Categoria electrodomesticos = new Categoria("Electrodomésticos",
                "Equipamiento para el hogar y la cocina.");
        electrodomesticos.agregarSubcategoria(new Subcategoria("Refrigeración"));
        electrodomesticos.agregarSubcategoria(new Subcategoria("Lavado & Secado"));
        electrodomesticos.agregarSubcategoria(new Subcategoria("Pequeños Electrodomésticos de Cocina"));
        electrodomesticos.agregarSubcategoria(new Subcategoria("Climatización"));
        electrodomesticos.agregarSubcategoria(new Subcategoria("Aspiradoras & Limpieza"));

        categoriaRepositorio.saveAll(List.of(electronicos, software, electrodomesticos));
    }

    private void sembrarUsuarios() {
        crearUsuarioSiNoExiste("admin_kairos", "admin@kairos.dev", "admin1234", RolUsuario.ADMIN);
        crearUsuarioSiNoExiste("curador_demo", "demo@kairos.dev", "demo1234", RolUsuario.USER);
        crearUsuarioSiNoExiste("kernel_panic", "kernel@kairos.dev", "demo1234", RolUsuario.USER);
        crearUsuarioSiNoExiste("nexus_scout", "nexus@kairos.dev", "demo1234", RolUsuario.USER);
        crearUsuarioSiNoExiste("caffeine_architect", "caffeine@kairos.dev", "demo1234", RolUsuario.USER);
    }

    private void crearUsuarioSiNoExiste(String nombre, String correo, String contrasena, RolUsuario rol) {
        if (usuarioRepositorio.existsByNombreUsuarioIgnoreCase(nombre)) {
            return;
        }
        usuarioRepositorio.save(new Usuario(nombre, correo, contrasena, rol));
    }

    private void sembrarPublicacionesDemo() {
        if (publicacionRepositorio.count() > 0) {
            return;
        }

        Usuario curador = usuarioRepositorio.findByNombreUsuarioIgnoreCase("curador_demo").orElse(null);
        Usuario kernel = usuarioRepositorio.findByNombreUsuarioIgnoreCase("kernel_panic").orElse(null);
        Usuario nexus = usuarioRepositorio.findByNombreUsuarioIgnoreCase("nexus_scout").orElse(null);
        Usuario caffeine = usuarioRepositorio.findByNombreUsuarioIgnoreCase("caffeine_architect").orElse(null);
        if (curador == null || kernel == null || nexus == null || caffeine == null) {
            return;
        }

        Categoria electronicos = categoriaRepositorio.findByNombreIgnoreCase("Electrónicos de Consumo").orElse(null);
        Categoria software = categoriaRepositorio.findByNombreIgnoreCase("Software y Aplicaciones").orElse(null);
        Categoria electrodomesticos = categoriaRepositorio.findByNombreIgnoreCase("Electrodomésticos").orElse(null);
        if (electronicos == null || software == null || electrodomesticos == null) {
            return;
        }

        Subcategoria laptops = buscarSubcategoria("Computadoras & Laptops", electronicos.getId());
        Subcategoria productividad = buscarSubcategoria("Productividad & SaaS", software.getId());
        Subcategoria aspiradoras = buscarSubcategoria("Aspiradoras & Limpieza", electrodomesticos.getId());
        Subcategoria consolas = buscarSubcategoria("Consolas & Gaming", electronicos.getId());
        Subcategoria smartphones = buscarSubcategoria("Smartphones & Accesorios", electronicos.getId());
        Subcategoria cocina = buscarSubcategoria("Pequeños Electrodomésticos de Cocina", electrodomesticos.getId());


        publicacionRepositorio.save(crearPublicacion(
                "ThinkPad Carbon X1 Gen 11 - Core i7 1365U, 32GB LPDDR5, 1TB SSD NVMe",
                "3600000", "7840000",
                "https://www.lenovo.com/",
                "Edición empresarial certificada. Incluye garantía premier de 3 años onsite. "
                        + "Descuento aplicado directamente en carrito con cupón de liquidación.",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.ACTIVA, curador, electronicos, laptops));

        publicacionRepositorio.save(crearPublicacion(
                "All Products Pack Anual - Licencia Comercial Completa 1 Año",
                "1000000", "3120000",
                "https://www.jetbrains.com/",
                "Promoción para desarrolladores independientes y startups. Válido para renovación "
                        + "o cuenta nueva introduciendo el cupón en pasarela.",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.ACTIVA, kernel, software, productividad));

        publicacionRepositorio.save(crearPublicacion(
                "Roborock S8 Pro Ultra Robot Aspirador con Base de Autovaciado",
                "1280000", "4000000",
                "https://www.roborock.com/",
                "Unidad con base de autovaciado y mapeo LiDAR. Stock limitado en almacén central. "
                        + "Verifica la disponibilidad antes de finalizar la compra.",
                "https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.ACTIVA, curador, electrodomesticos, aspiradoras));

        publicacionRepositorio.save(crearPublicacion(
                "PlayStation 5 Slim Digital Chassis + 2 Mandos DualSense Midnight",
                "1400000", "2000000",
                "https://www.playstation.com/",
                "Oferta flash de medianoche expirada. Stock liquidado en menos de 18 minutos "
                        + "en almacén central.",
                "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.VENCIDA, nexus, electronicos, consolas));

        publicacionRepositorio.save(crearPublicacion(
                "Google Pixel 8 Pro 256GB Obsidian - Libre Internacional + Buds Pro",
                "2480000", "4000000",
                "https://store.google.com/",
                "Empaque nuevo con sello de fábrica intacto. Envío internacional bonificado "
                        + "aplicando código en checkout.",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.ACTIVA, nexus, electronicos, smartphones));

        publicacionRepositorio.save(crearPublicacion(
                "Breville Barista Touch Impress - Cafetera Espresso Control PID",
                "2360000", "4320000",
                "https://www.breville.com/",
                "Precio mínimo histórico en España. Sistema de dosificación asistida y "
                        + "preinfusión digital de baja presión.",
                "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.ACTIVA, caffeine, electrodomesticos, cocina));


        publicacionRepositorio.save(crearPublicacion(
                "Samsung Odyssey G9 49\" QHD 240Hz - Monitor Ultrawide Curvo",
                "4800000", "7200000",
                "https://www.samsung.com/",
                "Monitor ultrawide de 49 pulgadas con panel QLED y 240 Hz. Envío gratuito "
                        + "con garantía oficial de 3 años.",
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=1200&q=80",
                EstadoPublicacion.PENDIENTE, kernel, electronicos, laptops));
    }

    private Subcategoria buscarSubcategoria(String nombre, Long categoriaId) {
        return subcategoriaRepositorio
                .findByNombreIgnoreCaseAndCategoriaId(nombre, categoriaId)
                .orElseThrow(() -> new IllegalStateException("Subcategoría demo no encontrada: " + nombre));
    }

    private Publicacion crearPublicacion(String titulo, String oferta, String original, String url,
                                         String descripcion, String imagen, EstadoPublicacion estado,
                                         Usuario autor, Categoria categoria, Subcategoria subcategoria)
                                        {
        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo(titulo);
        publicacion.setPrecioOferta(new BigDecimal(oferta));
        publicacion.setPrecioOriginal(new BigDecimal(original));
        publicacion.setUrlOferta(url);
        publicacion.setDescripcion(descripcion);
        publicacion.setImagenesUrls(imagen);
        publicacion.setEstado(estado);
        publicacion.setReportesCount(0);
        publicacion.setUsuario(autor);
        publicacion.setCategoria(categoria);
        publicacion.setSubcategoria(subcategoria);
        return publicacion;
    }
}
