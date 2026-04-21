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

    @Column(name="adresse_client",nullable = false)
    private String adresse;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Decodeur> decodeurs;

    public Client() {

    }
}
