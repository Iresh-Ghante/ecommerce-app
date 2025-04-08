package com.ecommerce.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        if (!request.getHeaders().containsKey("Authorization")) {
            return exchange.getResponse().setComplete();
        }

        String token = request.getHeaders().getOrEmpty("Authorization").get(0).replace("Bearer ", "");
        
        if (!jwtUtil.validateToken(token)) {
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
