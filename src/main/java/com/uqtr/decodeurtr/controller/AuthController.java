package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.service.auth.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST gérant l'authentification et la récupération de mot de passe.
 *
 * Regroupe deux fonctionnalités distinctes liées à l'accès au système :
 * - La connexion d'un utilisateur (CU1)
 * - La réinitialisation de mot de passe via question secrète (réservée aux ADMIN)
 *
 * Toutes les routes sont préfixées par /api.
 * La politique CORS est gérée globalement dans SecurityConfig.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * Injection du service d'authentification par constructeur.
     *
     * @param authService service gérant la logique d'authentification
     *                    et de récupération de mot de passe
     */
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    /**
     * Authentifie un utilisateur à partir de son identifiant et de son mot de passe.
     *
     * Le mot de passe fourni est comparé au hash BCrypt stocké en base via
     * passwordEncoder.matches(). En cas de succès, le rôle et l'identifiant
     * sont retournés au frontend pour orienter la navigation.
     *
     * POST /api/login
     *
     * @param dto les credentials de connexion (identifiantConnexion, motDePasse)
     * @return 200 OK avec le résultat de l'authentification et le rôle utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity authentifier(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.authentifier(dto));
    }

    /**
     * Retourne la question secrète associée à un identifiant de connexion.
     *
     * Cette étape initie le processus de récupération de mot de passe.
     * Pour des raisons de sécurité, le message retourné est identique
     * que l'identifiant existe ou non, afin d'éviter l'énumération de comptes.
     * Cette fonctionnalité est réservée aux comptes de rôle ADMIN.
     *
     * GET /api/mot-de-passe/question/{identifiant}
     *
     * @param identifiant l'identifiant de connexion de l'utilisateur
     * @return 200 OK avec la question secrète, ou un message d'erreur générique
     */
    @GetMapping("/mot-de-passe/question/{identifiant}")
    public ResponseEntity getQuestion(@PathVariable String identifiant) {
        return ResponseEntity.ok(authService.getQuestionSecrete(identifiant));
    }

    /**
     * Réinitialise le mot de passe après vérification de la réponse secrète.
     *
     * La réponse fournie est comparée au hash BCrypt de la réponse stockée,
     * après normalisation (minuscules et suppression des espaces superflus).
     * Le nouveau mot de passe est hashé avant d'être persisté en base.
     *
     * POST /api/mot-de-passe/reset
     *
     * @param dto les données de réinitialisation (identifiant, réponse secrète,
     *            nouveau mot de passe, confirmation)
     * @return 200 OK avec un message de succès ou d'erreur
     */
    @PostMapping("/mot-de-passe/reset")
    public ResponseEntity resetMotDePasse(@RequestBody ResetMotDePasseRequestDTO dto) {
        return ResponseEntity.ok(authService.resetMotDePasse(dto));
    }
}