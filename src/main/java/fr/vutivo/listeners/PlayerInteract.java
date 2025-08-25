package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class PlayerInteract implements Listener {
    private final GameService gameService;

    public PlayerInteract(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (gameService.getState() != State.PLAYING) return;
        if (event.getItem() == null) return;
        Action action = event.getAction();

        Joueur joueur = gameService.getJoueur(event.getPlayer());
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                //Nezuko - Rage du Démon
                if (item.getType() == Material.NETHER_STAR && "§cRage du Démon".equals(name)) {

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
                                joueur.setResistance(joueur.getResistance() - 10);
                                gameService.reloadEffectScoreboard(joueur);
                                joueur.getPlayer().sendMessage("§cLa §o§cRage du Démon§r§c s'est terminée, vous perdez 10% de resistance et 10% de force.");
                            }, 20 * 15); // 15 secondes


                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé la §o§cRage du Démon§r§c !");
                        }
                    }
                    //Zenitsu - Frappe Foudroyante
                } else if (item.getType() == Material.NETHER_STAR && "§eFrappe Foudroyante".equals(name)) {
                    if (joueur != null && joueur.getRole() == Role.Zenitsu) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            joueur.getPlayer().sendMessage("§eVous avez activé la §o§eFrappe Foudroyante§r§e !");
                            joueur.getRoleItem().setPower(0);
                            joueur.setInDash(true);

                            // Calcul du dash (10 blocs devant le joueur)
                            Vector direction = joueur.getPlayer().getLocation().getDirection().normalize();
                            double dashPower = 5.0;
                            joueur.getPlayer().setVelocity(direction.multiply(dashPower));


                            Bukkit.getScheduler().runTaskLater(gameService.getInstance(), () -> {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Player player = joueur.getPlayer();
                                        if (player == null || !player.isOnline() || player.isDead()) {
                                            cancel();
                                            joueur.setInDash(false);
                                            return;
                                        }

                                        if (player.isOnGround()) {
                                            World world = player.getWorld();

                                            // 1. Liste des joueurs autour
                                            List<Player> nearbyPlayers = new ArrayList<>();
                                            for (Player p : world.getPlayers()) {
                                                if (!p.equals(player) && p.getLocation().distance(player.getLocation()) <= 3) {
                                                    nearbyPlayers.add(p);
                                                }
                                            }

                                            // 2. Activer l'état "immunité explosion" pour ces joueurs
                                            for (Player p : nearbyPlayers) {
                                                Joueur j = gameService.getJoueur(p);
                                                if (j != null) j.setIgnoreExplosion(true);
                                            }

                                            // 3. Explosion + foudre
                                            world.createExplosion(player.getLocation(), 3f, false); // casse les blocs
                                            world.strikeLightningEffect(player.getLocation());

                                            // 4. Retirer l'immunité et appliquer 1 cœur de dégâts
                                            for (Player p : nearbyPlayers) {
                                                Joueur j = gameService.getJoueur(p);
                                                if (j != null) {
                                                    j.setIgnoreExplosion(false);
                                                    double newHealth = Math.max(0.5, p.getHealth() - 2.0);
                                                    p.setHealth(newHealth);
                                                    p.sendMessage("§eVous avez été frappé par un éclair de Zenitsu !");
                                                }
                                            }

                                            joueur.setInDash(false);
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(gameService.getInstance(), 0L, 2L);
                            }, 10L);


                        } else {
                            joueur.getPlayer().sendMessage("§eVous avez déjà utilisé la §o§eFrappe Foudroyante§r§e !");
                        }
                    }
                    //Inosuke - Sixième Sens
                } else if (item.getType() == Material.NETHER_STAR && "§bSixième Sens".equals(name)) {
                    if (joueur != null && joueur.getRole() == Role.Inosuke) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            joueur.getPlayer().sendMessage("§bVous avez activé le §o§bSixième Sens§r§b !");
                            joueur.getRoleItem().setPower(0);
                            gameService.viewHpPlayer(joueur.getPlayer());

                           Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                               @Override
                               public void run() {
                                  gameService.disableHealthDisplay(joueur.getPlayer());
                               }
                           }, 20 * 60); // 1 minute





                        }


                    }

                //Gyomei - Onde de choc
                } // Gyomei - Onde de choc
                else if (item.getType() == Material.STONE_AXE && "§bOnde de choc".equals(name) && action == Action.RIGHT_CLICK_BLOCK) {
                    if (joueur != null && joueur.getRole() == Role.Gyomei) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé l'§o§bOnde de choc§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());

                               //Cree une liste des joueurs autour
                                List<Player> nearbyPlayers = new ArrayList<>();
                                for (Player p : joueur.getPlayer().getWorld().getPlayers()) {
                                    if (!p.equals(joueur.getPlayer()) && p.getLocation().distance(joueur.getPlayer().getLocation()) <= 5) {
                                        nearbyPlayers.add(p);
                                    }
                                }

                                //  appliquer 2 cœurs de dégâts && knockback
                                for (Player p : nearbyPlayers) {
                                    Joueur j = gameService.getJoueur(p);
                                    if (j != null) {
                                        double newHealth = Math.max(0.5, p.getHealth() - 4.0);
                                        p.setHealth(newHealth);
                                        Vector knockback = p.getLocation().toVector().subtract(joueur.getPlayer().getLocation().toVector()).normalize().multiply(1.5);
                                        knockback.setY(1.5); // Ajouter une composante verticale pour un effet de knockback plus réaliste
                                        p.setVelocity(knockback);
                                        //Envoyer un message au joueur
                                        p.sendMessage("§eVous avez été frappé par l'onde de choc de Gyomei !");
                                    }
                                }


                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }
                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }
                }



            }
        }
    }
}