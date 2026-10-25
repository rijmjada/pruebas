package com.test.demo.rest.dtos;


import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
}
