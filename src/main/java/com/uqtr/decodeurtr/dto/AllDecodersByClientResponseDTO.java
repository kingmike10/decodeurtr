package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class AllDecodersByClientResponseDTO {

    private String adresseIp;
    private String etat;
    private LocalDateTime lastRestart;
    private LocalDateTime lastReinit;

        public AllDecodersByClientResponseDTO(String adresseIp, String etat, LocalDateTime lastRestart, LocalDateTime lastReinit) {
            this.adresseIp = adresseIp;
            this.etat = etat;
            this.lastRestart = lastRestart;
            this.lastReinit = lastReinit;
        }
}
