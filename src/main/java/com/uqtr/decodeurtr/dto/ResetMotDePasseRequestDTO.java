package com.uqtr.decodeurtr.dto;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class ResetMotDePasseRequestDTO {
    private String identifiantConnexion;
    private String reponseSecrete;
    private String nouveauMotDePasse;
    private String confirmationMotDePasse;
}
