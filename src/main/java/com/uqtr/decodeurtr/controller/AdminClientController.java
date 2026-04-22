package com.uqtr.decodeurtr.controller;

import com.uqtr.decodeurtr.dto.AdminClientDecodeursDTO;
import com.uqtr.decodeurtr.dto.CreateClientRequestDTO;
import com.uqtr.decodeurtr.service.client.ClientService;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST réservé aux opérations administratives sur les clients.
 *
 * Expose les endpoints de gestion du cycle de vie d'un client :
 * création, consultation, et suppression. La gestion des décodeurs
 * associés à un client est également accessible depuis ce contrôleur.
 *
 * Toutes les routes sont préfixées par /api/admin/clients.
 * La politique CORS est gérée globalement dans SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin/clients")
public class AdminClientController {

    private final ClientService clientService;
    private final DecodeurService decodeurService;

    /**
     * Injection des services par constructeur.
     * Cette approche est privilégiée car elle garantit l'immutabilité
     * des dépendances et facilite les tests unitaires.
     *
     * @param clientService   service de gestion des clients
     * @param decodeurService service de gestion des décodeurs
     */
    public AdminClientController(ClientService clientService, DecodeurService decodeurService) {
        this.clientService = clientService;
        this.decodeurService = decodeurService;
    }

    /**
     * Crée un nouveau client ainsi que son compte utilisateur associé.
     *
     * POST /api/admin/clients/create
     *
     * @param request les informations du client à créer (nom, adresse,
     *                identifiant de connexion, mot de passe)
     * @return 200 OK avec un message de confirmation
     */
    @PostMapping("/create")
    public ResponseEntity createClient(@RequestBody CreateClientRequestDTO request) {
        return ResponseEntity.ok(clientService.createClient(request));
    }

    /**
     * Retourne la liste des décodeurs assignés à un client donné,
     * ainsi que les chaînes associées à chacun d'eux.
     *
     * GET /api/admin/clients/{idClient}/decodeurs
     *
     * @param idClient identifiant unique du client
     * @return les décodeurs du client avec leurs chaînes
     */
    @GetMapping("/{idClient}/decodeurs")
    public AdminClientDecodeursDTO getDecodeursByClient(@PathVariable Long idClient) {
        return decodeurService.getDecodeursByClient(idClient);
    }

    /**
     * Retourne la liste complète de tous les clients enregistrés.
     *
     * GET /api/admin/clients/all
     *
     * @return 200 OK avec la liste des clients
     */
    @GetMapping("/all")
    public ResponseEntity getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    /**
     * Supprime un client et désassigne ses décodeurs avant la suppression.
     * Les décodeurs ne sont pas supprimés — ils redeviennent disponibles.
     *
     * DELETE /api/admin/clients/delete/{idClient}
     *
     * @param idClient identifiant unique du client à supprimer
     * @return 200 OK avec un message indiquant le nombre de décodeurs libérés
     */
    @DeleteMapping("/delete/{idClient}")
    public ResponseEntity deleteClient(@PathVariable Long idClient) {
        return ResponseEntity.ok(clientService.deleteClient(idClient));
    }
}