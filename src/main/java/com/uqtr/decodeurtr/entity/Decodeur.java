package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité représentant un décodeur physique du câblodistributeur.
 *
 * Chaque décodeur est identifié par son adresse IP et peut être assigné
 * à un client. Il maintient en base son état courant ainsi que les dates
 * de ses dernières opérations, synchronisées avec le simulateur externe.
 * La gestion des chaînes de télévision accessibles est déléguée à
 * l'objet embarqué ModuleContenu.
 *
 * Cette classe est mappée à la table "decodeur" en base de données.
 */
@Entity
@Getter @Setter
@Table(name = "decodeur")
public class Decodeur {

    /**
     * Identifiant unique généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_decodeur")
    private Long id;

    /**
     * Adresse IP du décodeur sur le réseau du câblodistributeur.
     * Doit être unique — elle sert de clé pour communiquer avec le simulateur.
     * La plage utilisée dans ce projet est 127.0.10.1 à 127.0.10.12.
     */
    @Column(name = "adresse_ip", nullable = false, unique = true, length = 50)
    private String adresseIp;

    /**
     * État courant du décodeur, stocké sous forme de chaîne en base.
     * Les valeurs possibles sont définies dans l'énumération EtatDecodeur :
     * EN_LIGNE ou HORS_LIGNE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private EtatDecodeur etat;

    /**
     * Date et heure du dernier redémarrage du décodeur.
     * Mise à jour lors de chaque synchronisation avec le simulateur.
     */
    @Column(name = "last_restart")
    private LocalDateTime lastRestart;

    /**
     * Date et heure de la dernière réinitialisation du mot de passe du décodeur.
     * Mise à jour lors de chaque synchronisation avec le simulateur.
     */
    @Column(name = "last_reinit")
    private LocalDateTime lastReinit;

    /**
     * Client auquel ce décodeur est actuellement assigné.
     * La valeur est null si le décodeur est disponible (non assigné).
     */
    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    /**
     * Module de gestion des chaînes de télévision accessibles sur ce décodeur.
     * Embarqué directement dans la table "decodeur" via @Embedded —
     * les chaînes sont stockées dans la table associée "decodeur_chaines".
     */
    @Embedded
    private ModuleContenu moduleContenu = new ModuleContenu();

    /**
     * Ajoute une chaîne au module de contenu de ce décodeur.
     * Délègue la logique de validation (duplication) à ModuleContenu.
     *
     * @param chaine le nom de la chaîne à ajouter
     * @return true si la chaîne a été ajoutée, false si elle existait déjà
     */
    public boolean ajouterChaine(String chaine) {
        return moduleContenu.ajouterChaine(chaine);
    }

    /**
     * Retire une chaîne du module de contenu de ce décodeur.
     * Délègue la logique de validation (existence) à ModuleContenu.
     *
     * @param chaine le nom de la chaîne à retirer
     * @return true si la chaîne a été retirée, false si elle était absente
     */
    public boolean supprimerChaine(String chaine) {
        return moduleContenu.supprimerChaine(chaine);
    }
}