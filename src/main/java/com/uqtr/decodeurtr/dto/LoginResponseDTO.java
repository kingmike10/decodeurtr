package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private String role;
    private String identifiantConnexion;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(boolean success, String message,String role,String identifiant) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.identifiantConnexion = identifiant;
    }

    public LoginResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
