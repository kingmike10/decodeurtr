package com.uqtr.decodeurtr.service.decodeur;

import com.uqtr.decodeurtr.dto.*;

import com.uqtr.decodeurtr.entity.Client;
import com.uqtr.decodeurtr.entity.Decodeur;
import com.uqtr.decodeurtr.entity.EtatDecodeur;
import com.uqtr.decodeurtr.repository.ClientRepository;
import com.uqtr.decodeurtr.repository.DecodeurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DecodeurServiceImpl implements DecodeurService {


    private final SimulateurDecodeurService simulateur;
    private final com.uqtr.decodeurtr.repository.DecodeurRepository decodeurRepository;
    private final ClientRepository clientRepository;


    public DecodeurServiceImpl(SimulateurDecodeurService simulateur, DecodeurRepository decodeurRepository, ClientRepository clientRepository) {
        this.simulateur = simulateur;
        this.decodeurRepository = decodeurRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public AdminClientDecodeursDTO getDecodeursByClient(Long idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        List<Decodeur> decodeurs = decodeurRepository.findByClientId(idClient);

        List<DecodeurAdminInfoDTO> decodeurDtos = decodeurs.stream()
                .map(decodeur -> {
                    List<String> chaines = new ArrayList<>();

                    if (decodeur.getModuleContenu() != null && decodeur.getModuleContenu().getChaines() != null) {
                        chaines = decodeur.getModuleContenu().getChaines();
                    }

                    return new DecodeurAdminInfoDTO(
                            decodeur.getId(),
                            decodeur.getAdresseIp(),
                            decodeur.getEtat().name(),
                            chaines
                    );
                })
                .toList();

        return new AdminClientDecodeursDTO(
                client.getId(),
                client.getNomClient(),
                decodeurDtos
        );
    }


    @Override
    public List<DecoderAssignedResponseDTO> getAllAssignedDecoders() {
        List<Decodeur> decodeursAttribues = decodeurRepository.findByClientIsNotNull();

        return decodeursAttribues.stream()
                .map(decodeur -> new DecoderAssignedResponseDTO(
                        decodeur.getId(),
                        decodeur.getAdresseIp(),
                        decodeur.getEtat().name(),
                        decodeur.getClient().getId(),
                        decodeur.getClient().getNomClient()
                ))
                .toList();
    }

    @Override
    public List<DecoderAvailableResponseDTO> getAllAvailableDecoders() {
        List<Decodeur> decodeursDisponibles = decodeurRepository.findByClientIsNull();

        return decodeursDisponibles.stream()
                .map(decodeur -> new DecoderAvailableResponseDTO(
                        decodeur.getId(),
                        decodeur.getAdresseIp(),
                        decodeur.getEtat().name()
                ))
                .toList();
    }

    @Override
    public RemoveDecoderResponseDTO removeDecoder(Long decoderId) {

        Decodeur decodeur = decodeurRepository.findById(decoderId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        Client client = decodeur.getClient();
        decodeur.setClient(null);
        decodeurRepository.save(decodeur);

        return new RemoveDecoderResponseDTO(decoderId,client.getId(),
                "Décodeur retiré du client avec succès",true
        );
    }

    @Override
    public EtatDecodeurResponseDTO getEtatDecodeur(Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        SimulateurInfoResponseDTO response = simulateur.obtenirEtat(decodeur.getAdresseIp());

        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("Erreur simulateur : " + response.getMessage());
        }

        EtatDecodeur etat = EtatDecodeur.fromSimulateurState(response.getState());
        decodeur.setEtat(etat);
        decodeurRepository.save(decodeur);

        return new EtatDecodeurResponseDTO(
                decodeur.getId(),
                decodeur.getAdresseIp(),
                etat.name(),
                response.getLastRestart(),
                response.getLastReinit()
        );
    }

    @Override
    public OperationDecodeurResponseDTO restartDecoder(Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        // 1. Envoyer l'ordre de reset (le simulateur commence ses 10-30s)
        SimulateurResetResponseDTO response = simulateur.resetDecodeur(decodeur.getAdresseIp());

        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            return new OperationDecodeurResponseDTO("Erreur simulateur", false);
        }

        // 2. On met l'état à HORS_LIGNE en base car il est en train de redémarrer
        decodeur.setEtat(EtatDecodeur.HORS_LIGNE);
        decodeurRepository.save(decodeur);

        return new OperationDecodeurResponseDTO("Redémarrage en cours (10-30s)...", true);
    }

    @Override
    public GetChainesResponseDTO getChaines(Long idDecodeur) {
        Decodeur decodeur = decodeurRepository.findById(idDecodeur)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        if (decodeur.getModuleContenu() == null) {
            return new GetChainesResponseDTO(idDecodeur, new ArrayList<>());
        }

        List<String> chaines = decodeur.getModuleContenu().getChaines();

        if (chaines == null) {
            chaines = new ArrayList<>();
        }

        return new GetChainesResponseDTO(idDecodeur, chaines);
    }


    @Override
    public OperationDecodeurResponseDTO ajouterChaine(ChaineRequestDTO request) {

        Decodeur decodeur = decodeurRepository.findById(request.getIdDecodeur())
                .orElse(null);

        if (decodeur == null) {
            return new OperationDecodeurResponseDTO("Décodeur introuvable", false);
        }

        boolean added = decodeur.ajouterChaine(request.getChaine());

        if (!added) {
            return new OperationDecodeurResponseDTO("La chaîne existe déjà", false);
        }

        try {
            decodeurRepository.save(decodeur);
        } catch (Exception e) {
            return new OperationDecodeurResponseDTO("Échec lors de la sauvegarde", false);
        }

        return new OperationDecodeurResponseDTO("Chaîne ajoutée avec succès", true);
    }

    @Override
    public OperationDecodeurResponseDTO retirerChaine(ChaineRequestDTO request) {

        Decodeur decodeur = decodeurRepository.findById(request.getIdDecodeur())
                .orElse(null);

        if (decodeur == null) {
            return new OperationDecodeurResponseDTO("Décodeur introuvable", false);
        }

        boolean removed = decodeur.supprimerChaine(request.getChaine());

        if (!removed) {
            return new OperationDecodeurResponseDTO("La chaîne n'existe pas", false);
        }

        try {
            decodeurRepository.save(decodeur);
        } catch (Exception e) {
            return new OperationDecodeurResponseDTO("Échec lors de la sauvegarde", false);
        }

        return new OperationDecodeurResponseDTO("Chaîne retirée avec succès", true);
    }


    @Transactional
    @Override
    public List<AllDecodersByClientResponseDTO> getAllDecodersByClient(Long idClient) {
        // 1. On récupère les décodeurs du client en base locale
        List<Decodeur> decodeurs = decodeurRepository.findByClientIsNotNull().stream()
                .filter(d -> d.getClient().getId().equals(idClient))
                .toList();

        DateTimeFormatter profFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 2. Pour chaque décodeur, on va chercher la "vérité" chez le simulateur
        return decodeurs.stream().map(decodeur -> {
            try {
                // APPEL AU SIMULATEUR
                SimulateurInfoResponseDTO infoReelle = simulateur.obtenirEtat(decodeur.getAdresseIp());

                if (infoReelle != null && infoReelle.getLastRestart() != null) {

                    String state = infoReelle.getState();
                    decodeur.setEtat(EtatDecodeur.fromSimulateurState(state)); // On met à jour l'état en fonction du simulateur
                    // On utilise le formatter pour lire la date avec l'espace et les secondes
                    LocalDateTime restart = LocalDateTime.parse(infoReelle.getLastRestart(), profFormatter);
                    LocalDateTime reinit = LocalDateTime.parse(infoReelle.getLastReinit(), profFormatter);
                        decodeur.setLastRestart(restart);
                        decodeur.setLastReinit(reinit);

                    decodeurRepository.save(decodeur); // On écrase les anciennes dates SQL
                }
            } catch (Exception e) {
                // Si le simulateur ne répond pas, on garde les dates de la BD (on ne crash pas)
                System.err.println("Erreur synchro pour " + decodeur.getAdresseIp() + " : " + e.getMessage());
            }

            // 3. On retourne le DTO avec les dates fraîches
            return new AllDecodersByClientResponseDTO(
                    decodeur.getId(),
                    decodeur.getAdresseIp(),
                    decodeur.getEtat().name(),
                    decodeur.getLastRestart() != null ? decodeur.getLastRestart().format(profFormatter).toString() : "N/A",
                    decodeur.getLastReinit() != null ? decodeur.getLastReinit().format(profFormatter).toString() : "N/A"
            );
        }).toList();
    }

    @Override
    public AssignDecoderResponseDTO assignDecoderToClient(Long clientId,  Long decodeurId) {

        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        if (decodeur.getClient() != null) {
            throw new RuntimeException("Ce décodeur est déjà attribué");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        decodeur.setClient(client);
        decodeurRepository.save(decodeur);

        return new AssignDecoderResponseDTO(
                client.getId(), decodeur.getId(),true,
                "Décodeur assigné avec succès"
        );
    }



}
