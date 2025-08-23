package fr.vutivo.utils;

/**
 * Interface pour les services du plugin NakimeParty
 */
public interface NakimeService {

    /**
     * Enregistre le service (événements, commandes, etc.)
     */
    void register();

    /**
     * Désenregistre le service et nettoie les ressources
     */
    void unregister();
}