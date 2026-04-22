package com.uqtr.decodeurtr.entity;

/**
 * Énumération représentant l'état de connexion d'un décodeur.
 *
 * Fait le lien entre la terminologie du simulateur externe ("Active", "Inactive")
 * et la représentation interne utilisée dans le domaine de l'application.
 * Les valeurs sont stockées en base de données sous forme de chaîne de caractères
 * grâce à l'annotation @Enumerated(EnumType.STRING) dans l'entité Decodeur.
 */
public enum EtatDecodeur {

    /** Le décodeur est en fonction et connecté au réseau. */
    EN_LIGNE,

    /** Le décodeur est éteint ou en cours de redémarrage. */
    HORS_LIGNE;

    /**
     * Convertit l'état retourné par le simulateur externe en valeur de cette énumération.
     *
     * Le simulateur retourne les valeurs "Active" et "Inactive" dans son API REST.
     * La comparaison est insensible à la casse et aux espaces superflus pour
     * absorber d'éventuelles variations de format dans les réponses du simulateur.
     *
     * @param state la valeur de l'état retournée par le simulateur (peut être null)
     * @return EN_LIGNE si l'état est "active", HORS_LIGNE dans tous les autres cas
     */
    public static EtatDecodeur fromSimulateurState(String state) {
        if (state == null) {
            return HORS_LIGNE;
        }

        return switch (state.trim().toLowerCase()) {
            case "active"   -> EN_LIGNE;
            case "inactive" -> HORS_LIGNE;
            default         -> HORS_LIGNE;
        };
    }

    /**
     * Retourne une représentation lisible de l'état en français.
     * Utilisée pour l'affichage dans les réponses de l'API.
     *
     * @return "En ligne" ou "Hors ligne"
     */
    public String getLabel() {
        return this == EN_LIGNE ? "En ligne" : "Hors ligne";
    }

    /**
     * Indique si le décodeur est actuellement en ligne.
     * Méthode utilitaire évitant les comparaisons directes avec l'énumération
     * dans les couches supérieures.
     *
     * @return true si l'état est EN_LIGNE, false sinon
     */
    public boolean isEnLigne() {
        return this == EN_LIGNE;
    }
}