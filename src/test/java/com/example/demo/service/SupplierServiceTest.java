package com.example.demo.service;

import com.example.demo.dto.SupplierDto;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Address;
import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.utils.SupplierMapperM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock private SupplierRepository supplierRepository;
    @Mock private SupplierMapperM supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDto supplierDto;

    @BeforeEach
    void setUp() {
        Address address = Address.builder()
                .street("5th Avenue")
                .city("New York")
                .country("USA")
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("ACME Supplies")
                .email("acme@example.com")
                .address(address)
                .carParts(List.of())
                .build();

        supplierDto = SupplierDto.builder()
                .id(1L)
                .name("ACME Supplies")
                .email("acme@example.com")
                .address(address)
                .carParts(List.of())
                .build();
    }

    @Test
    void getAllSuppliers_ShouldReturnListOfDtos() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.modelToDto(supplier)).thenReturn(supplierDto);

        List<SupplierDto> result = supplierService.getAllSuppliers();

        assertEquals(1, result.size());
        assertEquals("ACME Supplies", result.get(0).getName());
        verify(supplierRepository).findAll();
    }

    @Test
    void getSupplierById_ShouldReturnDto_WhenSupplierExists() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.modelToDto(supplier)).thenReturn(supplierDto);

        SupplierDto result = supplierService.getSupplierById(1L);

        assertEquals("acme@example.com", result.getEmail());
        verify(supplierRepository).findById(1L);
    }

    @Test
    void getSupplierById_ShouldThrow_WhenNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                supplierService.getSupplierById(99L));
    }

    @Test
    void addSupplier_ShouldAddAndReturnAllSuppliers_WhenEmailIsNew() {
        when(supplierRepository.existsByEmail("acme@example.com")).thenReturn(false);
        when(supplierMapper.dtoToModelOnAdd(supplierDto)).thenReturn(supplier);
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.modelToDto(supplier)).thenReturn(supplierDto);

        List<SupplierDto> result = supplierService.addSupplier(supplierDto);

        assertEquals(1, result.size());
        assertEquals("ACME Supplies", result.get(0).getName());
        verify(supplierRepository).save(supplier);
    }

    @Test
    void addSupplier_ShouldThrow_WhenEmailExists() {
        when(supplierRepository.existsByEmail("acme@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                supplierService.addSupplier(supplierDto));
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnUpdatedDto_WhenValid() {
        SupplierDto updatedDto = SupplierDto.builder()
                .name("ACME New")
                .email("acme@example.com") // same email
                .address(Address.builder()
                        .street("New Street")
                        .city("Los Angeles")
                        .country("USA")
                        .build())
                .build();

        Supplier updatedSupplier = Supplier.builder()
                .id(1L)
                .name("ACME New")
                .email("acme@example.com")
                .address(updatedDto.getAddress())
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierMapper).updateSupplierFromDto(updatedDto, supplier);
        when(supplierRepository.save(supplier)).thenReturn(updatedSupplier);
        when(supplierMapper.modelToDto(updatedSupplier)).thenReturn(updatedDto);

        SupplierDto result = supplierService.updateSupplier(1L, updatedDto);

        assertEquals("ACME New", result.getName());
        assertEquals("Los Angeles", result.getAddress().getCity());
        verify(supplierRepository, times(2)).save(supplier);
    }

    @Test
    void updateSupplier_ShouldThrow_WhenEmailAlreadyExists() {
        SupplierDto updatedDto = SupplierDto.builder()
                .email("duplicate@example.com")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        supplier.setEmail("original@example.com");

        assertThrows(ResourceAlreadyExistsException.class, () ->
                supplierService.updateSupplier(1L, updatedDto));
    }

    @Test
    void updateSupplier_ShouldThrow_WhenSupplierNotFound() {
        when(supplierRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                supplierService.updateSupplier(77L, supplierDto));
    }

    @Test
    void deleteSupplier_ShouldDelete_WhenExists() {
        when(supplierRepository.existsById(1L)).thenReturn(true);

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).deleteById(1L);
    }

    @Test
    void deleteSupplier_ShouldThrow_WhenNotExists() {
        when(supplierRepository.existsById(123L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                supplierService.deleteSupplier(123L));
    }
}
