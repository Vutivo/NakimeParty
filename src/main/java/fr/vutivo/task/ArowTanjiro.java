package fr.vutivo.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.GameService;
import fr.vutivo.roles.Role;
import fr.vutivo.roles.Camp;

import static fr.vutivo.utils.SendActionBar.sendActionBar;

public class ArowTanjiro extends BukkitRunnable {

    private final GameService gameService;

    public ArowTanjiro(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void run() {
        for (Joueur joueur : gameService.getJoueurs()) {
            if (!joueur.getRole().equals(Role.Tanjiro) || !joueur.isAlive()) continue;

            Player player = joueur.getPlayer();
            Joueur nearestDemon = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Joueur other : gameService.getJoueurs()) {
                if (other.equals(joueur) || !other.isAlive() || !gameService.getDemons().contains(other)) continue;

                double distance = player.getLocation().distance(other.getPlayer().getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestDemon = other;
                }
            }

            if (nearestDemon != null) {
                String arrow = gameService.getTanjiroArrow(player, nearestDemon.getPlayer());
                String msg = "§c" + arrow + " " + (int) nearestDistance + "m";
               sendActionBar(player, msg);
            } else {
                sendActionBar(player, "§7Aucun démon détecté");
            }
        }
    }

}
