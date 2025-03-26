package com.example.demo.service;

import com.example.demo.dto.CarPartDto;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CarPartRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.utils.CarPartMapperM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarPartService {

    private final CarPartRepository carPartRepository;
    private final SupplierRepository supplierRepository;
    private final CarPartMapperM carPartMapper;

    public List<CarPartDto> getAllCarParts() {
        return carPartRepository.findAll().stream()
                .map(carPartMapper::modelToDtoWithSupplier)
                .toList();
    }
 
    public List<CarPartDto> addCarPart(Long supplierId, CarPartDto dto) {
        var existingSupplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", supplierId));
        var newCarPart = carPartMapper.dtoToEntityWithoutSupplier(dto);
        newCarPart.setSupplier(existingSupplier);
        carPartRepository.save(newCarPart);
        return carPartRepository.findAll().stream()
                .map(carPartMapper::modelToDtoWithSupplier)
                .toList();
    }

    public CarPartDto updateCarPart(Long id, CarPartDto dto) {
        var carPart = carPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car part", id));
        carPartMapper.updateEntityFromDto(dto, carPart);
        return carPartMapper.modelToDtoWithSupplier(carPartRepository.save(carPart));
    }

    public void deleteCarPart(Long id) {
        if (!carPartRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car Part", id);
        }
        carPartRepository.deleteById(id);
    }
}
