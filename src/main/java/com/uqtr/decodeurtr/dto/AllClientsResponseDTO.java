package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AllClientsResponseDTO {

    private Long id;
    private String adresse;
    private String nomClient;
    private List<Long> decodeurIds;

    public AllClientsResponseDTO(Long id, String nomClient, String adresse, List<Long> decodeurIds) {
        this.id = id;
        this.adresse = adresse;
        this.nomClient = nomClient;
        this.decodeurIds = decodeurIds;
    }
}
