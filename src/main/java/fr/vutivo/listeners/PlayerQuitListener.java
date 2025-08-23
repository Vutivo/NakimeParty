package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private GameService gameService;
    public PlayerQuitListener(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);
        gameService.getScoreBoardManager().remove(player);

        gameService.getJoueurs().stream()
                .filter(joueur -> joueur.getPlayer().equals(player))
                .findFirst()
                .ifPresent(gameService::removeJoueur);

        if(gameService.getState().equals(State.WAITING) || gameService.getState().equals(State.STARTING)) {
            Bukkit.broadcastMessage("§c" + player.getName() + " a quitté la partie." + "§7 (" + gameService.getJoueurs().size() + "/" + gameService.maxPlayers + ")");
        }

        if(gameService.getState().equals(State.STARTING)){
            gameService.getgAutoStart().checkMinPlayers();

        }


    }


}
