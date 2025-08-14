package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.scoreboard.ScoreBoardManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final ScoreBoardManager scoreboardService;
    private final GameService gameService;

    public PlayerJoin(GameService gameService) {
        this.gameService = gameService;
        this.scoreboardService = gameService.getScoreBoardManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(canJoin(gameService, player)) {
            Joueur joueur = new Joueur(player);
            gameService.addJoueur(joueur);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);

            initScoreboard(scoreboardService, player);


        }


    }

    private boolean canJoin(GameService service , Player player) {
        if(service.getState().equals(State.WAITING)){
            if (service.minPlayers < service.maxPlayers) {
                return true;
            } else {
                player.kickPlayer("§cLa partie est pleine");
                return false;
            }
        } else {
            player.kickPlayer(ChatColor.RED + "La partie a déjà commencé, vous ne pouvez pas rejoindre maintenant.");
            return false;
        }

    }

    private void initScoreboard(ScoreBoardManager scoreboardService, Player player) {
        scoreboardService.create
                (player, ChatColor.RED + "Nakime" + ChatColor.DARK_RED + ChatColor.BOLD + "Party",
                        "",
                        "",
                        ChatColor.GREEN + "Attente de joueurs...",
                        "Plugin developpé par",
                        ChatColor.GOLD + "Vutivo");

    }
}