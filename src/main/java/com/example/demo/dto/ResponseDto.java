package com.example.demo.dto;

import java.util.List;

public record ResponseDto(
        String username,
        List<String> roles
) {
}
