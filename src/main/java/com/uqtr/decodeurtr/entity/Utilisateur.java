package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant un compte utilisateur du système.
 *
 * Deux rôles coexistent : ADMIN (accès complet) et CLIENT (accès limité
 * à ses propres décodeurs). Un compte CLIENT est toujours lié à un client
 * institutionnel ; un compte ADMIN n'a pas de client associé.
 */
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

    /** Stocké sous forme de hash BCrypt — jamais en clair. */
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    /** Valeurs possibles : "ADMIN" ou "CLIENT". */
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    /** Un compte inactif est refusé à la connexion sans être supprimé. */
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    /** Question secrète pour la récupération de mot de passe (ADMIN uniquement). */
    @Column(name = "question_secrete")
    private String questionSecrete;

    /** Réponse hashée avec BCrypt, normalisée en minuscules avant hashage. */
    @Column(name = "reponse_secrete")
    private String reponseSecrete;

    /** Null pour un compte ADMIN, obligatoirement renseigné pour un CLIENT. */
    @OneToOne
    @JoinColumn(name = "id_client")
    private Client client;

    public Utilisateur() {}
}