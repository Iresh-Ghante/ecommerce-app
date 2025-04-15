package com.ecommerce.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.security.JwtUtil;
import com.ecommerce.product.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody @Valid ProductRequest product, 
                                                      HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        if (!jwtUtil.hasRole(token, "ADMIN")) {
            log.warn("Unauthorized access attempt for adding product.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ProductResponse createdProduct = productService.addProduct(product);
        log.info("Product created successfully: {}", createdProduct.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @RequestBody @Valid ProductRequest product,
                                                         HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        if (!jwtUtil.hasRole(token, "ADMIN")) {
            log.warn("Unauthorized access attempt for updating product: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ProductResponse updatedProduct = productService.updateProduct(id, product);
        log.info("Product updated successfully: {}", updatedProduct.getId());
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        log.info("Fetched all products, total: {}", products.size());
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long id) {
        ProductResponse product = productService.getProductById(id);
        log.info("Fetched product by id: {}", id);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        List<ProductResponse> products = productService.searchByName(name);
        log.info("Search products by name: {}, found: {}", name, products.size());
        return ResponseEntity.ok(products);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("An error occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
    }
}