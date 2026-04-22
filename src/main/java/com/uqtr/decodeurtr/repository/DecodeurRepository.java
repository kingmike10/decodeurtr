package com.uqtr.decodeurtr.repository;

import com.uqtr.decodeurtr.entity.Decodeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Decodeur.
 *
 * Les méthodes de requête sont dérivées automatiquement par Spring Data
 * à partir de leur nom, sans nécessiter d'écriture de SQL ou JPQL.
 */
@Repository
public interface DecodeurRepository extends JpaRepository<Decodeur, Long> {

    /** Retourne tous les décodeurs actuellement assignés à un client. */
    List<Decodeur> findByClientIsNotNull();

    /** Retourne tous les décodeurs non assignés, disponibles pour attribution. */
    List<Decodeur> findByClientIsNull();

    /** Retourne les décodeurs assignés à un client spécifique. */
    List<Decodeur> findByClientId(Long idClient);
}