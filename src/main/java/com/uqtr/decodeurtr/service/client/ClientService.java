package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.*;

import java.util.List;

/**
 * Contrat du service de gestion des clients.
 *
 * Couvre le cycle de vie complet d'un client : création avec son compte
 * utilisateur associé, consultation de la liste et du tableau de bord,
 * et suppression avec désassignation préalable des décodeurs.
 */
public interface ClientService {

    /** Crée un client et son compte utilisateur avec le mot de passe hashé. */
    CreateClientResponseDTO createClient(CreateClientRequestDTO createClientRequestDTO);

    /** Retourne la liste de tous les clients avec leurs décodeurs assignés. */
    List<AllClientsResponseDTO> getAllClients();

    /**
     * Supprime un client après avoir désassigné ses décodeurs.
     * Les décodeurs redeviennent disponibles pour attribution.
     */
    DeleteClientResponse deleteClient(Long idClient);

    /**
     * Retourne les données du tableau de bord d'un client identifié
     * par son identifiant de connexion, avec synchronisation temps réel
     * de l'état de ses décodeurs depuis le simulateur.
     */
    ClientDashboardDTO getDashboardData(String identifiant);
}