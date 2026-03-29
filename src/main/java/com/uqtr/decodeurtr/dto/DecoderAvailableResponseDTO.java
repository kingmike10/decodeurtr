package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecoderAvailableResponseDTO {
    private String adresseIp;
    private String etat;

    public DecoderAvailableResponseDTO(String adresseIp,  String etat) {
        this.adresseIp = adresseIp;
        this.etat = etat;
    }
}
