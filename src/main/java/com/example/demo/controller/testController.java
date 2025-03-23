package com.example.demo.controller;
import com.example.demo.dto.RequestDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class testController {

    private final UserService userService;

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint - everyone can access!";
    }

    @GetMapping("/user")
    public String userEndpoint() {
        return "This is a user endpoint - only USER role can access this!";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "This is an admin endpoint -  ADMIN, USER and MODERATOR roles can access this!";
    }

    @GetMapping("/moderator")
    public String moderatorEndpoint() {
        return "This is an moderator endpoint - only MODERATOR and ADMIN role can access this!";
    }

    @PostMapping("/admin")
    public ResponseEntity<ResponseDto> addRoleToUser(@RequestBody RequestDto request) {
        var roles = userService.addUserRole(request);
        return  ResponseEntity.ok(roles);
    }


}
