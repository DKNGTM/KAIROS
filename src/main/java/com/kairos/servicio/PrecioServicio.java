package com.kairos.servicio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.kairos.modelo.Publicacion;

public interface PrecioServicio {


    BigDecimal calcularAhorro(Publicacion publicacion);


    int calcularPorcentajeDescuento(Publicacion publicacion);


    Map<Long, BigDecimal> calcularAhorros(List<Publicacion> publicaciones);

    Map<Long, Integer> calcularPorcentajesDescuento(List<Publicacion> publicaciones);




}
