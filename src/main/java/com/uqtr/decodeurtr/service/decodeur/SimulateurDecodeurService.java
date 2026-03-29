package com.uqtr.decodeurtr.service.decodeur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uqtr.decodeurtr.dto.SimulateurInfoResponseDTO;
import com.uqtr.decodeurtr.dto.SimulateurRequestDTO;
import com.uqtr.decodeurtr.dto.SimulateurResetResponseDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SimulateurDecodeurService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SimulateurDecodeurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SimulateurInfoResponseDTO obtenirEtat(String adresseIp) {
        String url = "https://wflageol-uqtr.net/decoder";

        SimulateurRequestDTO requestBody = new SimulateurRequestDTO(
                "ANAM82290400",
                adresseIp,
                "info"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Accepter application/json et text/json;charset=utf-8
        headers.setAccept(java.util.List.of(
                MediaType.APPLICATION_JSON,
                MediaType.parseMediaType("application/json;charset=utf-8")
        ));

        HttpEntity<SimulateurRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        // Obtenir la réponse en String puis désérialiser manuellement pour gérer les content-types non-standard
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        String body = response.getBody();
        try {
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Réponse vide du simulateur");
            }
            // Désérialisation manuelle — ObjectMapper gère application/json même si le serveur envoie text/json
            return objectMapper.readValue(body, SimulateurInfoResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation de la réponse du simulateur: " + e.getMessage(), e);
        }
    }

    public SimulateurResetResponseDTO reinitialiserDecodeur(String adresseIp) {
        String url = "https://wflageol-uqtr.net/decoder";

        SimulateurRequestDTO requestBody = new SimulateurRequestDTO(
                "ANAM82290400",
                adresseIp,
                "reset"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(
                MediaType.APPLICATION_JSON,
                MediaType.parseMediaType("application/json;charset=utf-8")
        ));

        HttpEntity<SimulateurRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        String body = response.getBody();
        try {
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Réponse vide du simulateur");
            }
            return objectMapper.readValue(body, SimulateurResetResponseDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    }
