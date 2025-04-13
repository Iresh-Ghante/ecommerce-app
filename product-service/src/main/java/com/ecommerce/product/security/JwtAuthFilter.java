package com.ecommerce.product.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		
		if(authHeader == null || !authHeader.startsWith("Bearer")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String token = authHeader.substring(7);
		try {
			jwtUtil.extractUsername(token); //validate token
			request.setAttribute("token", token); //pass token downstream
			filterChain.doFilter(request, response);
		} catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
