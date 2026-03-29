package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class CreateClientResponseDTO {

    private Long id;
    private   String nomClient;
    private  String identifiantConnexion;
    private  String nomAffichage;
    private   String message;
    private boolean succes;

//    public CreateClientResponseDTO(Long id, String nomClient, String identifiantConnexion,
//                             String nomAffichage, String message, boolean succes) {
//        this.id = id;
//        this.nomClient = nomClient;
//        this.identifiantConnexion = identifiantConnexion;
//        this.nomAffichage = nomAffichage;
//        this.message = message;
//        this.succes = succes;
//    }

    public CreateClientResponseDTO (String nomClient, String identifiantConnexion,
    String nomAffichage, String message) {
        this.nomClient = nomClient;
        this.identifiantConnexion = identifiantConnexion;
        this.nomAffichage = nomAffichage;
        this.message = message;

    }
}
