package fr.vutivo.task;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Camp;
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

            gameService.getJoueurs().forEach(joueur -> {
                joueur.getPlayer().setLevel(timer);
            });
            gameService.getJoueurs().forEach(joueur -> {
                joueur.getPlayer().setExp((float) timer / 60);
            });

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
        Collections.shuffle(gameService.getJoueurs());

        int totalPlayers = gameService.getJoueurs().size();
        int nbSlayers = totalPlayers / 2;
        int nbDemons = totalPlayers / 2;
        boolean hasYoriichi = (totalPlayers % 2 == 1);

        List<Role> roles = gameService.getCompo();
//        // Yoriichi si impair
//        if (hasYoriichi) {
//            roles.add(Role.Yoriichi);
//        }
//
//        // Slayers
//        if (nbSlayers > 0) {
//            roles.add(Role.Tanjiro); // priorité Tanjiro
//            for (int i = 1; i < nbSlayers; i++) {
//                roles.add(Role.getRandomSlayerExceptTanjiro());
//            }
//        }
//
//        // Demons
//        if (nbDemons > 0) {
//            roles.add(Role.Nakime); // priorité Nakime
//            for (int i = 1; i < nbDemons; i++) {
//                roles.add(Role.getRandomDemonExceptNakime());
//            }
//        }

        roles.add(Role.Gyomei);
        roles.add(Role.Doma);
        Collections.shuffle(roles);

        GameTask gameTask = new GameTask(gameService);
        gameService.setGameTask(gameTask); // Assigner au GameService
        gameTask.runTaskTimer(gameService.getInstance(), 0, 20);

        for (int i = 0; i < gameService.getJoueurs().size(); i++) {
            Joueur joueur = gameService.getJoueurs().get(i);

            gameService.getSpawnManager().teleportRandomSpawn(joueur.getPlayer(), "world");

            Role role = roles.get(i);
            joueur.setRole(role);
            joueur.setCamp(role.getCamp());
            gameService.givePlayerArmor(joueur);
            joueur.aplyItems(role);
            joueur.setCooldown(0);
            Description.sendDescriptionToPlayer(joueur);

            if(joueur.getCamp() == Camp.Slayer) {
                gameService.addSlayer(joueur);
            } else if (joueur.getCamp() == Camp.Demon) {
                gameService.addDemon(joueur);
            }
            gameService.initScoreboardGame(joueur);
        }
        Description.otherInfo(gameService);


        gameService.maxDemons = gameService.getDemons().size();
        gameService.maxSlayers = gameService.getSlayers().size();
        gameService.maxJoueurs = gameService.getJoueurs().size();


        gameService.getScoreBoardManager().updateLineAll(2,ChatColor.WHITE+"Joueurs : " + ChatColor.AQUA + gameService.getJoueurs().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxJoueurs);
        gameService.getScoreBoardManager().updateLineAll(3,ChatColor.WHITE+"Slayers : " + ChatColor.AQUA + gameService.getSlayers().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxSlayers);
        gameService.getScoreBoardManager().updateLineAll(4,ChatColor.WHITE+"Démons : " + ChatColor.AQUA + gameService.getDemons().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxDemons);

        ArowTanjiro arowTanjiro = new ArowTanjiro(gameService);
        arowTanjiro.runTaskTimer(gameService.getInstance(), 0, 20*2); // Toutes les 2 secondes

    }

}
