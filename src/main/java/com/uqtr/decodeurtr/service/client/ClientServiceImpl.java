package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.AllClientsResponseDTO;
import com.uqtr.decodeurtr.dto.CreateClientRequestDTO;
import com.uqtr.decodeurtr.dto.CreateClientResponseDTO;
import com.uqtr.decodeurtr.dto.DeleteClientResponse;
import com.uqtr.decodeurtr.entity.Client;
import com.uqtr.decodeurtr.repository.ClientRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService{

    private ClientRepository clientRepository;
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    @Override
    public CreateClientResponseDTO createClient(CreateClientRequestDTO request) {

        if (request.getNomClient() == null || request.getNomClient().isBlank()
                || request.getIdentifiantConnexion() == null || request.getIdentifiantConnexion().isBlank()
                || request.getMotDePasse() == null || request.getMotDePasse().isBlank()
                || request.getNomAffichage() == null || request.getNomAffichage().isBlank()) {
            throw new RuntimeException("Tous les champs sont obligatoires.");
        }

        if (clientRepository.existsByIdentifiantConnexion(request.getIdentifiantConnexion())) {
            throw new RuntimeException("L'identifiant de connexion existe déjà.");
        }

        Client client = new Client(request.getNomClient(), request.getIdentifiantConnexion(),
                request.getMotDePasse(), request.getNomAffichage());
        Client savedClient = clientRepository.save(client);

        return new CreateClientResponseDTO(savedClient.getNomClient(), savedClient.getIdentifiantConnexion(),
                savedClient.getNomAffichage(),
                "Client créé avec succès.");

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
                            client.getIdentifiantConnexion(),
                            client.getNomClient(),
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
