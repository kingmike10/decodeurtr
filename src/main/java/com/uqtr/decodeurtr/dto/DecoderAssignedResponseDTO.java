package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecoderAssignedResponseDTO {
    private String adresseIp;
    private String etat;
    private Long idClient;
    private String nomClient;

    public DecoderAssignedResponseDTO(String adresseIp,  String etat, Long idClient, String nomClient) {
        this.adresseIp = adresseIp;
        this.etat = etat;
        this.idClient = idClient;
        this.nomClient = nomClient;
    }
}
