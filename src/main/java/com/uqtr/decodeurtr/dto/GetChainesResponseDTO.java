package com.uqtr.decodeurtr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetChainesResponseDTO {

    private Long idDecodeur;
    private List<String> chaines;

    public GetChainesResponseDTO(Long idDecodeur, List<String> chaines) {
        this.idDecodeur = idDecodeur;
        this.chaines = chaines;
    }


}
