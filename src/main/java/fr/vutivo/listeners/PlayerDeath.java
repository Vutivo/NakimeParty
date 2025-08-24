package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Camp;
import fr.vutivo.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    private final GameService gameService;

    public PlayerDeath(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameService.getState() != State.PLAYING) {
            return;
        }

        event.setDeathMessage(null);
        Player victim = event.getEntity();
        Player killer = victim.getKiller(); // Peut être null

        Joueur jKiller = gameService.getJoueur(killer);
        Joueur jVictim = gameService.getJoueur(victim);

            if (jKiller != null && jVictim != null) {
                jKiller.addKill();
                // Récompenses
                killer.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE, 2).build());


            }
            if(jVictim == null){
                return;
            }
        Location location = victim.getLocation();
        victim.spigot().respawn();
        victim.teleport(location);
        victim.setGameMode(GameMode.SPECTATOR);
        jVictim.setAlive(false);

        Bukkit.broadcastMessage("§8==========================");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7" + victim.getName() + " est mort. Il était " + jVictim.getRole().getName());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8==========================");

        // Retirer du camp
        gameService.removeJoueur(jVictim);
        gameService.getScoreBoardManager().updateLineAll(2, ChatColor.WHITE+"Joueurs : " + ChatColor.AQUA + gameService.getJoueurs().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxJoueurs);
        Camp victimCamp = jVictim.getCamp();
        switch (victimCamp) {
            case Slayer:
                gameService.removeSlayer(jVictim);
                gameService.getScoreBoardManager().updateLineAll(3,ChatColor.WHITE+"Slayers : " + ChatColor.AQUA + gameService.getSlayers().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxSlayers);
                break;
            case Demon:
                gameService.removeDemon(jVictim);
                gameService.getScoreBoardManager().updateLineAll(4,ChatColor.WHITE+"Démons : " + ChatColor.AQUA + gameService.getDemons().size() +ChatColor.WHITE+" / " +ChatColor.AQUA + gameService.maxDemons);

                break;
        }

    }


}
