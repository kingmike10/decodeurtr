package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "identifiant_connexion", nullable = false, unique = true, length = 100)
    private String identifiantConnexion;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;


    @Column(name = "question_secrete", nullable = true, length = 255)
    private String questionSecrete;

    @Column(name = "reponse_secrete", nullable = true, length = 255)
    private String reponseSecrete; // hashée avec BCrypt

    @OneToOne
    @JoinColumn(name = "id_client", nullable = true)
    private Client client;

    public Utilisateur() {}
}
