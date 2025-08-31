package fr.vutivo.task;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;


public class GameTask extends BukkitRunnable {
    private final GameService gameService;
    public int timer = 0;

    public GameTask(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public void run() {
        if (gameService.getState() == State.PLAYING) {

            for (Joueur joueur : gameService.getJoueurs()) {
                if (joueur.isAlive()) {
                    int cd = joueur.getCooldown();
                    if (cd > 0) {
                        joueur.setCooldown(cd - 1);
                    }

                    // Vérifie la hauteur Y
                    if (joueur.getPlayer().getLocation().getY() < 8) {
                        joueur.getPlayer().sendMessage("§cVous êtes trop bas, vous perdez 1 coeur !");
                        joueur.getPlayer().setMaxHealth(Math.max(0.5, joueur.getPlayer().getMaxHealth() - 2));

                        // Téléportation
                        List<Location> spawns = gameService.getSpawnManager().getSpawns(gameService.MAP_NAME);
                        Collections.shuffle(spawns);
                        Location spawn = spawns.get(0);
                        joueur.getPlayer().teleport(spawn);
                    }
                }
            }
            //On check si on reveal ou non Nezuko
            if (!gameService.revealNezuko) {

                // Afficher les noms des Slayers
                StringBuilder slayersList = new StringBuilder();
                for (Joueur s : gameService.getSlayers()) {
                    slayersList.append(s.getPlayer().getName()).append(" ");
                }

                    // Condition de reveal : 5 minutes écoulées ou nombre de Slayers <= 3
                if (timer >= 300 || gameService.getSlayers().size() <= gameService.slayerCountForReveal) {
                    gameService.revealNezuko = true;
                    Joueur nezuko = gameService.getRole(Role.Nezuko);
                    if (nezuko == null) {
                        return;
                    }

                    for (Joueur demon : gameService.getDemons()) {
                        Bukkit.broadcastMessage("§a[DEBUG] Envoi du message de reveal à : " + demon.getPlayer().getName());
                        demon.getPlayer().sendMessage("§cNezuko a été révélée ! Il s'agit de §6" + nezuko.getPlayer().getName() + "§c !");
                    }


                    gameService.getTabList().revealNezukoForAll(nezuko.getPlayer());
                }
            }

        }

            //On check les conition de win
            gameService.checkWin();

            // Update scoreboard timer
            timer++;
            gameService.getScoreBoardManager().updateLineAll(8, "§6Temps: §e" + gameService.Chrono(timer));

            // Check Tanjiro rage
            gameService.tanjiroRage();
        }
    }


