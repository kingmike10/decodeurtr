package com.uqtr.decodeurtr.service.auth;

import com.uqtr.decodeurtr.dto.*;

/**
 * Contrat du service d'authentification et de récupération de mot de passe.
 *
 * Couvre deux responsabilités liées à l'accès au système :
 * - La vérification des credentials lors de la connexion
 * - La réinitialisation de mot de passe via question secrète (ADMIN uniquement)
 */
public interface AuthService {

    /** Vérifie les credentials et retourne le rôle de l'utilisateur si valides. */
    LoginResponseDTO authentifier(LoginRequestDTO loginRequestDTO);

    /**
     * Retourne la question secrète associée à un identifiant.
     * Réservé aux comptes ADMIN — retourne un message générique en cas d'échec
     * pour éviter l'énumération de comptes.
     */
    GetQuestionResponseDTO getQuestionSecrete(String identifiantConnexion);

    /**
     * Réinitialise le mot de passe après vérification de la réponse secrète.
     * La réponse est comparée au hash BCrypt stocké après normalisation.
     */
    ResetMotDePasseResponseDTO resetMotDePasse(ResetMotDePasseRequestDTO request);
}