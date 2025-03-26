package com.example.demo.utils;

import com.example.demo.dto.CarPartDto;
import com.example.demo.model.CarPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CarPartMapperM {

    @Named("withSupplier")
    @Mapping(source = "supplier.name", target = "supplierName")
    CarPartDto modelToDtoWithSupplier(CarPart entity);

    @Named("withoutSupplier")
    @Mapping(target = "supplierName", ignore = true)
    CarPartDto modelToDtoWithoutSupplier(CarPart entity);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    CarPart dtoToEntityWithoutSupplier(CarPartDto dto);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateEntityFromDto(CarPartDto dto, @MappingTarget CarPart entity);
    
}

