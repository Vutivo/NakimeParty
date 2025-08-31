package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.scoreboard.ScoreBoardManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
        event.setJoinMessage(null);
        if (canJoin(gameService, player)) {
            Joueur joueur = new Joueur(player);
            gameService.addJoueur(joueur);
            gameService.allJoueurs.add(joueur);
            gameService.clearPlayerInventory(joueur);
            player.setGameMode(GameMode.SURVIVAL);
            gameService.getSpawnManager().teleportToWorldSpawn(player, gameService.MAP_NAME);
            Bukkit.broadcastMessage("§a" + player.getName() + " a rejoint la partie" + "§7 (" + gameService.getJoueurs().size() + "/" + gameService.maxPlayers + ")");

            // 1️⃣ Remettre son pseudo en blanc dans le tab
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME,
                    ((CraftPlayer) player).getHandle()
            );
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

            // 2️⃣ Supprimer toutes ses équipes / préfixes pour remettre le nametag à la normale
            Scoreboard board = player.getScoreboard();
            for (Team team : board.getTeams()) {
                if (team.hasEntry(player.getName())) {
                    team.removeEntry(player.getName());
                }
            }
        }

        initScoreboard(scoreboardService, player);
        gameService.getScoreBoardManager().updateLineAll(1, "Nombre de joueurs : " + ChatColor.GOLD + gameService.getJoueurs().size() + "/" + gameService.maxPlayers);

        //Si le nombre de joueurs est supérieur ou égal au minimum pour lancer la partie et que l'état est WAITING, on lance le timer
        if (gameService.getJoueurs().size() >= gameService.minPlayers && gameService.getState().equals(State.WAITING)) {
            gameService.checkAutoStart();

        }


    }


    private boolean canJoin(GameService service, Player player) {
        if (service.getState().equals(State.WAITING) || service.getState().equals(State.STARTING)) {
            if (service.getJoueurs().size() < service.maxPlayers) {
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
                        "Nombre de joueurs : " + ChatColor.GOLD + gameService.getJoueurs().size() + "/" + gameService.maxPlayers,
                        ChatColor.GREEN + "Attente de joueurs...",
                        "",
                        "",
                        "Plugin developpé par",
                        ChatColor.GOLD + "Vutivo");

    }
}