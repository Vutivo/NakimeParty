package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Camp;
import fr.vutivo.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final GameService gameService;

    public PlayerQuitListener(GameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        event.setQuitMessage(null);

        // Récupère le joueur AVANT tout traitement
        Joueur joueurQuit = gameService.getJoueur(player);


        // Supprime du scoreboard
        if (gameService.getScoreBoardManager() != null) {
            gameService.getScoreBoardManager().remove(player);
        }

        // Messages selon l'état du jeu
        if (gameService.getState() == State.WAITING || gameService.getState() == State.STARTING) {
            // Supprime le joueur de la liste
            gameService.removeJoueur(joueurQuit);
            gameService.allJoueurs.add(joueurQuit);

            Bukkit.broadcastMessage("§c" + player.getName() + " a quitté la partie. §7(" +
                    gameService.getJoueurs().size() + "/" + gameService.maxPlayers + ")");
            gameService.getScoreBoardManager().updateLineAll(1, "Nombre de joueurs : " + ChatColor.GOLD + gameService.getJoueurs().size() + "/" + gameService.maxPlayers);


            // Vérifie les joueurs minimums si en phase de démarrage
            if (gameService.getState() == State.STARTING && gameService.getgAutoStart() != null) {
                gameService.getgAutoStart().checkMinPlayers();
            }
        }

        // Si le jeu est en cours
        if (gameService.getState() == State.PLAYING && joueurQuit != null) {
            if (joueurQuit.getRole() != null && joueurQuit.isAlive()) {

                Bukkit.broadcastMessage("§8==========================");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§7" + player.getName() + " est mort. Il était " + joueurQuit.getRole().getName());
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("§8==========================");

                // Gestion spéciale pour Yoriichi
                if (joueurQuit.getRole() == Role.Yoriichi) {
                    gameService.YoriichiAlive = false;
                }

                // Supprime selon le camp
                Camp victimCamp = joueurQuit.getCamp();
                switch (victimCamp) {
                    case Slayer:
                        gameService.removeSlayer(joueurQuit);
                        break;
                    case Demon:
                        gameService.removeDemon(joueurQuit);
                        break;
                    case Solo:
                        // Supprime juste de la liste principale
                        gameService.removeJoueur(joueurQuit);
                        break;
                }

                // Supprime le joueur de la liste
                gameService.removeJoueur(joueurQuit);
                gameService.allJoueurs.add(joueurQuit);
                // Met à jour les compteurs du scoreboard
                updateScoreboardCounters();
            }
        }
    }

    private void updateScoreboardCounters() {
        if (gameService.getScoreBoardManager() != null) {
            gameService.getScoreBoardManager().updateLineAll(2,
                    ChatColor.WHITE + "Joueurs : " + ChatColor.AQUA + gameService.getJoueurs().size() +
                            ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxJoueurs);

            gameService.getScoreBoardManager().updateLineAll(3,
                    ChatColor.WHITE + "Slayers : " + ChatColor.AQUA + gameService.getSlayers().size() +
                            ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxSlayers);

            gameService.getScoreBoardManager().updateLineAll(4,
                    ChatColor.WHITE + "Démons : " + ChatColor.AQUA + gameService.getDemons().size() +
                            ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxDemons);
        }
    }
}