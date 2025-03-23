package com.example.demo.repository;

import com.example.demo.model.CarPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarPartsRepository extends JpaRepository<CarPart, Long> {
}
