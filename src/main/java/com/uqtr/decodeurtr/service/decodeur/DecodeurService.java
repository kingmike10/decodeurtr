package com.uqtr.decodeurtr.service.decodeur;

import com.uqtr.decodeurtr.dto.*;

import java.util.List;

/**
 * Contrat du service de gestion des décodeurs.
 *
 * Regroupe deux catégories d'opérations :
 * - Opérations physiques sur le décodeur, exécutées via le simulateur externe
 *   (obtenir l'état, redémarrer, réinitialiser, éteindre)
 * - Opérations de gestion locale (assignation, retrait, chaînes)
 *   traitées directement en base de données
 */
public interface DecodeurService {

    //  Opérations physiques (simulateur)

    /** Retourne l'état temps réel d'un décodeur depuis le simulateur. */
    EtatDecodeurResponseDTO getEtatDecodeur(Long decodeurId);

    /** Redémarre un décodeur. Le retour en ligne est détecté par polling côté client. */
    OperationDecodeurResponseDTO restartDecoder(Long decodeurId);

    /** Réinitialise le mot de passe d'un décodeur via le simulateur. */
    OperationDecodeurResponseDTO reinitDecoder(Long decodeurId);

    /** Éteint un décodeur via le simulateur. */
    OperationDecodeurResponseDTO shutdownDecoder(Long decodeurId);

    // Gestion de l'assignation

    /** Retourne tous les décodeurs actuellement assignés à un client. */
    List<DecoderAssignedResponseDTO> getAllAssignedDecoders();

    /** Retourne tous les décodeurs non assignés, disponibles pour attribution. */
    List<DecoderAvailableResponseDTO> getAllAvailableDecoders();

    /** Assigne un décodeur disponible à un client. */
    AssignDecoderResponseDTO assignDecoderToClient(Long clientId, Long decodeurId);

    /** Désassigne un décodeur de son client sans le supprimer. */
    RemoveDecoderResponseDTO removeDecoder(Long decoderId);

    //  Consultation

    /**
     * Retourne les décodeurs d'un client avec synchronisation temps réel
     * depuis le simulateur. Utilisé pour le tableau de bord client.
     */
    List<AllDecodersByClientResponseDTO> getAllDecodersByClient(Long idClient);

    /**
     * Retourne les décodeurs d'un client avec leurs chaînes associées.
     * Utilisé pour la vue d'administration.
     */
    AdminClientDecodeursDTO getDecodeursByClient(Long idClient);

    // Gestion des chaînes

    /** Retourne la liste des chaînes associées à un décodeur. */
    GetChainesResponseDTO getChaines(Long idDecodeur);

    /** Ajoute une chaîne au décodeur si elle n'existe pas déjà. */
    OperationDecodeurResponseDTO ajouterChaine(ChaineRequestDTO request);

    /** Retire une chaîne du décodeur si elle est présente. */
    OperationDecodeurResponseDTO retirerChaine(ChaineRequestDTO request);
}