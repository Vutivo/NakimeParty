package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;



public class PlayerInteract implements Listener {
    private final GameService gameService;

    public PlayerInteract(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (gameService.getState() != State.PLAYING) return;
        if (event.getItem() == null) return;
        Action action = event.getAction();

        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                //Nezuko - Rage du Démon
                if (item.getType() == Material.NETHER_STAR && "§cRage du Démon".equals(name)) {

                    Joueur joueur = gameService.getJoueur(event.getPlayer());
                    if (joueur != null && joueur.getRole() == Role.Nezuko) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            joueur.getPlayer().sendMessage("§cVous avez activé la §o§cRage du Démon§r§c !");
                            joueur.getRoleItem().setPower(0);
                            joueur.setSpeed(joueur.getSpeed() + 10);
                            joueur.setStrength(joueur.getStrength() + 10);
                            gameService.reloadEffectScoreboard(joueur);

                            Bukkit.getScheduler().runTaskLater(gameService.getInstance(), () -> {
                                joueur.setSpeed(joueur.getSpeed() - 10);
                                joueur.setStrength(joueur.getStrength() - 20);
                                joueur.setResistance(joueur.getResistance()-10);
                                gameService.reloadEffectScoreboard(joueur);
                                joueur.getPlayer().sendMessage("§cLa §o§cRage du Démon§r§c s'est terminée, vous perdez 10% de resistance et 10% de force.");
                            }, 20 * 15); // 15 secondes


                        }
                    }
                }

            }


        }


    }
}
