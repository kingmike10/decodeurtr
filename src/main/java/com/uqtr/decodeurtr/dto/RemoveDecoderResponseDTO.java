package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RemoveDecoderResponseDTO {

    private Long idDecodeur;
    private Long idAncienClient;
    private String message;
    private boolean succes;

    public RemoveDecoderResponseDTO(Long idDecodeur, Long idAncienClient, String message, boolean succes) {
        this.idDecodeur = idDecodeur;
        this.idAncienClient = idAncienClient;
        this.message = message;
        this.succes = succes;
    }
}
