package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.entity.Client;
import com.uqtr.decodeurtr.entity.Decodeur;
import com.uqtr.decodeurtr.entity.Utilisateur;
import com.uqtr.decodeurtr.repository.ClientRepository;
import com.uqtr.decodeurtr.repository.UtilisateurRepository;
import com.uqtr.decodeurtr.service.decodeur.DecodeurService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService{

    private UtilisateurRepository utilisateurRepository;
    private ClientRepository clientRepository;
    private DecodeurService decodeurService;

    public ClientServiceImpl(ClientRepository clientRepository,UtilisateurRepository utilisateurRepository,DecodeurService decodeurService) {
        this.clientRepository = clientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.decodeurService = decodeurService;
    }

    @Transactional
    @Override
    public CreateClientResponseDTO createClient(CreateClientRequestDTO request) {

        // 1. Validation de présence des données
        if (request.getNomClient() == null || request.getNomClient().isBlank()
                || request.getAdresse() == null || request.getAdresse().isBlank()
                || request.getMotDePasse() == null || request.getMotDePasse().isBlank()
                || request.getIdentifiantConnexion() == null || request.getIdentifiantConnexion().isBlank()) {
            throw new RuntimeException("Tous les champs sont obligatoires.");
        }

        // 2. VÉRIFICATION D'UNICITÉ
        // L'identifiant est le pivot : s'il existe en tant qu'admin ou client, on bloque.
        if (utilisateurRepository.existsByIdentifiantConnexion(request.getIdentifiantConnexion())) {
            throw new RuntimeException("Erreur : Cet identifiant est déjà utilisé par un autre utilisateur.");
        }

        // 3. Création du CLIENT
        Client client = new Client();
        client.setNomClient(request.getNomClient());
        client.setAdresse(request.getAdresse());

        // 4. Création de l'UTILISATEUR (Lié au client)
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdentifiantConnexion(request.getIdentifiantConnexion());
        utilisateur.setMotDePasse(request.getMotDePasse());
        utilisateur.setRole("CLIENT");
        utilisateur.setActif(true);

        // 5. Établir le lien bidirectionnel
        // Très important : l'utilisateur pointe vers le client ET le client vers l'utilisateur
        utilisateur.setClient(client);
        client.setUtilisateur(utilisateur);

        // 6. Sauvegarde
        // Grâce au CascadeType.ALL sur la relation dans l'entité Client,
        // sauvegarder le client créera automatiquement l'entrée dans la table utilisateur.
        clientRepository.save(client);

        return new CreateClientResponseDTO("Client créé avec succès.", true);
    }

    public ClientDashboardDTO getDashboardData(String identifiant) {
        Utilisateur user = utilisateurRepository.findByIdentifiantConnexion(identifiant)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Client c = user.getClient();
        if (c == null) {
            throw new RuntimeException("Profil client introuvable");
        }

        List<AllDecodersByClientResponseDTO> decodersStatus =
                decodeurService.getAllDecodersByClient(c.getId());

        List<ClientDashboardDTO.DecodeurInfo> infos = decodersStatus.stream()
                .map(d -> new ClientDashboardDTO.DecodeurInfo(
                        d.getId(),
                        d.getAdresseIp(),
                        d.getEtat(),
                        d.getLastRestart(),
                        d.getLastReinit()
                ))
                .toList();

        ClientDashboardDTO dto = new ClientDashboardDTO();
        dto.setNomClient(c.getNomClient());
        dto.setDecodeurs(infos);
        dto.setNbTotal(infos.size());
        dto.setNbActifs(infos.stream()
                .filter(dec -> "EN_LIGNE".equals(dec.getEtat()))
                .count());

        return dto;
    }
    @Override
    public List<AllClientsResponseDTO> getAllClients() {
        var clients = clientRepository.findAll();
        return clients.stream()
                .map(client -> {
                    var decodeurIds = client.getDecodeurs() == null ? List.<Long>of() :
                            client.getDecodeurs().stream()
                                    .map(decodeur -> decodeur.getId())
                                    .toList();
                    return new AllClientsResponseDTO(
                            client.getId(),
                            client.getNomClient(),
                            client.getAdresse(),
                            decodeurIds
                    );
                })
                .toList();
    }

    @Override
    public DeleteClientResponse deleteClient(Long idClient) {
        try {
            clientRepository.deleteById(idClient);
            return new DeleteClientResponse(idClient,true, "Client supprimé avec succès.");
        } catch (EmptyResultDataAccessException e) {
            return new DeleteClientResponse(idClient,false, "Client introuvable.");
        } catch (Exception e) {
            return new DeleteClientResponse(idClient,false, "Erreur lors de la suppression du client.");
        }
    }
}
