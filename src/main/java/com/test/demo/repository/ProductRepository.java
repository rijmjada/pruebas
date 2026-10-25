package com.test.demo.repository;

import com.test.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStockGreaterThanOrderByNombreAsc(int stock);

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.stock > :stock
          AND p.precio BETWEEN :precioMin AND :precioMax
          AND p.nombre LIKE :nombre
        ORDER BY p.precio DESC, p.nombre ASC
    """)
    List<Product> buscarProductosPorStockYPrecioConNombre(
            @Param("stock") int stock,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("nombre") String nombre
    );
}
