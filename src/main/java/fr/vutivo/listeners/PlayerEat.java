package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.roles.Role;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerEat implements Listener {
    private final GameService gameService;

    public PlayerEat(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (gameService.getState() != fr.vutivo.game.State.PLAYING) {
            event.setCancelled(true);
            return;
        }

        if (event.getItem().getType() == org.bukkit.Material.GOLDEN_APPLE) {
            Player player = event.getPlayer();
            Location playerLoc = player.getLocation();
            boolean yoriichiNearby = false;

            for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.equals(player)) continue;

                double distance = playerLoc.distance(onlinePlayer.getLocation());

                if (distance <= 5.0) {
                    Joueur joueur = gameService.getJoueur(onlinePlayer);

                    if (joueur != null && joueur.getRole() == Role.Yoriichi && joueur.isCanUseEternalFire()) {
                        yoriichiNearby = true;
                        break;
                    }
                }
            }

            if (yoriichiNearby) {
                event.setCancelled(true);

                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.REGENERATION,
                        100, // 5 secondes
                        1    // Niveau II
                ));

                org.bukkit.inventory.ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getAmount() > 1) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                } else {
                    player.setItemInHand(null);
                }
            }
        }
    }
}
