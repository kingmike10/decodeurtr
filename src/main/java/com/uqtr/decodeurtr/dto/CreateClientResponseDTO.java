package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class CreateClientResponseDTO {

    private   String message;
    private boolean succes;


    public CreateClientResponseDTO ( String message, boolean succes) {
        this.message = message;
        this.succes = succes;

    }
}
