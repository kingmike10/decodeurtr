package com.uqtr.decodeurtr.service.decodeur;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.entity.Client;
import com.uqtr.decodeurtr.entity.Decodeur;
import com.uqtr.decodeurtr.repository.ClientRepository;
import com.uqtr.decodeurtr.repository.DecodeurRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DecodeurServiceImpl implements DecodeurService {


    private final SimulateurDecodeurService simulateur;
    private final DecodeurRepository decodeurRepository;
    private final ClientRepository clientRepository;


    public DecodeurServiceImpl(SimulateurDecodeurService simulateur, DecodeurRepository decodeurRepository, ClientRepository clientRepository) {
        this.simulateur = simulateur;
        this.decodeurRepository = decodeurRepository;
        this.clientRepository = clientRepository;
    }



    @Override
    public List<DecoderAssignedResponseDTO> getAllAssignedDecoders() {
        List<Decodeur> decodeursAttribues = decodeurRepository.findByClientIsNotNull();
        return decodeursAttribues.stream().map(decodeur -> new DecoderAssignedResponseDTO(
                decodeur.getAdresseIp(),decodeur.getEtat().name(),decodeur.getClient().getId(),
                decodeur.getClient().getNomClient()
        )
        ).toList();

    }

    @Override
    public List<DecoderAvailableResponseDTO> getAllAvailableDecoders() {
        List<Decodeur> decodeursDisponibles = decodeurRepository.findByClientIsNull();
        return decodeursDisponibles.stream().map(decodeur -> new DecoderAvailableResponseDTO(
                        decodeur.getAdresseIp(),decodeur.getEtat().name()
                )
        ).toList();
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

        SimulateurInfoResponseDTO response= simulateur.obtenirEtat(decodeur.getAdresseIp());


        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("Erreur simulateur : " + response.getMessage());
        }

        return new EtatDecodeurResponseDTO(decodeur.getAdresseIp(),response.getState(),
                response.getLastRestart(), response.getLastReinit()
        );

    }

    @Override
    public ResetDecodeurResponseDTO resetDecoder(Long decodeurId) {

        Decodeur decodeur = decodeurRepository.findById(decodeurId)
                .orElseThrow(() -> new RuntimeException("Décodeur introuvable"));

        SimulateurResetResponseDTO response= simulateur.reinitialiserDecodeur(decodeur.getAdresseIp());


        if (!"OK".equalsIgnoreCase(response.getResponse())) {
            throw new RuntimeException("Erreur simulateur : " + response.getMessage());
        }

        return new ResetDecodeurResponseDTO(response.getResponse());
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


    @Override
    public List<AllDecodersByClientResponseDTO> getAllDecodersByClient(Long idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        return decodeurRepository.findByClientIsNotNull().stream()
                .filter(decodeur -> decodeur.getClient().getId().equals(client.getId()))
                .map(decodeur -> new AllDecodersByClientResponseDTO(decodeur.getAdresseIp(),decodeur.getEtat().name(),
                        decodeur.getLastRestart(), decodeur.getLastReinit()
                )).toList();
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
