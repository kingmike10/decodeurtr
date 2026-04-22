package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class LoginRequestDTO {
    private String identifiantConnexion;
    private String motDePasse;
}
