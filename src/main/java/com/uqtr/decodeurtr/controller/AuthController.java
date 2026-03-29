package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.LoginRequestDTO;
import com.uqtr.decodeurtr.dto.LoginResponseDTO;
import com.uqtr.decodeurtr.service.auth.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO authentifier(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.authentifier(loginRequestDTO);
    }
}
