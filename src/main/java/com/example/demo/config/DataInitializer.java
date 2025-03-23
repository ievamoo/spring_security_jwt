package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = new User();
        admin.setUsername(Role.ADMIN.name());
        admin.setPassword(passwordEncoder.encode(Role.ADMIN.name()));
        admin.setRoles(List.of(Role.ADMIN, Role.USER));
        userRepository.save(admin);

        User user = new User();
        user.setUsername(Role.USER.name());
        user.setPassword(passwordEncoder.encode(Role.USER.name()));
        user.setRoles(List.of(Role.USER));
        userRepository.save(user);
    }
}
