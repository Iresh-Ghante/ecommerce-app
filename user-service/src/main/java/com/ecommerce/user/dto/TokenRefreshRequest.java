package com.ecommerce.user.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
    // getter, setter
}

