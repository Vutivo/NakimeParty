package fr.vutivo.task;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Description;
import fr.vutivo.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GAutoStart extends BukkitRunnable {
    private final GameService gameService;
    private int timer = 5;


    public GAutoStart(GameService gameService) {
        this.gameService = gameService;

    }

    @Override
    public void run() {
        if(gameService.getState() == State.STARTING) {
            if (timer == 15 || timer == 10 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1) {
                gameService.getJoueurs().forEach(joueur -> joueur.getPlayer().sendMessage("§aLa partie commence dans " + timer + " seconde(s) !"));
            }
            if (timer == 0) {
                gameService.getJoueurs().forEach(joueur -> joueur.getPlayer().sendMessage("§aLa partie commence !"));
                gameService.setState(State.PLAYING);
                start();
                cancel();
                return;
            }

            updateScoreboard();


            timer--;
        }



    }

    private void updateScoreboard() {
        gameService.getScoreBoardManager().updateLineAll(2, "Début dans " + timer + " seconde(s)");
    }

    public void checkMinPlayers() {
        if (gameService.getJoueurs().size() < gameService.minPlayers) {
            gameService.setState(State.WAITING);
           Bukkit.broadcastMessage("§cPas assez de joueurs pour commencer la partie. Le timer est remis à zéro.");
            gameService.getScoreBoardManager().updateLineAll(2, ChatColor.GREEN + "Attente de joueurs...");
            timer = 5;
        }
    }

    public void start() {

        cancel();
        Collections.shuffle(gameService.getJoueurs()); // Mélange les joueurs

        // Récupérer et mélanger les rôles disponibles
        List<Role> roles = new ArrayList<>(Arrays.asList(Role.values()));
        Collections.shuffle(roles);

        for (int i = 0; i < gameService.getJoueurs().size(); i++) {
            Joueur joueur = gameService.getJoueurs().get(i);

            // TP au spawn aléatoire
            gameService.getSpawnManager().teleportRandomSpawn(joueur.getPlayer(), "world");

            // Distribution du rôle
            Role role = roles.get(i);
            joueur.setRole(role);
            joueur.setCamp(role.getCamp());
            gameService.givePlayerArmor(joueur);
            joueur.aplyItems(role);
            Description.sendDescriptionToPlayer(joueur);

        }
    }

}
