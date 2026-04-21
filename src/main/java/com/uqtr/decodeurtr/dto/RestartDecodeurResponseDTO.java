package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RestartDecodeurResponseDTO {
    private String response;

    public RestartDecodeurResponseDTO(String response) {
        this.response = response;
    }
}
