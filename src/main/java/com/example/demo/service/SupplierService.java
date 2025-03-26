package com.example.demo.service;

import com.example.demo.dto.SupplierDto;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.utils.SupplierMapperM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapperM supplierMapper;

    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::modelToDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::modelToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    public List<SupplierDto> addSupplier(SupplierDto supplierDto) {
        if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email", supplierDto.getEmail());
        }
        var supplier = supplierMapper.dtoToModelOnAdd(supplierDto);
        supplierRepository.save(supplier);
        return supplierRepository.findAll().stream()
                .map(supplierMapper::modelToDto)
                .toList();
    }

    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        var supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
        validateEmail(supplierDto, supplier);
        supplierMapper.updateSupplierFromDto(supplierDto, supplier);
        return supplierMapper.modelToDto(supplierRepository.save(supplier));
    }

    private void validateEmail(SupplierDto supplierDto, Supplier supplier) {
        if (!supplierDto.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
                throw new ResourceAlreadyExistsException("Email", supplierDto.getEmail());
            }
        }
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier", id);
        }
        supplierRepository.deleteById(id);
    }
}
