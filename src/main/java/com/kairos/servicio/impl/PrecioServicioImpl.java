package com.kairos.servicio.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kairos.modelo.Publicacion;
import com.kairos.servicio.PrecioServicio;

@Service 
public class PrecioServicioImpl implements PrecioServicio {

    @Override
    public BigDecimal calcularAhorro(Publicacion publicacion) {
      
        if (publicacion == null || publicacion.getPrecioOriginal() == null || publicacion.getPrecioOferta() == null) {
            return BigDecimal.ZERO;
        }

        //El ahorro no puede ser negativo si la oferta es más cara se reporta 0
        BigDecimal ahorro = publicacion.getPrecioOriginal()
                .subtract(publicacion.getPrecioOferta());

        return ahorro.compareTo(BigDecimal.ZERO) > 0 ? ahorro : BigDecimal.ZERO;
    }


    @Override
    public int calcularPorcentajeDescuento(Publicacion publicacion) {
        
                if (publicacion == null
                || publicacion.getPrecioOriginal() == null
                || publicacion.getPrecioOferta() == null
                || publicacion.getPrecioOriginal().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 1. Calcular el porcentaje sobre el precio original, redondeando al entero más cercano.
        BigDecimal descuento = calcularAhorro(publicacion)
                .multiply(BigDecimal.valueOf(100))
                .divide(publicacion.getPrecioOriginal(), 0, RoundingMode.HALF_UP);

        return descuento.intValue();


    }

    @Override
    public Map<Long, BigDecimal> calcularAhorros(List<Publicacion> publicaciones) {

             if (publicaciones == null || publicaciones.isEmpty()) {
            return Map.of();
        }

        // 1. Indexar por id para que las plantillas resuelvan el ahorro de cada tarjeta.
        Map<Long, BigDecimal> ahorros = new LinkedHashMap<>();

        for (Publicacion publicacion : publicaciones) {
            if (publicacion != null && publicacion.getId() != null) {
                ahorros.put(publicacion.getId(), calcularAhorro(publicacion));
            }
        }

        return ahorros;
    }

    @Override
    public Map<Long, Integer> calcularPorcentajesDescuento(List<Publicacion> publicaciones) {
     
                if (publicaciones == null || publicaciones.isEmpty()) {
            return Map.of();
        }

        // 1. Indexar por id para que las plantillas resuelvan el descuento de cada tarjeta.
        Map<Long, Integer> porcentajes = new LinkedHashMap<>();

        for (Publicacion publicacion : publicaciones) {
            if (publicacion != null && publicacion.getId() != null) {
                porcentajes.put(publicacion.getId(), calcularPorcentajeDescuento(publicacion));
            }
        }

        return porcentajes;



    }

    


}
