package com.uqtr.decodeurtr.dto;
import lombok.Getter;
@Getter
public class ResetMotDePasseResponseDTO {
    private final boolean success;
    private final String message;
    public ResetMotDePasseResponseDTO(boolean success, String message) {
        this.success = success; this.message = message;
    }
}
