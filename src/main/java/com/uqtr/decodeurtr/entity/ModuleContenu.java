package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Objet embarqué représentant le module de gestion du contenu d'un décodeur.
 *
 * Encapsule la liste des chaînes de télévision accessibles sur un décodeur donné,
 * ainsi que la logique métier associée (ajout, suppression, vérification).
 *
 * Étant annoté @Embeddable, cet objet n'a pas de table propre — ses données
 * sont stockées dans la table "decodeur_chaines", liée à la table "decodeur"
 * par une clé étrangère. Cette approche permet de traiter les chaînes comme
 * une responsabilité distincte tout en restant dans le même agrégat.
 */
@Embeddable
@Getter @Setter
public class ModuleContenu {

    /**
     * Liste des chaînes de télévision accessibles sur ce décodeur.
     *
     * Stockée dans la table "decodeur_chaines" via @ElementCollection.
     * Chaque entrée représente le nom d'une chaîne (ex : "RDS", "TVA").
     * Initialisée à une liste vide pour éviter les NullPointerException
     * lors des opérations d'ajout sur un décodeur nouvellement créé.
     */
    @ElementCollection
    @CollectionTable(
            name = "decodeur_chaines",
            joinColumns = @JoinColumn(name = "decodeur_id_decodeur")
    )
    @Column(name = "chaines")
    private List<String> chaines = new ArrayList<>();

    /**
     * Vérifie si une chaîne est déjà présente dans la liste.
     * Utilisée en interne avant tout ajout ou suppression.
     *
     * @param chaine le nom de la chaîne à rechercher
     * @return true si la chaîne est déjà enregistrée, false sinon
     */
    public boolean contientChaine(String chaine) {
        return chaines.contains(chaine);
    }

    /**
     * Ajoute une chaîne à la liste si elle n'est pas déjà présente.
     * Empêche les doublons sans lever d'exception — le résultat booléen
     * permet à l'appelant de distinguer un ajout réussi d'un doublon.
     *
     * @param chaine le nom de la chaîne à ajouter
     * @return true si la chaîne a été ajoutée, false si elle existait déjà
     */
    public boolean ajouterChaine(String chaine) {
        if (contientChaine(chaine)) {
            return false;
        }
        chaines.add(chaine);
        return true;
    }

    /**
     * Retire une chaîne de la liste si elle y est présente.
     * Empêche une suppression sur un élément absent sans lever d'exception —
     * le résultat booléen permet à l'appelant de distinguer une suppression
     * réussie d'une tentative sur une chaîne inexistante.
     *
     * @param chaine le nom de la chaîne à retirer
     * @return true si la chaîne a été retirée, false si elle était absente
     */
    public boolean supprimerChaine(String chaine) {
        if (!contientChaine(chaine)) {
            return false;
        }
        return chaines.remove(chaine);
    }
}