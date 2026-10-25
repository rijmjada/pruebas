package com.test.demo.rest.dtos;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;


    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;


    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precio;


    @NotNull(message = "El stock es obligatorio")
    @Positive
    private Integer stock;
}
