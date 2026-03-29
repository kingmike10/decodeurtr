package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetDecodeurResponseDTO {
    private String response;

    public ResetDecodeurResponseDTO(String response) {
        this.response = response;
    }
}
