package com.ecommerce.product.service;

import com.ecommerce.product.kafka.ProductEventPublish;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductEventPublish eventPublisher;

    public Product addProduct(Product product) {
    	Product saved = productRepository.save(product);
        eventPublisher.publishProductCreated(saved);
        return saved;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setCategory(updatedProduct.getCategory());
        Product saved = productRepository.save(existing);
        eventPublisher.publishProductUpdated(saved);
        return saved;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}
