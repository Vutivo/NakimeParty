package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlace implements Listener {
    private final GameService gameService;

    public PlayerPlace (GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        // Sauvegarde le bloc et le supprime après 45 secondes
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        Material blockType = block.getType();

        // Programmer la suppression du bloc après 45 secondes (45 * 20 = 900 ticks)
        Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (blockLocation.getBlock().getType() == blockType) {
                    blockLocation.getBlock().setType(org.bukkit.Material.AIR);
                }
            }
        }, 20*45);
    }
}
