package com.uqtr.decodeurtr.service.client;

import com.uqtr.decodeurtr.dto.AllClientsResponseDTO;
import com.uqtr.decodeurtr.dto.CreateClientRequestDTO;
import com.uqtr.decodeurtr.dto.CreateClientResponseDTO;
import com.uqtr.decodeurtr.dto.DeleteClientResponse;
import com.uqtr.decodeurtr.entity.Client;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    CreateClientResponseDTO createClient(CreateClientRequestDTO createClientRequestDTO);
    List<AllClientsResponseDTO> getAllClients();
    DeleteClientResponse deleteClient(Long idClient);
}
