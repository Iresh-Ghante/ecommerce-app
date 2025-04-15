package com.ecommerce.gateway.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;
    
    private static final List<String> openApiEndpoints = List.of(
    	    "/auth/login",
    	    "/auth/register",
    	    "/actuator/**"  // Optional, for monitoring
    	);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

     // Bypass JWT filter for public endpoints
        if (openApiEndpoints.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Invalid JWT", HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange); // valid, continue
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        log.error(err);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // run before other filters
    }
}
