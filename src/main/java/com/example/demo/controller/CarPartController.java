package com.example.demo.controller;

import com.example.demo.dto.CarPartDto;
import com.example.demo.service.CarPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/carPart")
@RequiredArgsConstructor
public class CarPartController {

    private final CarPartService carPartService;

    @GetMapping
    public ResponseEntity<List<CarPartDto>> getAllCarParts() {
        log.info("GET /api/carPart - Fetching all car parts");
        var result = carPartService.getAllCarParts();
        log.debug("Found {} car parts", result.size());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{supplierId}")
    public ResponseEntity<List<CarPartDto>> addCarPart(
            @PathVariable Long supplierId,
            @RequestBody CarPartDto dto
    ) {
        log.info("POST /api/carPart/{} - Adding new car part for supplier", supplierId);
        log.debug("New car part details: {}", dto);
        var result = carPartService.addCarPart(supplierId, dto);
        log.info("Successfully added new car part for supplier {}", supplierId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarPartDto> updateCarPart(
            @PathVariable Long id,
            @RequestBody CarPartDto dto
    ) {
        log.info("PUT /api/carPart/{} - Updating car part", id);
        log.debug("Updated car part details: {}", dto);
        var result = carPartService.updateCarPart(id, dto);
        log.info("Successfully updated car part with id {}", id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarPart(@PathVariable Long id) {
        log.info("DELETE /api/carPart/{} - Deleting car part", id);
        carPartService.deleteCarPart(id);
        log.info("Successfully deleted car part with id {}", id);
        return ResponseEntity.noContent().build();
    }

}
