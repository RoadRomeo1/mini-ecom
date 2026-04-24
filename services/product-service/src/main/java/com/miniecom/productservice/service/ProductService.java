package com.miniecom.productservice.service;

import com.miniecom.productservice.dto.ProductRequest;
import com.miniecom.productservice.dto.ProductResponse;
import com.miniecom.productservice.model.Product;
import com.miniecom.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse create(ProductRequest req) {
        Product p = new Product(req.name(), req.description(), req.price(), req.stock());
        return toResponse(repo.save(p));
    }

    @Cacheable(value = "products")
    public List<ProductResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getById(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Caching(
        put  = { @CachePut(value = "product", key = "#id") },
        evict = { @CacheEvict(value = "products", allEntries = true) }
    )
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setStock(req.stock());
        return toResponse(repo.save(p));
    }

    @Caching(evict = {
        @CacheEvict(value = "product", key = "#id"),
        @CacheEvict(value = "products", allEntries = true)
    })
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock());
    }
}
