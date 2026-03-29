package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
public class LoginRequestDTO {

    private String identifiantConnexion;
    private String motDePasse;
}
