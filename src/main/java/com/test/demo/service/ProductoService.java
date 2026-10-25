package com.test.demo.service;


import com.test.demo.model.Product;
import com.test.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductRepository productRepository;


    public Product create(Product p) {
        return productRepository.save(p);
    }

    public List<Product> obtenerTodos(){
        return productRepository.findAll();
    }

    public List<Product> obtenerTodos(Pageable pageable) {
        return productRepository.findAll(pageable).getContent();
    }

    public List<Product> obtenerPorStock(int stock){
        return productRepository.findByStockGreaterThanOrderByNombreAsc(stock);
    }

    public List<Product> buscarProductosPorStockYPrecioConNombre(
                                              int stock,
                                              BigDecimal precioMin,
                                              BigDecimal precioMax,
                                              String nombre) {
        return productRepository.buscarProductosPorStockYPrecioConNombre(stock, precioMin, precioMax, nombre);
    }
}
