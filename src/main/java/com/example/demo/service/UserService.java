package com.example.demo.service;

import com.example.demo.dto.RequestDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseDto addUserRole(RequestDto request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("user not found:" + request.username()));
        if (user.getRoles().contains(request.role())) {
            throw new RuntimeException("Role exists");
        }
        user.getRoles().add(request.role());
        userRepository.save(user);
        return new ResponseDto(
                user.getUsername(),
                user.getRoles());
    }
}
