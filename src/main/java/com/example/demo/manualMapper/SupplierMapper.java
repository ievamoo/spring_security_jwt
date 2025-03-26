package com.example.demo.manualMapper;

import com.example.demo.dto.SupplierDto;
import com.example.demo.model.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SupplierMapper {

    private final CarPartMapper carPartMapper;

    public SupplierDto modelToDto(Supplier entity) {
        return SupplierDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .email(entity.getEmail())
                .carParts(
                        entity.getCarParts().stream()
                                .map(carPartMapper::modelToDtoWithoutSupplier)
                                .toList()
                )
                .build();

    }

    public Supplier dtoToEntityOnAdd(SupplierDto dto) {
        return Supplier.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .carParts(new ArrayList<>())
                .build();
    }

    public void updateEntityFromDto(SupplierDto supplierDto, Supplier supplier) {
        supplier.setName(supplierDto.getName());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());
    }
}
