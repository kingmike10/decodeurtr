package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.LoginRequestDTO;
import com.uqtr.decodeurtr.dto.LoginResponseDTO;
import com.uqtr.decodeurtr.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity authentifier(@RequestBody LoginRequestDTO loginRequestDTO) {
       return ResponseEntity.ok(authService.authentifier(loginRequestDTO));
    }
}
