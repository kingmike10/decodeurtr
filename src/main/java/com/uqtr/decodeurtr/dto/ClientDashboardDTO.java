package com.uqtr.decodeurtr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// ClientDashboardDTO.java
@Getter
@Setter
public class ClientDashboardDTO {
    private String nomClient;
    private long nbActifs;
    private long nbTotal;
    private List<DecodeurInfo> decodeurs;

    @Getter @Setter @AllArgsConstructor
    public static class DecodeurInfo {
        private Long id;
        private String adresseIp; // ou numeroSerie
        private String etat;
        private String lastRestart;
        private String lastReinit;

    }
}
