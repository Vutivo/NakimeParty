package fr.vutivo.scoreboard;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.roles.Role;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabList implements Listener {
    private final GameService gameService;
    private Scoreboard scoreboard;

    public TabList(GameService gameService) {
        this.gameService = gameService;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Nettoie et crée les teams
        setupTeams();

        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(gameService.getInstance(), 20L, 20L);
    }

    private void setupTeams() {
        // Nettoie les anciennes teams
        cleanupTeams();

        // Crée les teams dans l'ordre de priorité pour le TabList
        Team slayerTeam = scoreboard.registerNewTeam("aslayers"); // 'a' pour être en premier
        slayerTeam.setPrefix(ChatColor.GREEN + "[Slayer] " + ChatColor.WHITE);

        Team demonTeam = scoreboard.registerNewTeam("bdemons"); // 'b' pour être en second
        demonTeam.setPrefix(ChatColor.RED + "[Demon] " + ChatColor.WHITE);

        Team soloTeam = scoreboard.registerNewTeam("csolos"); // 'c' pour être en dernier
        soloTeam.setPrefix(ChatColor.GOLD + "[Solo] " + ChatColor.WHITE);
    }

    private void cleanupTeams() {
        String[] teamNames = {"aslayers", "bdemons", "csolos", "slayers", "demons", "solos", "Slayer", "Demon", "Solo"};
        for (String teamName : teamNames) {
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                team.unregister();
            }
        }
    }

    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null && player.isOnline()) {
                updatePlayerNameTag(player);
                updatePlayerTab(player);
            }
        }
    }

    private void updatePlayerNameTag(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Joueur j = gameService.getJoueur(player);
        if (j == null || j.getRole() == null) {
            return;
        }

        // Retire le joueur de toutes les teams
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        // Cas spécial pour Nezuko : toujours un Demon sauf si révélée
        if (j.getRole() == Role.Nezuko) {
            if (gameService.revealNezuko) {
                // Nezuko révélée = Slayer
                Team slayerTeam = scoreboard.getTeam("aslayers");
                if (slayerTeam != null) {
                    slayerTeam.addEntry(player.getName());
                }
            } else {
                // Nezuko cachée = Demon
                Team demonTeam = scoreboard.getTeam("bdemons");
                if (demonTeam != null) {
                    demonTeam.addEntry(player.getName());
                }
            }
        } else {
            // Ajoute le joueur à la bonne team selon son camp
            Team targetTeam = null;
            switch (j.getRole().getCamp()) {
                case Slayer:
                    targetTeam = scoreboard.getTeam("aslayers");
                    break;
                case Demon:
                    targetTeam = scoreboard.getTeam("bdemons");
                    break;
                case Solo:
                    targetTeam = scoreboard.getTeam("csolos");
                    break;
            }

            if (targetTeam != null) {
                targetTeam.addEntry(player.getName());
            }
        }

        // Applique le scoreboard au joueur
        player.setScoreboard(scoreboard);
    }

    private void updatePlayerTab(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Joueur j = gameService.getJoueur(player);
        if (j == null || j.getRole() == null) {
            return;
        }

        try {
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            // Header simplifié
            String header = buildHeader();
            Object headerComponent = new ChatComponentText(header);

            // Footer avec les infos du joueur
            String footerText = ChatColor.GRAY + "Rôle: " + ChatColor.RESET + j.getRole().getName() +
                    ChatColor.GRAY + " --- " + ChatColor.RESET + j.getKills() +
                    (j.getKills() > 1 ? " kills" : " kill");
            Object footerComponent = new ChatComponentText(footerText);

            Field a = packet.getClass().getDeclaredField("a");
            Field b = packet.getClass().getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);
            a.set(packet, headerComponent);
            b.set(packet, footerComponent);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        } catch (Exception e) {
            // Erreur silencieuse
        }
    }

    private String buildHeader() {
        return ChatColor.RED + "Nakime" + ChatColor.DARK_RED + ChatColor.BOLD + "Party";
    }

    public void revealNezukoForAll(Player nezukoPlayer) {
        if (nezukoPlayer == null || !nezukoPlayer.isOnline()) {
            return;
        }

        // Force la mise à jour des nametags et du TabList pour tous
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != null && online.isOnline()) {
                updatePlayerNameTag(online);
                updatePlayerTab(online);
            }
        }

    }

    public void disable() {
        cleanupTeams();
    }
}