package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SimulateurInfoResponseDTO {
    private String response;
    private String message;
    private String state;
    private String lastRestart;
    private String lastReinit;
}
