package com.example.demo.dto;

import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {

    private Long carPartId;
    private String carPartName;
    private Integer quantity;
    private Double price;

}
