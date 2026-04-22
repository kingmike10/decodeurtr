package com.uqtr.decodeurtr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration centrale de Spring Security pour l'application DecodeurTR.
 *
 * Cette classe regroupe deux responsabilités liées à la sécurité :
 * - L'encodage des mots de passe via BCrypt
 * - La configuration de la chaîne de filtres HTTP (CSRF, sessions, CORS, autorisation)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Déclare un encodeur de mots de passe BCrypt comme bean Spring.
     *
     * BCrypt est un algorithme de hachage adaptatif conçu pour les mots de passe.
     * Il intègre automatiquement un sel aléatoire dans chaque hash, ce qui rend
     * les attaques par table arc-en-ciel impossibles. Le facteur de coût par défaut
     * est 10, ce qui représente un bon équilibre entre sécurité et performance.
     *
     * Ce bean est injecté dans AuthServiceImpl (vérification au login)
     * et dans ClientServiceImpl (hashage à la création d'un compte).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure la chaîne de filtres de sécurité HTTP appliquée à chaque requête.
     *
     * Les choix retenus sont adaptés à une architecture REST stateless où
     * l'authentification est gérée côté client via le localStorage du navigateur.
     *
     * @param http le builder Spring Security permettant de configurer les filtres
     * @return la chaîne de filtres construite
     * @throws Exception si la configuration est invalide
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // La protection CSRF est désactivée car l'API REST ne repose pas sur
                // des cookies de session. Les requêtes JSON cross-origin ne peuvent pas
                // être déclenchées involontairement par un site tiers.
                .csrf(AbstractHttpConfigurer::disable)

                // Aucune session HTTP n'est créée côté serveur. Chaque requête est
                // traitée de manière indépendante, conformément au modèle REST.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // La configuration CORS est centralisée dans corsConfigurationSource()
                // plutôt que dispersée via @CrossOrigin sur chaque contrôleur.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Toutes les routes sont accessibles sans authentification côté serveur.
                // Le contrôle d'accès par rôle (RBAC) est actuellement appliqué
                // côté frontend via la redirection post-login selon le rôle retourné.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * Définit la politique CORS (Cross-Origin Resource Sharing) de l'application.
     *
     * Cette configuration autorise les requêtes provenant de n'importe quelle
     * origine, ce qui est approprié en développement. En production, il serait
     * recommandé de restreindre les origines autorisées à l'URL exacte du frontend.
     *
     * @return la source de configuration CORS appliquée à toutes les routes
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Toutes les origines sont autorisées (à restreindre en production)
        config.setAllowedOriginPatterns(List.of("*"));

        // Méthodes HTTP acceptées pour les requêtes cross-origin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Tous les en-têtes sont acceptés, y compris Content-Type et Authorization
        config.setAllowedHeaders(List.of("*"));

        // Les cookies et credentials ne sont pas transmis dans les requêtes cross-origin
        config.setAllowCredentials(false);

        // Applique cette configuration à toutes les routes de l'application
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}