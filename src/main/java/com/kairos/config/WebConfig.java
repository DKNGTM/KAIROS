package com.kairos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración MVC. Expone el directorio físico de imágenes subidas bajo la ruta
 * pública {@code /uploads/**} para que las ofertas puedan servirlas desde Thymeleaf.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path directorioSubidas;

    public WebConfig(@Value("${kairos.uploads.dir:src/main/resources/static/images/uploads}") String directorio) {
        this.directorioSubidas = Paths.get(directorio).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = directorioSubidas.toUri().toString();
        if (!ubicacion.endsWith("/")) {
            ubicacion = ubicacion + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(ubicacion)
                .setCachePeriod(3600);
    }
}

