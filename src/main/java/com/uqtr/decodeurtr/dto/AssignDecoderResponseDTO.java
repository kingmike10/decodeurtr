package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AssignDecoderResponseDTO {
    private Long idClient;
    private Long idDecodeur;
    private boolean success;
    private String message;

    public AssignDecoderResponseDTO() {
    }

    public AssignDecoderResponseDTO(Long idClient, Long idDecodeur, boolean success, String message) {
        this.idClient = idClient;
        this.idDecodeur = idDecodeur;
        this.success = success;
        this.message = message;
    }
}
