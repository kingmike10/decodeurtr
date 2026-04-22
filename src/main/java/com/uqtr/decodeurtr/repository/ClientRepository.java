package com.uqtr.decodeurtr.repository;

import com.uqtr.decodeurtr.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entité Client.
 *
 * Fournit les opérations CRUD standard via JpaRepository :
 * findById, findAll, save, delete, etc.
 * Aucune requête personnalisée n'est nécessaire pour l'instant —
 * les besoins actuels sont couverts par les méthodes héritées.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}