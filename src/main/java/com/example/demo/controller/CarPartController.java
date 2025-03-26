package com.example.demo.controller;


import com.example.demo.dto.CarPartDto;
import com.example.demo.service.CarPartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carPart")
@RequiredArgsConstructor
public class CarPartController {

    private final CarPartService carPartService;

    @GetMapping
    public ResponseEntity<List<CarPartDto>> getAllCarParts() {
        return ResponseEntity.ok(carPartService.getAllCarParts());
    }

    @PostMapping("/{supplierId}")
    public ResponseEntity<List<CarPartDto>> addCarPart(
            @PathVariable Long supplierId,
            @RequestBody CarPartDto dto
    ) {
        return ResponseEntity.ok(carPartService.addCarPart(supplierId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarPartDto> updateCarPart(
            @PathVariable Long id,
            @RequestBody CarPartDto dto
    ) {
        return ResponseEntity.ok(carPartService.updateCarPart(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarPart(@PathVariable Long id) {
        carPartService.deleteCarPart(id);
        return ResponseEntity.noContent().build();
    }

}
