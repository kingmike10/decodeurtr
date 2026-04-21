package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class AllDecodersByClientResponseDTO {

    private Long id;
    private String adresseIp;
    private String etat;
    private String  lastRestart;
    private String lastReinit;

        public AllDecodersByClientResponseDTO(Long id,String adresseIp, String etat, String lastRestart, String lastReinit) {
            this.id = id;
            this.adresseIp = adresseIp;
            this.etat = etat;
            this.lastRestart = lastRestart;
            this.lastReinit = lastReinit;
        }
}
