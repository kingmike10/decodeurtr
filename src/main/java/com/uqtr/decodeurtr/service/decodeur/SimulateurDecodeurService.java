package com.uqtr.decodeurtr.service.decodeur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uqtr.decodeurtr.dto.SimulateurInfoResponseDTO;
import com.uqtr.decodeurtr.dto.SimulateurRequestDTO;
import com.uqtr.decodeurtr.dto.SimulateurResetResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Service d'accès à l'API REST du simulateur de décodeurs.
 *
 * Centralise toutes les communications avec le simulateur externe disponible
 * à l'adresse https://wflageol-uqtr.net/decoder. Chaque opération physique
 * sur un décodeur (état, redémarrage, réinitialisation, extinction) transite
 * par ce service, isolant ainsi le reste de l'application des détails
 * du protocole de communication externe.
 *
 * Le simulateur retourne parfois un content-type non standard (text/json).
 * La désérialisation est donc effectuée manuellement via ObjectMapper
 * plutôt que de laisser Spring la gérer automatiquement.
 */
@Service
public class SimulateurDecodeurService {

    private static final String SIMULATEUR_URL = "https://wflageol-uqtr.net/decoder";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Code permanent identifiant l'équipe auprès du simulateur.
     * Externalisé dans application.properties sous la clé simulateur.code-permanent.
     */
    @Value("${simulateur.code-permanent:ANAM82290400}")
    private String codePermanent;

    public SimulateurDecodeurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Construit la requête HTTP à envoyer au simulateur.
     * Accepte application/json et application/json;charset=utf-8 pour
     * absorber les variations de content-type retournées par le simulateur.
     *
     * @param adresseIp l'adresse IP du décodeur cible
     * @param action    l'opération à effectuer (info, reset, reinit, shutdown)
     * @return l'entité HTTP prête à être envoyée
     */
    private HttpEntity<SimulateurRequestDTO> buildRequest(String adresseIp, String action) {
        SimulateurRequestDTO body = new SimulateurRequestDTO(codePermanent, adresseIp, action);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.parseMediaType("application/json;charset=utf-8")
        ));
        return new HttpEntity<>(body, headers);
    }

    /**
     * Méthode générique d'envoi de requête au simulateur.
     * Récupère la réponse sous forme de String puis la désérialise
     * vers le type attendu, évitant les problèmes de content-type non standard.
     *
     * @param adresseIp    l'adresse IP du décodeur cible
     * @param action       l'opération à effectuer
     * @param responseType le type Java attendu pour la désérialisation
     * @return la réponse désérialisée du simulateur
     * @throws RuntimeException si la réponse est vide ou non désérialisable
     */
    private <T> T envoyerRequete(String adresseIp, String action, Class<T> responseType) {
        ResponseEntity<String> response = restTemplate.exchange(
                SIMULATEUR_URL, HttpMethod.POST, buildRequest(adresseIp, action), String.class);

        String bodyStr = response.getBody();
        if (bodyStr == null || bodyStr.isBlank()) {
            throw new RuntimeException("Réponse vide du simulateur");
        }

        try {
            return objectMapper.readValue(bodyStr, responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur désérialisation simulateur : " + e.getMessage(), e);
        }
    }

    /** Interroge le simulateur pour obtenir l'état courant d'un décodeur. */
    public SimulateurInfoResponseDTO obtenirEtat(String adresseIp) {
        return envoyerRequete(adresseIp, "info", SimulateurInfoResponseDTO.class);
    }

    /** Envoie un ordre de redémarrage au décodeur (durée : 10 à 30 secondes). */
    public SimulateurResetResponseDTO resetDecodeur(String adresseIp) {
        return envoyerRequete(adresseIp, "reset", SimulateurResetResponseDTO.class);
    }

    /** Réinitialise le mot de passe du décodeur et met à jour la date de réinitialisation. */
    public SimulateurResetResponseDTO reinitDecodeur(String adresseIp) {
        return envoyerRequete(adresseIp, "reinit", SimulateurResetResponseDTO.class);
    }

    /** Envoie un ordre d'extinction au décodeur. */
    public SimulateurResetResponseDTO shutdownDecodeur(String adresseIp) {
        return envoyerRequete(adresseIp, "shutdown", SimulateurResetResponseDTO.class);
    }
}