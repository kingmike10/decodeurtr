package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EtatDecodeurResponseDTO {

    private Long id;
    private String adresseIp;
    private String etat;
    private String lastRestart;
    private String lastReinit;

    public EtatDecodeurResponseDTO(  Long id,String adresseIp, String etat, String lastRestart, String lastReinit) {
        this.id = id;
        this.adresseIp = adresseIp;
        this.etat = etat;
        this.lastRestart = lastRestart;
        this.lastReinit = lastReinit;
    }
}