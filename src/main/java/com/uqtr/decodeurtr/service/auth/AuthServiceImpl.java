package com.uqtr.decodeurtr.service.auth;

import com.uqtr.decodeurtr.dto.*;
import com.uqtr.decodeurtr.entity.Utilisateur;
import com.uqtr.decodeurtr.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Authentification ──────────────────────────────────────────────────────

    public LoginResponseDTO authentifier(LoginRequestDTO dto) {
        Optional<Utilisateur> opt =
                utilisateurRepository.findByIdentifiantConnexion(dto.getIdentifiantConnexion());

        if (opt.isEmpty())
            return new LoginResponseDTO(false, "Identifiant de connexion non trouvé");

        Utilisateur u = opt.get();

        if (!u.getActif())
            return new LoginResponseDTO(false, "Compte inactif");

        if (!passwordEncoder.matches(dto.getMotDePasse(), u.getMotDePasse()))
            return new LoginResponseDTO(false, "Mot de passe incorrect");

        return new LoginResponseDTO(true, "Authentification réussie",
                u.getRole(), u.getIdentifiantConnexion());
    }

    // ── Étape 1 : retourner la question secrète ───────────────────────────────

    public GetQuestionResponseDTO getQuestionSecrete(String identifiantConnexion) {
        Optional<Utilisateur> opt =
                utilisateurRepository.findByIdentifiantConnexion(identifiantConnexion);

        // SÉCURITÉ : message identique que l'identifiant existe ou non
        // → empêche l'énumération de comptes
        if (opt.isEmpty() || opt.get().getQuestionSecrete() == null
                || opt.get().getQuestionSecrete().isBlank()) {
            return new GetQuestionResponseDTO(false, null,
                    "Aucun compte associé à cet identifiant.");
        }

        return new GetQuestionResponseDTO(true, opt.get().getQuestionSecrete(), null);
    }

    // ── Étape 2 : vérifier la réponse et réinitialiser le mot de passe ────────

    @Transactional
    public ResetMotDePasseResponseDTO resetMotDePasse(ResetMotDePasseRequestDTO req) {

        // Validation des champs
        if (req.getNouveauMotDePasse() == null || req.getNouveauMotDePasse().isBlank())
            return new ResetMotDePasseResponseDTO(false, "Le nouveau mot de passe est obligatoire.");

        if (!req.getNouveauMotDePasse().equals(req.getConfirmationMotDePasse()))
            return new ResetMotDePasseResponseDTO(false, "Les mots de passe ne correspondent pas.");

        if (req.getNouveauMotDePasse().length() < 6)
            return new ResetMotDePasseResponseDTO(false,
                    "Le mot de passe doit contenir au moins 6 caractères.");

        Optional<Utilisateur> opt =
                utilisateurRepository.findByIdentifiantConnexion(req.getIdentifiantConnexion());

        // SÉCURITÉ : "Réponse incorrecte" même si l'identifiant est inconnu
        // → ne pas révéler si le compte existe
        if (opt.isEmpty())
            return new ResetMotDePasseResponseDTO(false, "Réponse incorrecte.");

        Utilisateur u = opt.get();

        // Vérifier la réponse secrète (BCrypt + normalisation minuscules/trim)
        if (u.getReponseSecrete() == null
                || !passwordEncoder.matches(
                    req.getReponseSecrete().trim().toLowerCase(),
                    u.getReponseSecrete())) {
            return new ResetMotDePasseResponseDTO(false, "Réponse incorrecte.");
        }

        // Mettre à jour le mot de passe hashé
        u.setMotDePasse(passwordEncoder.encode(req.getNouveauMotDePasse()));
        utilisateurRepository.save(u);

        return new ResetMotDePasseResponseDTO(true, "Mot de passe réinitialisé avec succès.");
    }
}
