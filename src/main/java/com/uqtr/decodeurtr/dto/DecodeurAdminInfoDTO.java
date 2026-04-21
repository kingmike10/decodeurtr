package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DecodeurAdminInfoDTO {

    private Long id;
    private String adresseIp;
    private String etat;
    private List<String> chaines;

    public DecodeurAdminInfoDTO() {
    }

    public DecodeurAdminInfoDTO(Long id, String adresseIp, String etat, List<String> chaines) {
        this.id = id;
        this.adresseIp = adresseIp;
        this.etat = etat;
        this.chaines = chaines;
    }

}
