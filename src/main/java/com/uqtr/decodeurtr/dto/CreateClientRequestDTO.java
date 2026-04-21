package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class CreateClientRequestDTO {

    private String nomClient;
    private String adresse;
    private String identifiantConnexion;
    private String motDePasse;
}
