package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AllClientsResponseDTO {

    private String idConnexion;
    private String nomClient;
    // Liste des IDs des décodeurs associés au client
    private List<Long> decodeurIds;

    public AllClientsResponseDTO(String idConnexion, String nomClient, List<Long> decodeurIds) {
        this.idConnexion = idConnexion;
        this.nomClient = nomClient;
        this.decodeurIds = decodeurIds;
    }

}
