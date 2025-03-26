package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CarPartDto {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String supplierName;
}
