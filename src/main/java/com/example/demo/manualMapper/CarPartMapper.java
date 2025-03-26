package com.example.demo.manualMapper;

import com.example.demo.dto.CarPartDto;
import com.example.demo.model.CarPart;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class CarPartMapper {

   public CarPartDto modelToDtoWithoutSupplier(CarPart entity) {
        return CarPartDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }


    public CarPart dtoToEntityWithoutSupplier(CarPartDto dto) {
       return CarPart.builder()
               .name(dto.getName())
               .price(dto.getPrice())
               .stock(dto.getStock())
               .build();
    }

    public CarPartDto modelToDtoWithSupplier(CarPart entity) {
        return CarPartDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .supplierName(entity.getSupplier().getName())
                .build();
    }

    /**
     * Updates only the modifiable fields of a CarPart entity.
     * Preserves the ID, supplier, and order items relationships.
     *
     * @param dto The DTO containing updated values
     * @param carPart The entity to update
     */
    public void updateEntityFromDto(CarPartDto dto, CarPart carPart) {
        // Only update the modifiable fields
        carPart.setName(dto.getName());
        carPart.setPrice(dto.getPrice());
        carPart.setStock(dto.getStock());
        // ID, supplier, and orderItems are preserved
    }
}
