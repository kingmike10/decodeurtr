package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecoderAssignedResponseDTO {
    private Long id;
    private String adresseIp;
    private String etat;
    private Long idClient;
    private String nomClient;

    public DecoderAssignedResponseDTO() {
    }

    public DecoderAssignedResponseDTO(Long id, String adresseIp, String etat, Long idClient, String nomClient) {
        this.id = id;
        this.adresseIp = adresseIp;
        this.etat = etat;
        this.idClient = idClient;
        this.nomClient = nomClient;
    }

}
