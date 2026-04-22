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

/**
 * Implémentation de DecodeurService gérant toutes les opérations sur les décodeurs.
 *
 * Les opérations physiques (redémarrage, extinction, réinitialisation, état)
 * sont déléguées à SimulateurDecodeurService qui communique avec l'API externe.
 * Les opérations de gestion (assignation, chaînes) sont traitées localement
 * et persistées en base de données.
 */
@Service
public class DecodeurServiceImpl implements DecodeurService {

    private final SimulateurDecodeurService simulateur;
    private final DecodeurRepository decodeurRepository;
    private final ClientRepository clientRepository;

    public DecodeurServiceImpl(SimulateurDecodeurService simulateur,
                               DecodeurRepository decodeurRepository,
                               ClientRepository clientRepository) {
        this.simulateur = simulateur;
        this.decodeurRepository = decodeurRepository;
        this.clientRepository = clientRepository;
    }

    /** Retourne les décodeurs d'un client avec leurs chaînes associées. */
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

        return new AdminClientDecodeursDTO(client.getId(), client.getNomClient(), decodeurDtos);
    }

    /** Retourne tous les décodeurs actuellement assignés à un client. */
    @Override
    public List<DecoderAssignedResponseDTO> getAllAssignedDecoders() {
        return decodeurRepository.findByClientIsNotNull().stream()
                .map(decodeur -> new DecoderAssignedResponseDTO(
                        decodeur.getId(),
                        decodeur.getAdresseIp(),
                        decodeur.getEtat().name(),
                        decodeur.getClient().getId(),
                        decodeur.getClient().getNomClient()
                ))
                .toList();
    }

    /** Retourne tous les décodeurs non assignés, disponibles pour attribution. */
    @Override
    public List<DecoderAvailableResponseDTO> getAllAvailableDecoders() {
        return decodeurRepository.findByClientIsNull().stream()
                .map(decodeur -> new DecoderAvailableResponseDTO(
                        decodeur.getId(),
                        decodeur.getAdresseIp(),
                        decodeur.getEtat().name()
                ))
                .toList();
    }

    /** Désassigne un décodeur de son client sans le supprimer. */
    @Override
    public RemoveDecoderResponseDTO removeDecoder(Long decoderId) {
        Decodeur decodeur = decodeurRepository.findById(decoderId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        Client client = decodeur.getClient();
        decodeur.setClient(null);
        decodeurRepository.save(decodeur);

        return new RemoveDecoderResponseDTO(decoderId, client.getId(),
                "Décodeur retiré du client avec succès", true);
    }

    /**
     * Interroge le simulateur pour obtenir l'état temps réel d'un décodeur
     * et met à jour sa valeur en base avant de retourner la réponse.
     */
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

    /**
     * Envoie l'ordre de redémarrage au simulateur et marque le décodeur
     * HORS_LIGNE en base. Le retour en ligne est détecté par polling côté client.
     */
    @Override
    public OperationDecodeurResponseDTO restartDecoder(Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        SimulateurResetResponseDTO response = simulateur.resetDecodeur(decodeur.getAdresseIp());

        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            return new OperationDecodeurResponseDTO("Erreur simulateur", false);
        }

        decodeur.setEtat(EtatDecodeur.HORS_LIGNE);
        decodeurRepository.save(decodeur);

        return new OperationDecodeurResponseDTO("Redémarrage en cours (10-30s)…", true);
    }

    /**
     * Réinitialise le mot de passe du décodeur via le simulateur
     * et met à jour la date de réinitialisation en base.
     */
    @Override
    public OperationDecodeurResponseDTO reinitDecoder(Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        SimulateurResetResponseDTO response = simulateur.reinitDecodeur(decodeur.getAdresseIp());

        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            return new OperationDecodeurResponseDTO("Erreur simulateur lors de la réinitialisation", false);
        }

        decodeur.setLastReinit(LocalDateTime.now());
        decodeurRepository.save(decodeur);

        return new OperationDecodeurResponseDTO("Mot de passe réinitialisé avec succès", true);
    }

    /** Éteint le décodeur via le simulateur et le marque HORS_LIGNE en base. */
    @Override
    public OperationDecodeurResponseDTO shutdownDecoder(Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        SimulateurResetResponseDTO response = simulateur.shutdownDecodeur(decodeur.getAdresseIp());

        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            return new OperationDecodeurResponseDTO("Erreur simulateur lors de l'extinction", false);
        }

        decodeur.setEtat(EtatDecodeur.HORS_LIGNE);
        decodeurRepository.save(decodeur);

        return new OperationDecodeurResponseDTO("Décodeur éteint avec succès", true);
    }

    /** Retourne la liste des chaînes associées à un décodeur. */
    @Override
    public GetChainesResponseDTO getChaines(Long idDecodeur) {
        Decodeur decodeur = decodeurRepository.findById(idDecodeur)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        if (decodeur.getModuleContenu() == null) {
            return new GetChainesResponseDTO(idDecodeur, new ArrayList<>());
        }

        List<String> chaines = decodeur.getModuleContenu().getChaines();
        return new GetChainesResponseDTO(idDecodeur, chaines != null ? chaines : new ArrayList<>());
    }

    /** Ajoute une chaîne au décodeur. Retourne false si elle existe déjà. */
    @Override
    public OperationDecodeurResponseDTO ajouterChaine(ChaineRequestDTO request) {
        Decodeur decodeur = decodeurRepository.findById(request.getIdDecodeur()).orElse(null);
        if (decodeur == null) return new OperationDecodeurResponseDTO("Décodeur introuvable", false);

        boolean added = decodeur.ajouterChaine(request.getChaine());
        if (!added) return new OperationDecodeurResponseDTO("La chaîne existe déjà", false);

        try {
            decodeurRepository.save(decodeur);
        } catch (Exception e) {
            return new OperationDecodeurResponseDTO("Échec lors de la sauvegarde", false);
        }
        return new OperationDecodeurResponseDTO("Chaîne ajoutée avec succès", true);
    }

    /** Retire une chaîne du décodeur. Retourne false si elle est absente. */
    @Override
    public OperationDecodeurResponseDTO retirerChaine(ChaineRequestDTO request) {
        Decodeur decodeur = decodeurRepository.findById(request.getIdDecodeur()).orElse(null);
        if (decodeur == null) return new OperationDecodeurResponseDTO("Décodeur introuvable", false);

        boolean removed = decodeur.supprimerChaine(request.getChaine());
        if (!removed) return new OperationDecodeurResponseDTO("La chaîne n'existe pas", false);

        try {
            decodeurRepository.save(decodeur);
        } catch (Exception e) {
            return new OperationDecodeurResponseDTO("Échec lors de la sauvegarde", false);
        }
        return new OperationDecodeurResponseDTO("Chaîne retirée avec succès", true);
    }

    /**
     * Retourne les décodeurs d'un client en synchronisant leur état avec
     * le simulateur. Si le simulateur est inaccessible pour un décodeur,
     * les données locales sont conservées sans interruption du traitement.
     */
    @Transactional
    @Override
    public List<AllDecodersByClientResponseDTO> getAllDecodersByClient(Long idClient) {
        List<Decodeur> decodeurs = decodeurRepository.findByClientId(idClient);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return decodeurs.stream().map(decodeur -> {
            try {
                SimulateurInfoResponseDTO infoReelle = simulateur.obtenirEtat(decodeur.getAdresseIp());
                if (infoReelle != null && infoReelle.getLastRestart() != null) {
                    decodeur.setEtat(EtatDecodeur.fromSimulateurState(infoReelle.getState()));
                    decodeur.setLastRestart(LocalDateTime.parse(infoReelle.getLastRestart(), fmt));
                    decodeur.setLastReinit(LocalDateTime.parse(infoReelle.getLastReinit(), fmt));
                    decodeurRepository.save(decodeur);
                }
            } catch (Exception e) {
                // Le simulateur est inaccessible pour ce décodeur — on conserve les données locales
                System.err.println("Erreur synchro pour " + decodeur.getAdresseIp() + " : " + e.getMessage());
            }

            return new AllDecodersByClientResponseDTO(
                    decodeur.getId(),
                    decodeur.getAdresseIp(),
                    decodeur.getEtat().name(),
                    decodeur.getLastRestart() != null ? decodeur.getLastRestart().format(fmt) : "N/A",
                    decodeur.getLastReinit()  != null ? decodeur.getLastReinit().format(fmt)  : "N/A"
            );
        }).toList();
    }

    /** Assigne un décodeur disponible à un client. Rejette si déjà attribué. */
    @Override
    public AssignDecoderResponseDTO assignDecoderToClient(Long clientId, Long decodeurId) {
        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        if (decodeur.getClient() != null) {
            throw new RuntimeException("Ce décodeur est déjà attribué");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        decodeur.setClient(client);
        decodeurRepository.save(decodeur);

        return new AssignDecoderResponseDTO(client.getId(), decodeur.getId(), true, "Décodeur assigné avec succès");
    }
}