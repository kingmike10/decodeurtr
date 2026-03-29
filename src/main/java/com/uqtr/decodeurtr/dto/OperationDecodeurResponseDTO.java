package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationDecodeurResponseDTO {
    private String message;
    private boolean succes;

    public OperationDecodeurResponseDTO(String message, boolean succes) {
        this.message = message;
        this.succes = succes;
    }
}
