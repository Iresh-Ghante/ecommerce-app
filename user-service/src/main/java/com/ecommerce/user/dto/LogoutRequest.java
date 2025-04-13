package com.ecommerce.user.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
    // getter, setter
}
