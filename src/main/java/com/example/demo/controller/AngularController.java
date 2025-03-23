package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/angular")
public class AngularController {

    @GetMapping("/public")
    public String publicEntry() {
        return "This is public endpoint";
    }

    @GetMapping("/private")
    public String privateEntry() {
        return "You are authenticated!";
    }

}
