package com.example.demo.config;

import com.example.demo.model.CarPart;
import com.example.demo.model.Supplier;
import com.example.demo.repository.CarPartsRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.security.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JsonService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarPartsRepository carPartsRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonService jsonService;

    @Override
    public void run(String... args) throws Exception {

        var users = jsonService
                .getData("users.json", new TypeReference<List<User>>() {});

        List<User> encodedPassUsers = users.stream()
                .peek(user -> user.setPassword(passwordEncoder.encode(user.getPassword())))
                .toList();
        userRepository.saveAll(encodedPassUsers);

        var suppliers = jsonService
                .getData("suppliers.json", new TypeReference<List<Supplier>>() {});
        supplierRepository.saveAll(suppliers);

        var carParts = jsonService
                .getData("carparts.json", new TypeReference<List<CarPart>>() {});
        carPartsRepository.saveAll(carParts);
    }
}
