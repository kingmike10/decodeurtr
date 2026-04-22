package com.uqtr.decodeurtr.repository;

import com.uqtr.decodeurtr.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Utilisateur.
 *
 * Les méthodes de requête sont dérivées automatiquement par Spring Data
 * à partir de leur nom, sans nécessiter d'écriture de SQL ou JPQL.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son identifiant de connexion.
     * Utilisé lors de l'authentification et de la récupération de mot de passe.
     */
    Optional<Utilisateur> findByIdentifiantConnexion(String identifiantConnexion);

    /**
     * Vérifie l'existence d'un identifiant sans charger l'entité complète.
     * Utilisé lors de la création d'un compte pour garantir l'unicité.
     */
    boolean existsByIdentifiantConnexion(String identifiantConnexion);
}