package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.ChaineRequestDTO;
import com.uqtr.decodeurtr.dto.AssignDecoderResponseDTO;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant toutes les opérations sur les décodeurs.
 *
 * Regroupe trois catégories d'endpoints :
 * - Opérations physiques sur le décodeur via le simulateur externe
 *   (état, redémarrage, réinitialisation, extinction)
 * - Gestion de l'assignation des décodeurs aux clients
 * - Gestion des chaînes de télévision accessibles sur un décodeur
 *
 * Toutes les routes sont préfixées par /api/decoder.
 * La politique CORS est gérée globalement dans SecurityConfig.
 */
@RestController
@RequestMapping("/api/decoder")
public class DecodeurController {

    private final DecodeurService decodeurService;

    /**
     * Injection du service décodeur par constructeur.
     *
     * @param decodeurService service gérant la logique métier des décodeurs
     */
    public DecodeurController(DecodeurService decodeurService) {
        this.decodeurService = decodeurService;
    }

    // ── Opérations physiques (simulateur) ────────────────────────────────────

    /**
     * Retourne l'état temps réel d'un décodeur depuis le simulateur
     * et met à jour sa valeur en base.
     *
     * GET /api/decoder/getEtat/{idDecodeur}
     */
    @GetMapping("/getEtat/{idDecodeur}")
    public ResponseEntity getEtatDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.getEtatDecodeur(idDecodeur));
    }

    /**
     * Envoie un ordre de redémarrage au décodeur.
     * Le retour en ligne est détecté par polling côté client (toutes les 5s).
     *
     * PUT /api/decoder/restart/{idDecodeur}
     */
    @PutMapping("/restart/{idDecodeur}")
    public ResponseEntity restartDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.restartDecoder(idDecodeur));
    }

    /**
     * Réinitialise le mot de passe d'un décodeur via le simulateur.
     *
     * PUT /api/decoder/reinit/{idDecodeur}
     */
    @PutMapping("/reinit/{idDecodeur}")
    public ResponseEntity reinitDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.reinitDecoder(idDecodeur));
    }

    /**
     * Éteint un décodeur via le simulateur.
     *
     * PUT /api/decoder/shutdown/{idDecodeur}
     */
    @PutMapping("/shutdown/{idDecodeur}")
    public ResponseEntity shutdownDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.shutdownDecoder(idDecodeur));
    }

    // ── Gestion de l'assignation ──────────────────────────────────────────────

    /**
     * Retourne tous les décodeurs actuellement assignés à un client.
     *
     * GET /api/decoder/assigned
     */
    @GetMapping("/assigned")
    public ResponseEntity getAllAssignedDecoders() {
        return ResponseEntity.ok(decodeurService.getAllAssignedDecoders());
    }

    /**
     * Retourne tous les décodeurs non assignés, disponibles pour attribution.
     *
     * GET /api/decoder/available
     */
    @GetMapping("/available")
    public ResponseEntity getAllUnassignedDecoders() {
        return ResponseEntity.ok(decodeurService.getAllAvailableDecoders());
    }

    /**
     * Assigne un décodeur disponible à un client.
     *
     * PUT /api/decoder/assign/{decodeurId}/assigner/{clientId}
     */
    @PutMapping("/assign/{decodeurId}/assigner/{clientId}")
    public ResponseEntity<AssignDecoderResponseDTO> assignerDecodeur(
            @PathVariable Long decodeurId,
            @PathVariable Long clientId) {
        return ResponseEntity.ok(decodeurService.assignDecoderToClient(clientId, decodeurId));
    }

    /**
     * Retourne les décodeurs assignés à un client spécifique,
     * avec synchronisation temps réel depuis le simulateur.
     *
     * GET /api/decoder/client/{clientId}
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity getDecoderByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(decodeurService.getAllDecodersByClient(clientId));
    }

    /**
     * Désassigne un décodeur de son client sans le supprimer.
     *
     * PUT /api/decoder/retirer/{idDecodeur}
     */
    @PutMapping("/retirer/{idDecodeur}")
    public ResponseEntity retirerDecodeur(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.removeDecoder(idDecodeur));
    }

    // ── Gestion des chaînes ───────────────────────────────────────────────────

    /**
     * Retourne la liste des chaînes associées à un décodeur.
     *
     * GET /api/decoder/getChaines/{idDecodeur}
     */
    @GetMapping("/getChaines/{idDecodeur}")
    public ResponseEntity getChaines(@PathVariable Long idDecodeur) {
        return ResponseEntity.ok(decodeurService.getChaines(idDecodeur));
    }

    /**
     * Ajoute une chaîne au décodeur si elle n'existe pas déjà.
     *
     * POST /api/decoder/ajouterChaine
     */
    @PostMapping("/ajouterChaine")
    public ResponseEntity ajouterChaine(@RequestBody ChaineRequestDTO request) {
        return ResponseEntity.ok(decodeurService.ajouterChaine(request));
    }

    /**
     * Retire une chaîne du décodeur si elle est présente.
     *
     * POST /api/decoder/retirerChaine
     */
    @PostMapping("/retirerChaine")
    public ResponseEntity retirerChaine(@RequestBody ChaineRequestDTO request) {
        return ResponseEntity.ok(decodeurService.retirerChaine(request));
    }
}