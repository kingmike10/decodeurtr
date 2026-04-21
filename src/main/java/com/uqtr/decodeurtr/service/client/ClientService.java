package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.entity.Client;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    CreateClientResponseDTO createClient(CreateClientRequestDTO createClientRequestDTO);
    List<AllClientsResponseDTO> getAllClients();
    DeleteClientResponse deleteClient(Long idClient);
    ClientDashboardDTO getDashboardData(String identifiant);
}
