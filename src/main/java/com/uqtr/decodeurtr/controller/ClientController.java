package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.ClientDashboardDTO;
import com.uqtr.decodeurtr.service.client.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les fonctionnalités accessibles aux clients.
 *
 * Contrairement à AdminClientController qui est réservé à l'administration,
 * ce contrôleur répond aux requêtes émises depuis la vue client (vueClient.html).
 * Un client ne peut accéder qu'aux données associées à son propre identifiant.
 *
 * Toutes les routes sont préfixées par /api/clients.
 * La politique CORS est gérée globalement dans SecurityConfig.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    /**
     * Injection du service client par constructeur.
     *
     * @param clientService service gérant la logique métier côté client
     */
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Retourne les données du tableau de bord pour un client authentifié.
     *
     * Récupère la liste des décodeurs assignés au client identifié par son
     * identifiant de connexion, en synchronisant leur état avec le simulateur
     * en temps réel. Les compteurs de décodeurs actifs et total sont également
     * calculés et inclus dans la réponse.
     *
     * GET /api/clients/dashboard/{identifiant}
     *
     * @param identifiant l'identifiant de connexion du client, tel que stocké
     *                    dans le localStorage après authentification
     * @return 200 OK avec les données du dashboard,
     *         ou 404 Not Found si l'identifiant ne correspond à aucun compte
     */
    @GetMapping("/dashboard/{identifiant}")
    public ResponseEntity<ClientDashboardDTO> getDashboard(@PathVariable String identifiant) {
        try {
            ClientDashboardDTO dashboard = clientService.getDashboardData(identifiant);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}