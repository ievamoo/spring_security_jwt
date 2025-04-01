package com.example.demo.controller;

import com.example.demo.config.TestMockBeansConfig;
import com.example.demo.config.TestSecurityConfig;
import com.example.demo.dto.SupplierDto;
import com.example.demo.model.Address;
import com.example.demo.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
@Import({TestSecurityConfig.class, TestMockBeansConfig.class})
class SupplierControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/suppliers";

    private SupplierDto getSampleSupplierDto() {
        return SupplierDto.builder()
                .id(1L)
                .name("ACME Corp")
                .email("acme@example.com")
                .address(Address.builder()
                        .street("5th Avenue")
                        .city("New York")
                        .country("USA")
                        .build())
                .carParts(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("GET /api/suppliers - Should return list of suppliers")
    void getAllSuppliers_ShouldReturnList() throws Exception {
        List<SupplierDto> suppliers = List.of(getSampleSupplierDto());

        when(supplierService.getAllSuppliers()).thenReturn(suppliers);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ACME Corp"))
                .andExpect(jsonPath("$[0].email").value("acme@example.com"));
    }

    @Test
    @DisplayName("POST /api/suppliers - Should add supplier and return updated list")
    void addSupplier_ShouldReturnUpdatedList() throws Exception {
        SupplierDto request = getSampleSupplierDto();

        List<SupplierDto> updatedList = List.of(request);

        when(supplierService.addSupplier(any(SupplierDto.class))).thenReturn(updatedList);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ACME Corp"))
                .andExpect(jsonPath("$[0].email").value("acme@example.com"));
    }

    @Test
    @DisplayName("PUT /api/suppliers/{id} - Should update and return supplier")
    void updateSupplier_ShouldReturnUpdatedSupplier() throws Exception {
        SupplierDto request = getSampleSupplierDto();

        when(supplierService.updateSupplier(eq(1L), any(SupplierDto.class))).thenReturn(request);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ACME Corp"))
                .andExpect(jsonPath("$.email").value("acme@example.com"));
    }

    @Test
    @DisplayName("DELETE /api/suppliers/{id} - Should delete supplier and return 204")
    void deleteSupplier_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }
}