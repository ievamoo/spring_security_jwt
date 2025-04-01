package com.example.demo.utils;

import com.example.demo.dto.SupplierDto;
import com.example.demo.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CarPartMapperM.class})
public interface SupplierMapperM {

    @Mapping(source = "carParts", target = "carParts", qualifiedByName = "withoutSupplier")
    SupplierDto modelToDto(Supplier supplier);

    @Mapping(target = "carParts", ignore = true)
    Supplier dtoToModelOnAdd(SupplierDto supplierDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carParts", ignore = true)
    void updateSupplierFromDto(SupplierDto dto, @MappingTarget Supplier entity);
}