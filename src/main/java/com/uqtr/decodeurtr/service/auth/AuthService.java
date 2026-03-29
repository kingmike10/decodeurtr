package com.uqtr.decodeurtr.service.auth;

import com.uqtr.decodeurtr.dto.LoginRequestDTO;
import com.uqtr.decodeurtr.dto.LoginResponseDTO;
import com.uqtr.decodeurtr.entity.Utilisateur;
import com.uqtr.decodeurtr.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private LoginRequestDTO loginRequestDTO;

    public AuthService(UtilisateurRepository utilisateurRepository,LoginRequestDTO loginRequestDTO) {
        this.utilisateurRepository = utilisateurRepository;
        this.loginRequestDTO = loginRequestDTO;
    }

    public LoginResponseDTO authentifier(LoginRequestDTO loginRequestDTO) {

        String identifiantConnexion = loginRequestDTO.getIdentifiantConnexion();
        String motDePasse = loginRequestDTO.getMotDePasse();

        Optional<Utilisateur> utilisateurOpt =
                utilisateurRepository.findByIdentifiantConnexion(identifiantConnexion);

        if (utilisateurOpt.isEmpty()) {
            return new LoginResponseDTO(false, "Identifiant de connexion non trouvé");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        if (!utilisateur.getActif()) {
            return new LoginResponseDTO(false, "Compte inactif");
        }

        if (!utilisateur.getMotDePasse().equals(motDePasse)) {
            return new LoginResponseDTO(false, "Mot de passe incorrect");
        }

        return new LoginResponseDTO(true, "Authentification réussie");
    }

}
