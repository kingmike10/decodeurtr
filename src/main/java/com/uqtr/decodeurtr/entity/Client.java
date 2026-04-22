package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entité représentant un client du câblodistributeur.
 *
 * Un client est une entité institutionnelle (hôtel, entreprise) possédant
 * un compte de connexion unique et un parc de décodeurs qui lui est assigné.
 * Cette classe est mappée à la table "client" en base de données.
 */
@Entity
@Table(name = "client")
@Getter @Setter
public class Client {

    /**
     * Identifiant unique généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client", nullable = false)
    private Long id;

    /**
     * Nom descriptif du client (ex : "Hôtel Le Bonne Entente").
     */
    @Column(name = "nom_client", nullable = false)
    private String nomClient;

    /**
     * Adresse physique du client.
     */
    @Column(name = "adresse_client", nullable = false)
    private String adresse;

    /**
     * Compte utilisateur associé à ce client.
     *
     * La cascade ALL garantit que la suppression d'un client entraîne
     * automatiquement la suppression de son compte utilisateur en base,
     * évitant ainsi des entrées orphelines dans la table utilisateur.
     */
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Utilisateur utilisateur;

    /**
     * Liste des décodeurs assignés à ce client.
     *
     * Aucune cascade n'est appliquée sur cette relation : la suppression
     * d'un client ne supprime pas ses décodeurs. Ces derniers sont
     * désassignés manuellement dans ClientServiceImpl avant la suppression,
     * ce qui les remet à disposition pour un autre client.
     */
    @OneToMany(mappedBy = "client")
    private List<Decodeur> decodeurs;

    public Client() {}
}