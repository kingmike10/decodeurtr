package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SimulateurRequestDTO {
    private String id;
    private String address;
    private String action;

    public SimulateurRequestDTO(String id, String address, String action) {
        this.id = id;
        this.address = address;
        this.action = action;
    }
}
