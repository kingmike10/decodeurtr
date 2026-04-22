package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AllClientsResponseDTO {

    private Long id;
    private String nomClient;
    private String adresse;
    private List<Long> decodeurIds;

    public AllClientsResponseDTO(Long id, String nomClient, String adresse,
                                 String identifiantConnexion, List<Long> decodeurIds) {
        this.id = id;
        this.nomClient = nomClient;
        this.adresse = adresse;
        this.decodeurIds = decodeurIds;
    }
}
