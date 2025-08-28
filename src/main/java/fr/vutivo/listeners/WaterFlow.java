package fr.vutivo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class WaterFlow implements Listener {

    @EventHandler
    public void onWaterFlow(BlockFromToEvent event) {
        if( event.getBlock().isLiquid() ) {
            event.setCancelled(true);
        }
        // This method is intentionally left blank to prevent water flow events.
    }
}
