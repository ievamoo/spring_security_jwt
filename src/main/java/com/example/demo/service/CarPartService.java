package com.example.demo.service;

import com.example.demo.dto.CarPartDto;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.CarPart;
import com.example.demo.repository.CarPartRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.utils.CarPartMapperM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarPartService {

    private final CarPartRepository carPartRepository;
    private final SupplierRepository supplierRepository;
    private final CarPartMapperM carPartMapper;

    public List<CarPartDto> getAllCarParts() {
        log.debug("Fetching all car parts");
        var result = carPartRepository.findAll().stream()
                .map(carPartMapper::modelToDtoWithSupplier)
                .toList();
        log.debug("Found {} car parts", result.size());
        return result;
    }
 
    public List<CarPartDto> addCarPart(Long supplierId, CarPartDto dto) {
        log.debug("Adding new car part for supplier id: {}", supplierId);
        var existingSupplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", supplierId);
                    return new ResourceNotFoundException("Supplier", supplierId);
                });
        var newCarPart = carPartMapper.dtoToEntityWithoutSupplier(dto);
        newCarPart.setSupplier(existingSupplier);
        carPartRepository.save(newCarPart);
        log.info("Successfully added new car part for supplier id: {}", supplierId);
        return carPartRepository.findAll().stream()
                .map(carPartMapper::modelToDtoWithSupplier)
                .toList();
    }

    public CarPartDto updateCarPart(Long id, CarPartDto dto) {
        log.debug("Updating car part with id: {}", id);
        var carPart = carPartRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car part not found with id: {}", id);
                    return new ResourceNotFoundException("Car part", id);
                });
        carPartMapper.updateEntityFromDto(dto, carPart);
        var result = carPartMapper.modelToDtoWithSupplier(carPartRepository.save(carPart));
        log.info("Successfully updated car part with id: {}", id);
        return result;
    }

    public void deleteCarPart(Long id) {
        log.debug("Deleting car part with id: {}", id);
        if (!carPartRepository.existsById(id)) {
            log.error("Car part not found with id: {}", id);
            throw new ResourceNotFoundException("Car Part", id);
        }
        carPartRepository.deleteById(id);
        log.info("Successfully deleted car part with id: {}", id);
    }

    public CarPart getById(Long id) {
        return carPartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car Part", id));
    }
}
