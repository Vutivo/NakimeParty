package fr.vutivo.game;

import org.bukkit.entity.Player;

public class Joueur {
    private Player player;
    private int kills;

    public Joueur(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
