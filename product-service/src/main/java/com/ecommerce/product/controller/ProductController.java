package com.ecommerce.product.controller;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.security.JwtUtil;
import com.ecommerce.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/admin/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product,
                                              HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        if (!jwtUtil.hasRole(token, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product));
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestBody Product product,
                                                 HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        if (!jwtUtil.hasRole(token, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
