package com.example.demo.config;

import com.example.demo.model.CarPart;
import com.example.demo.model.Supplier;
import com.example.demo.repository.CarPartRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JsonService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for initializing the database with sample data on application startup.
 * Loads data from JSON files and populates the following entities:
 * - Users (with encrypted passwords)
 * - Suppliers
 * - Car Parts
 *
 * This initializer uses JsonService to read data from JSON files located in the resources directory
 * and automatically encrypts user passwords before saving to the database.
 * The initialization happens once when the application starts.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarPartRepository carPartsRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonService jsonService;

    /**
     * Executes the data initialization process when the application starts.
     * This method performs the following operations in sequence:
     * 1. Loads users from users.json, encrypts their passwords, and saves them to the database
     * 2. Loads suppliers from suppliers.json and saves them to the database
     * 3. Loads car parts from carparts.json and saves them to the database
     *
     * The data is loaded using JsonService which handles JSON file reading and object mapping.
     * User passwords are encrypted using the configured PasswordEncoder before saving.
     *
     * @throws Exception if there are any issues reading the JSON files or saving to the database
     */
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
