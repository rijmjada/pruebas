package com.test.demo.rest;


import com.test.demo.model.Product;
import com.test.demo.rest.dtos.ProductMapper;
import com.test.demo.rest.dtos.ProductRequest;
import com.test.demo.rest.dtos.ProductResponse;
import com.test.demo.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductoService productoService;
    private final ProductMapper productMapper;


    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request){

        Product product = productMapper.toEntity(request);
        productoService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listarTodos() {
        List<ProductResponse> listProducts = productoService.obtenerTodos()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return ResponseEntity.ok(listProducts);
    }

    @GetMapping("/paginados")
    public ResponseEntity<List<ProductResponse>> obtenerTodos(Pageable pageable) {
        List<ProductResponse> products = productoService.obtenerTodos(pageable).stream()
                                        .map(productMapper::toResponse)
                                        .toList();

        return ResponseEntity.ok(products);
    }

    @GetMapping("/stock")
    public ResponseEntity<List<ProductResponse>> obtenerPorStock(@RequestParam(defaultValue = "0") int stock) {

        List<ProductResponse> listProducts = productoService.obtenerPorStock(stock)
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return ResponseEntity.ok(listProducts);
    }


    @GetMapping("/searchable")
    public ResponseEntity<List<ProductResponse>> buscarProductosPorStockYPrecioConNombre(
                                                @RequestParam int stock,
                                                @RequestParam BigDecimal precioMin,
                                                @RequestParam BigDecimal precioMax,
                                                @RequestParam String nombre) {
        List<ProductResponse> listProducts = productoService.buscarProductosPorStockYPrecioConNombre(stock,precioMin,precioMax,nombre)
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return ResponseEntity.ok(listProducts);
    }

}
