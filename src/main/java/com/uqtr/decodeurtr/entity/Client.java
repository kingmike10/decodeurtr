package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "client")
@Getter @Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_client", nullable = false)
    private Long id;

    @Column(name="nom_client",nullable = false)
    private String nomClient;

    @Column(nullable = false, unique = true)
    private String identifiantConnexion;

    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private String nomAffichage;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Decodeur> decodeurs;



    public Client(String nomClient, String identifiantConnexion, String motDePasse, String nomAffichage) {
        this.nomClient = nomClient;
        this.identifiantConnexion = identifiantConnexion;
        this.motDePasse = motDePasse;
        this.nomAffichage = nomAffichage;
    }

    public Client() {

    }
}
