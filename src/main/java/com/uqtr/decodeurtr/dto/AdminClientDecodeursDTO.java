package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AdminClientDecodeursDTO {
    private Long idClient;
    private String nomClient;
    private List<DecodeurAdminInfoDTO> decodeurs;

    public AdminClientDecodeursDTO() {
    }

    public AdminClientDecodeursDTO(Long idClient, String nomClient, List<DecodeurAdminInfoDTO> decodeurs) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.decodeurs = decodeurs;
    }

}
