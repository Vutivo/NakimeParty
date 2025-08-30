package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreak implements Listener {
    private final GameService gameService;

    public PlayerBreak(GameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(gameService.getState() != fr.vutivo.game.State.PLAYING) {
            event.setCancelled(true);
            return;
        }
        if(gameService.isUnbreakable(event.getBlock())){
            event.setCancelled(true);
            return;
        }

        // Pour 1.8.8, on doit écouter l'événement de drop séparément
        // Ici on laisse le bloc se casser normalement
    }

    // Ajoutez ce second EventHandler pour gérer les drops
    @EventHandler
    public void onBlockDrop(BlockBreakEvent event) {
        if(gameService.getState() == fr.vutivo.game.State.PLAYING &&
                !gameService.isUnbreakable(event.getBlock())) {

            // Récupérer les drops avant qu'ils apparaissent
            Block block = event.getBlock();
            block.setType(Material.AIR); // Empêche le drop automatique
            event.setCancelled(true);
        }
    }




}
