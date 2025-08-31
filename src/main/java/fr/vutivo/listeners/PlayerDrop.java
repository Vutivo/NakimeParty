package fr.vutivo.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDrop implements Listener {

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!(event.getItemDrop().getItemStack().getType() == Material.GOLDEN_APPLE)) {
            event.setCancelled(true);
        }
    }
}
