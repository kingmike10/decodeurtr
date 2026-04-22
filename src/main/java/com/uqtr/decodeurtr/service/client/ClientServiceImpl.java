package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.entity.Client;
import com.uqtr.decodeurtr.entity.Decodeur;
import com.uqtr.decodeurtr.entity.Utilisateur;
import com.uqtr.decodeurtr.repository.ClientRepository;
import com.uqtr.decodeurtr.repository.DecodeurRepository;
import com.uqtr.decodeurtr.repository.UtilisateurRepository;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation de ClientService gérant le cycle de vie des clients.
 *
 * Responsabilités : création d'un client avec son compte utilisateur,
 * consultation de la liste et du tableau de bord, suppression avec
 * désassignation préalable des décodeurs.
 */
@Service
public class ClientServiceImpl implements ClientService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository      clientRepository;
    private final DecodeurRepository    decodeurRepository;
    private final DecodeurService       decodeurService;
    private final PasswordEncoder       passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository,
                             UtilisateurRepository utilisateurRepository,
                             DecodeurRepository decodeurRepository,
                             DecodeurService decodeurService,
                             PasswordEncoder passwordEncoder) {
        this.clientRepository      = clientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.decodeurRepository    = decodeurRepository;
        this.decodeurService       = decodeurService;
        this.passwordEncoder       = passwordEncoder;
    }

    /**
     * Crée un client et son compte utilisateur en une seule transaction.
     *
     * Le mot de passe est hashé avec BCrypt avant persistance.
     * La cascade CascadeType.ALL sur la relation Client → Utilisateur
     * garantit que les deux entités sont sauvegardées en un seul appel.
     */
    @Transactional
    @Override
    public CreateClientResponseDTO createClient(CreateClientRequestDTO request) {

        // Validation des champs obligatoires
        if (request.getNomClient() == null || request.getNomClient().isBlank()
                || request.getAdresse() == null || request.getAdresse().isBlank()
                || request.getMotDePasse() == null || request.getMotDePasse().isBlank()
                || request.getIdentifiantConnexion() == null || request.getIdentifiantConnexion().isBlank()) {
            throw new RuntimeException("Tous les champs sont obligatoires.");
        }

        // Vérification de l'unicité de l'identifiant toutes tables confondues
        if (utilisateurRepository.existsByIdentifiantConnexion(request.getIdentifiantConnexion())) {
            throw new RuntimeException("Cet identifiant est déjà utilisé.");
        }

        Client client = new Client();
        client.setNomClient(request.getNomClient());
        client.setAdresse(request.getAdresse());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdentifiantConnexion(request.getIdentifiantConnexion());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("CLIENT");
        utilisateur.setActif(true);

        // Établissement du lien bidirectionnel avant persistance
        utilisateur.setClient(client);
        client.setUtilisateur(utilisateur);
        clientRepository.save(client);

        return new CreateClientResponseDTO("Client créé avec succès.", true);
    }

    /**
     * Construit le tableau de bord d'un client à partir de son identifiant.
     *
     * L'état de chaque décodeur est synchronisé en temps réel avec le
     * simulateur externe via DecodeurService.getAllDecodersByClient().
     */
    @Override
    public ClientDashboardDTO getDashboardData(String identifiant) {
        Utilisateur user = utilisateurRepository.findByIdentifiantConnexion(identifiant)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Client c = user.getClient();
        if (c == null) throw new RuntimeException("Profil client introuvable");

        List<AllDecodersByClientResponseDTO> decodersStatus =
                decodeurService.getAllDecodersByClient(c.getId());

        List<ClientDashboardDTO.DecodeurInfo> infos = decodersStatus.stream()
                .map(d -> new ClientDashboardDTO.DecodeurInfo(
                        d.getId(), d.getAdresseIp(), d.getEtat(),
                        d.getLastRestart(), d.getLastReinit()))
                .toList();

        ClientDashboardDTO dto = new ClientDashboardDTO();
        dto.setNomClient(c.getNomClient());
        dto.setDecodeurs(infos);
        dto.setNbTotal(infos.size());
        dto.setNbActifs(infos.stream().filter(d -> "EN_LIGNE".equals(d.getEtat())).count());
        return dto;
    }

    /**
     * Retourne la liste de tous les clients avec leur identifiant de connexion
     * et les identifiants de leurs décodeurs assignés.
     */
    @Override
    public List<AllClientsResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> new AllClientsResponseDTO(
                        client.getId(),
                        client.getNomClient(),
                        client.getAdresse(),
                        client.getUtilisateur() != null
                                ? client.getUtilisateur().getIdentifiantConnexion() : null,
                        client.getDecodeurs() == null ? List.of() :
                                client.getDecodeurs().stream().map(Decodeur::getId).toList()
                ))
                .toList();
    }

    /**
     * Supprime un client en deux étapes dans la même transaction :
     * 1. Désassignation de tous ses décodeurs (setClient à null)
     * 2. Suppression du client (cascade vers son compte utilisateur)
     *
     * Les décodeurs ne sont pas supprimés — ils redeviennent disponibles.
     */
    @Transactional
    @Override
    public DeleteClientResponse deleteClient(Long idClient) {
        try {
            Client client = clientRepository.findById(idClient)
                    .orElseThrow(() -> new RuntimeException("Client introuvable."));

            List<Decodeur> decodeurs = decodeurRepository.findByClientId(idClient);
            for (Decodeur d : decodeurs) {
                d.setClient(null);
                decodeurRepository.save(d);
            }

            clientRepository.delete(client);
            return new DeleteClientResponse(idClient, true,
                    "Client supprimé. " + decodeurs.size() + " décodeur(s) remis disponibles.");

        } catch (RuntimeException e) {
            return new DeleteClientResponse(idClient, false, e.getMessage());
        } catch (Exception e) {
            return new DeleteClientResponse(idClient, false, "Erreur lors de la suppression.");
        }
    }
}