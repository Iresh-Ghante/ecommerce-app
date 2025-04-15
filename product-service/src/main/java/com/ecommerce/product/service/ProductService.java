package com.ecommerce.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.kafka.ProductEventPublish;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductEventPublish eventPublisher;

    public ProductResponse addProduct(ProductRequest request) {
        log.info("Adding new product: {}", request.getName());
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .build();
        
        product = productRepository.save(product);
        eventPublisher.publishProductCreated(product);
        log.info("Product added successfully: {}", product.getName());
        
        return mapToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest updatedProduct) {
        log.info("Updating product with id: {}", id);
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setCategory(updatedProduct.getCategory());
        
        Product saved = productRepository.save(existing);
        eventPublisher.publishProductUpdated(saved);
        
        log.info("Product updated successfully: {}", saved.getName());
        
        return mapToResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        log.info("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        return mapToResponse(product);
    }
    
    public List<ProductResponse> searchByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
