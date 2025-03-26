package com.example.demo.dto;

import com.example.demo.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierDto {
    private Long id;
    private String name;
    private Address address;
    private String email;
    private List<CarPartDto> carParts;
}
