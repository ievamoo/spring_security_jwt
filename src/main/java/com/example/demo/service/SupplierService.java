package com.example.demo.service;

import com.example.demo.dto.SupplierDto;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.utils.SupplierMapperM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapperM supplierMapper;

    public List<SupplierDto> getAllSuppliers() {
        log.debug("Fetching all suppliers");
        var result = supplierRepository.findAll().stream()
                .map(supplierMapper::modelToDto)
                .collect(Collectors.toList());
        log.debug("Found {} suppliers", result.size());
        return result;
    }

    public SupplierDto getSupplierById(Long id) {
        log.debug("Fetching supplier with id: {}", id);
        var result = supplierRepository.findById(id)
                .map(supplierMapper::modelToDto)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", id);
                    return new ResourceNotFoundException("Supplier", id);
                });
        log.debug("Found supplier: {}", result);
        return result;
    }

    public List<SupplierDto> addSupplier(SupplierDto supplierDto) {
        log.debug("Adding new supplier with email: {}", supplierDto.getEmail());
        if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
            log.error("Email already exists: {}", supplierDto.getEmail());
            throw new ResourceAlreadyExistsException("Email", supplierDto.getEmail());
        }
        var supplier = supplierMapper.dtoToModelOnAdd(supplierDto);
        supplierRepository.save(supplier);
        log.info("Successfully added new supplier with email: {}", supplierDto.getEmail());
        return supplierRepository.findAll().stream()
                .map(supplierMapper::modelToDto)
                .toList();
    }

    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        log.debug("Updating supplier with id: {}", id);
        var supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Supplier not found with id: {}", id);
                    return new ResourceNotFoundException("Supplier", id);
                });
        validateEmail(supplierDto, supplier);
        supplierMapper.updateSupplierFromDto(supplierDto, supplier);
        supplierRepository.save(supplier);
        log.info("Successfully updated supplier with id: {}", id);
        return supplierMapper.modelToDto(supplierRepository.save(supplier));
    }

    private void validateEmail(SupplierDto supplierDto, Supplier supplier) {
        log.debug("Validating email for supplier id: {}", supplier.getId());
        if (!supplierDto.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
                log.error("Email already exists: {}", supplierDto.getEmail());
                throw new ResourceAlreadyExistsException("Email", supplierDto.getEmail());
            }
        }
        log.debug("Email validation passed for supplier id: {}", supplier.getId());
    }

    public void deleteSupplier(Long id) {
        log.debug("Deleting supplier with id: {}", id);
        if (!supplierRepository.existsById(id)) {
            log.error("Supplier not found with id: {}", id);
            throw new ResourceNotFoundException("Supplier", id);
        }
        supplierRepository.deleteById(id);
        log.info("Successfully deleted supplier with id: {}", id);
    }
}
