package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;


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

        World world = joueur.getPlayer().getWorld();
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
                            joueur.setRageMode(true);
                            gameService.reloadEffectScoreboard(joueur);

                            Bukkit.getScheduler().runTaskLater(gameService.getInstance(), () -> {
                                joueur.setSpeed(joueur.getSpeed() - 10);
                                joueur.setStrength(joueur.getStrength() - 20);
                                joueur.setResistance(joueur.getResistance() - 10);
                                gameService.reloadEffectScoreboard(joueur);
                                joueur.setRageMode(false);
                                joueur.getPlayer().sendMessage("§cLa §o§cRage du Démon§r§c s'est terminée, vous perdez 10% de resistance et 10% de force.");
                            }, 20 * 15); // 15 secondes


                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé la §o§cRage du Démon§r§c !");
                        }
                    }
                    //Zenitsu - Frappe Foudroyante
                } else if (item.getType() == Material.NETHER_STAR && "§eFrappe Foudroyante".equals(name)) {
                    if (joueur.getRole() == Role.Zenitsu) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            joueur.getPlayer().sendMessage("§eVous avez activé la §o§eFrappe Foudroyante§r§e !");
                            joueur.getRoleItem().setPower(0);
                            joueur.setInDash(true);

                            // Calcul du dash ( environ 10 blocs devant le joueur)
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
                                                if (j != null) j.setIgnoreDamage(true);
                                            }

                                            // 3. Explosion + foudre
                                            gameService.createCustomExplosion(player.getLocation(), 1.5f);
                                            world.strikeLightningEffect(player.getLocation());

                                            // 4. Retirer l'immunité et appliquer 1 cœur de dégâts
                                            for (Player p : nearbyPlayers) {
                                                Joueur j = gameService.getJoueur(p);
                                                if (j != null) {
                                                    j.setIgnoreDamage(false);
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
                    if (joueur.getRole() == Role.Inosuke) {
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


                } // Gyomei - Onde de choc
                else if (item.getType() == Material.STONE_AXE && "§bOnde de choc".equals(name) && action == Action.RIGHT_CLICK_BLOCK) {
                    if (joueur.getRole() == Role.Gyomei) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé l'§o§bOnde de choc§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());

                                // Cree une liste des joueurs autour
                                List<Player> nearbyPlayers = new ArrayList<>();
                                for (Player p : joueur.getPlayer().getWorld().getPlayers()) {
                                    if (!p.equals(joueur.getPlayer()) &&
                                            p.getLocation().distance(joueur.getPlayer().getLocation()) <= 8) {
                                        nearbyPlayers.add(p);
                                    }
                                }

                                // On ajoute Gyomei lui-même pour qu’il ait ignoreDamage
                                nearbyPlayers.add(joueur.getPlayer());

                                // Activer l'état "immunité explosion" pour ces joueurs
                                for (Player p : nearbyPlayers) {
                                    Joueur j = gameService.getJoueur(p);
                                    if (j != null) j.setIgnoreDamage(true);
                                }


                                //Prendre le blocs ou le joueur a click droit
                                Location targetBlock = event.getClickedBlock().getLocation();
                                // Explosion
                                gameService.createCustomExplosion(targetBlock, 2f);

                                // appliquer 2 cœurs de dégâts && knockback
                                for (Player p : nearbyPlayers) {
                                    Joueur j = gameService.getJoueur(p);
                                    if (j != null) {
                                        if (!p.equals(joueur.getPlayer())) { // ✅ Gyomei ne prend pas ses propres dégâts custom
                                            double newHealth = Math.max(0.5, p.getHealth() - 4.0);
                                            p.setHealth(newHealth);

                                            // kb vertical + un peu de recul
                                            Vector knockback = p.getLocation().toVector()
                                                    .subtract(joueur.getPlayer().getLocation().toVector())
                                                    .normalize()
                                                    .multiply(1)
                                                    .setY(1.5);
                                            p.setVelocity(knockback);

                                            p.sendMessage("§eVous avez été frappé par l'onde de choc de Gyomei !");
                                        }

                                        // réinitialiser ignoreDamage pour tout le monde
                                        j.setIgnoreDamage(false);
                                    }
                                }


                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }
                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }
                    // Tomioka - Sphère d’Eau
                } else if (item.getType() == Material.NETHER_STAR && "§bSphère d’Eau".equals(name)) {
                    if (joueur.getRole() == Role.Tomioka) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            Joueur target = gameService.getTargetJoueur(joueur, 10);

                            if (target != null) {
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bSphère d’Eau§r§b sur " + target.getPlayer().getName() + " !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());

                                // Création de la sphère d’eau (pleine)
                                List<Location> sphereLocations = new ArrayList<>();
                                HashMap<Location, Material> latestBlocks = new HashMap<>();

                                Location center = joueur.getPlayer().getLocation().clone().add(0, 8, 0); // bulle centrée un peu au-dessus
                                int radius = 6;

                                for (int x = -radius; x <= radius; x++) {
                                    for (int y = -radius; y <= radius; y++) {
                                        for (int z = -radius; z <= radius; z++) {
                                            if (x * x + y * y + z * z <= radius * radius) {
                                                Location loc = center.clone().add(x, y, z);

                                                Block block = loc.getBlock();
                                                //si lze blocs fait partie de la list bloc indestructible on le laisse
                                                if (!gameService.isUnbreakable(block)) {
                                                    // Sauvegarde du bloc d’origine
                                                    latestBlocks.put(loc, block.getType());

                                                    // Pose de l’eau
                                                    block.setType(Material.WATER);

                                                    sphereLocations.add(loc);
                                                }


                                            }
                                        }
                                    }
                                }
                                //on teleporte le joueur vers le bas de la bulle
                                Location tpLocation = center.clone().add(-1, -radius + 1, -1);
                                tpLocation.setPitch(joueur.getPlayer().getLocation().getPitch());
                                tpLocation.setYaw(joueur.getPlayer().getLocation().getYaw());
                                joueur.getPlayer().teleport(tpLocation);
                                //on teleporte le joueur ciblé au centre de la bulle
                                Location tpLocationTarget = center.clone().add(+1, -1, +1);
                                tpLocationTarget.setPitch(target.getPlayer().getLocation().getPitch());
                                tpLocationTarget.setYaw(target.getPlayer().getLocation().getYaw());
                                target.getPlayer().teleport(tpLocationTarget);

                                Location locTarget = target.getPlayer().getLocation();
                                Location locJoueur = joueur.getPlayer().getLocation();

                                // Tourner joueur vers target
                                locJoueur.setDirection(locTarget.toVector().subtract(locJoueur.toVector()));
                                joueur.getPlayer().teleport(locJoueur);

                                // Tourner target vers joueur
                                locTarget.setDirection(locJoueur.toVector().subtract(locTarget.toVector()));
                                target.getPlayer().teleport(locTarget);

                                // Retirer la sphère d’eau après un certain temps (par exemple, 10 secondes)
                                Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        // Retirer la sphère d’eau et restaurer les blocs d’origine
                                        for (Location loc : sphereLocations) {
                                            Block block = loc.getBlock();
                                            Material originalMaterial = latestBlocks.get(loc);
                                            if (originalMaterial != null) {
                                                block.setType(originalMaterial);
                                            } else {
                                                block.setType(Material.AIR); // Par sécurité
                                            }
                                        }

                                    }
                                }, 20 * 10); // 10 secondes


                            } else {
                                joueur.getPlayer().sendMessage("§cAucun joueur ciblé à portée !");
                            }
                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }
                    //Sanemi - Rafale Violente
                } else if (item.getType() == Material.NETHER_STAR && "§bRafale Violente".equals(name)) {
                    if (joueur.getRole() == Role.Sanemi) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            // on verifie que le joueur n'est pas en cooldown
                            if (joueur.getCooldown() == 0) {
                                //On enleve une utilisation
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bRafale Violente§r§b !");


                                // Calcul du dash
                                Vector direction = joueur.getPlayer().getLocation().getDirection().normalize();
                                double dashPower = 7.0;
                                joueur.getPlayer().setVelocity(direction.multiply(dashPower));
                                joueur.setInDash(true);
                                Bukkit.broadcastMessage("Dash commencé");

                                long startTick = System.currentTimeMillis();
                                Set<Player> alreadyHit = new HashSet<>();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Player p = joueur.getPlayer();

                                        // sécurité si joueur null
                                        if (p == null || !p.isOnline()) {
                                            return;
                                        }

                                        // si le dash a duré au moins 10 ticks (~0.5s) ET que le joueur est au sol
                                        long elapsedTicks = (System.currentTimeMillis() - startTick) / 50;
                                        if (elapsedTicks > 10 && p.isOnGround()) {
                                            joueur.setInDash(false);
                                            p.sendMessage("§cDash terminé !");
                                            cancel();
                                            return;
                                        }

                                        // logique du dash ici (KB, etc.)
                                        for (Player target : p.getWorld().getPlayers()) {
                                            if (!target.equals(p)
                                                    && target.getLocation().distance(p.getLocation()) <= 4
                                                    && !alreadyHit.contains(target)) {

                                                // knockback
                                                Vector knockback = target.getLocation().toVector()
                                                        .subtract(p.getLocation().toVector())
                                                        .normalize()
                                                        .multiply(1)
                                                        .setY(1.5);
                                                target.setVelocity(knockback);

                                                // message + dégâts
                                                target.sendMessage("§eVous avez été frappé par la rafale violente de Sanemi !");
                                                double newHealth = Math.max(0.5, target.getHealth() - 2.0);
                                                target.setHealth(newHealth);

                                                // marquer comme touché
                                                alreadyHit.add(target);
                                            }
                                        }
                                    }
                                }.runTaskTimer(gameService.getInstance(), 0L, 1L);

                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }

                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }

                    }
                    //Mitsuri - Flamme de l’Amour
                } else if (item.getType() == Material.NETHER_STAR && "§bFlamme de l’Amour".equals(name)) {
                    if (joueur.getRole() == Role.Mitsuri) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {

                                Joueur target = gameService.getTargetJoueur(joueur, 10);
                                if (target != null) {
                                    joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                    joueur.setCooldown(joueur.getRoleItem().getCooldown());

                                    Joueur jTarget = gameService.getJoueur(target.getPlayer());
                                    jTarget.setCanDamageMitsuri(false);
                                    joueur.getPlayer().sendMessage("§dVous avez activé la §o§dFlamme de l’Amour§r§d sur " + target.getPlayer().getName() + " !");
                                    jTarget.getPlayer().sendMessage("§dVous tombez sous le charme de Mitsuri pendant 10 secondes !");

                                    Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            jTarget.setCanDamageMitsuri(true);
                                            jTarget.getPlayer().sendMessage("§dLe charme de Mitsuri s'est dissipé !");

                                        }
                                    }, 20 * 10);


                                }


                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());
                            }


                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }
                    //Nakime - Biwa
                } else if (item.getType() == Material.NETHER_STAR && "§bBiwa".equals(name)) {
                    if (joueur.getRole() == Role.Nakime) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bBiwa§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());

                                //On recupere la list des point de spawn du début avec SpawnManager
                                List<Location> spawns = gameService.getSpawnManager().getSpawns(gameService.MAP_NAME);

                                for (Joueur Slayer : gameService.getSlayers()) {
                                    //On téléporte chaque slayer sur un spawn aléatoire
                                    Collections.shuffle(spawns);
                                    Location spawn = spawns.get(0);
                                    Slayer.getPlayer().teleport(spawn);
                                    Slayer.getPlayer().sendMessage("§bVous avez été téléporté par Nakime !");
                                    //On enlève le spawn de la liste pour pas que deux slayers spawn au même endroit
                                    spawns.remove(0);
                                }

                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }
                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }


                    //Doma - Sérénité Mortelle
                } else if (item.getType() == Material.NETHER_STAR && "§bSérénité Mortelle".equals(name)) {
                    if (joueur.getRole() == Role.Doma) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bSérénité Mortelle§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());


                                new BukkitRunnable() {
                                    int timer = 30; // Durée en secondes

                                    @Override
                                    public void run() {
                                        timer--;

                                        // Quand le timer finit -> on enlève l'effet à tout le monde
                                        if (timer <= 0) {
                                            for (Joueur slayer : gameService.getSlayers()) {
                                                if (slayer.isDomaSlowed()) {
                                                    slayer.setSpeed(slayer.getSpeed() + 20);
                                                    slayer.setDomaSlowed(false);
                                                    slayer.getPlayer().sendMessage("§bVous n'êtes plus ralenti par la sérénité mortelle de Doma.");
                                                    gameService.reloadEffectScoreboard(slayer);
                                                }
                                            }
                                            joueur.getPlayer().sendMessage("§bLa §o§bSérénité Mortelle§r§b de Doma s'est dissipée !");
                                            cancel();
                                            return;
                                        }

                                        // Effet permanent pendant les 30s
                                        for (Joueur slayer : gameService.getSlayers()) {
                                            Player p = slayer.getPlayer();
                                            double distance = p.getLocation().distance(joueur.getPlayer().getLocation());

                                            // S'il est dans la zone
                                            if (distance <= 20) {
                                                if (!slayer.isDomaSlowed()) {
                                                    slayer.setSpeed(slayer.getSpeed() - 20);
                                                    slayer.setDomaSlowed(true);
                                                    p.sendMessage("§bVous êtes ralenti par la sérénité mortelle de Doma (-20% de vitesse) !");
                                                    gameService.reloadEffectScoreboard(slayer);
                                                }
                                            }
                                            // S'il sort de la zone mais était ralenti
                                            else if (slayer.isDomaSlowed()) {
                                                slayer.setSpeed(slayer.getSpeed() + 20);
                                                slayer.setDomaSlowed(false);
                                                p.sendMessage("§bVous n'êtes plus ralenti par la sérénité mortelle de Doma.");
                                                gameService.reloadEffectScoreboard(slayer);
                                            }
                                        }
                                    }
                                }.runTaskTimer(gameService.getInstance(), 0L, 20L);


                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());
                            }


                        } else {
                            joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());
                        }


                    }

                    //Gyokko - Transposition
                } else if (item.getType() == Material.NETHER_STAR && "§bTransposition".equals(name)) {
                    if (joueur.getRole() == Role.Gyokko) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);

                                //Systeme de tp
                                List<Location> spawns = gameService.getSpawnManager().getSpawns(gameService.MAP_NAME);
                                Collections.shuffle(spawns);
                                Location spawn = spawns.get(0);
                                joueur.getPlayer().teleport(spawn);
                                joueur.setStrength(joueur.getStrength() + 10);
                                gameService.reloadEffectScoreboard(joueur);

                                Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        joueur.setStrength(joueur.getStrength() - 10);
                                        gameService.reloadEffectScoreboard(joueur);
                                    }
                                }, 20 * 10); // 10 secondes

                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }

                        } else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }

                    //Gyutaro - Houe de Souffrance
                } else if (item.getType() == Material.DIAMOND_HOE && " §bHoue de Souffrance".equals(name)) {
                    if (joueur.getRole() == Role.Gyutaro) {
                        Joueur target = gameService.getTargetJoueur(joueur, 10);
                        if (target != null) {
                            if (joueur.getCooldown() == 0) {
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                target.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1)); // Wither 1 pendant 10 secondes
                                target.getPlayer().setMaxHealth(Math.max(0.5, target.getPlayer().getMaxHealth() - 2)); // Perte permanente de 1 cœur
                                target.getPlayer().sendMessage("§cVous avez été touché par la Houe de Souffrance de Gyutaro ! Vous perdez 1 cœur pendant 2 minutes et êtes affecté par Wither 2 pendant 10 secondes.");
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bHoue de Souffrance§r§b sur " + target.getPlayer().getName() + " !");
                                joueur.getPlayer().setMaxHealth(joueur.getPlayer().getMaxHealth() + 2); // Gain permanent de 1 cœur

                                Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        target.getPlayer().setMaxHealth(Math.min(20.0, target.getPlayer().getMaxHealth() + 2)); // Récupération du cœur perdu
                                        target.getPlayer().sendMessage("§aVous avez récupéré le cœur perdu de la Houe de Souffrance de Gyutaro.");
                                        joueur.getPlayer().setMaxHealth(Math.max(0.5, joueur.getPlayer().getMaxHealth() - 2)); // Perte du cœur gagné
                                        joueur.getPlayer().sendMessage("§cVous perdez le cœur gagné de la Houe de Souffrance de Gyutaro.");

                                    }
                                }, 20 * 120); // 2 minutes
                            } else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());

                            }

                        } else {
                            joueur.getPlayer().sendMessage("§cAucun joueur ciblé à portée !");
                        }

                    }
                    //Kaigaku - Foudre Déchaînée

                } else if (item.getType() == Material.NETHER_STAR && "§bFoudre Déchaînée".equals(name)) {
                    if (joueur.getRole() == Role.Kaigaku) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bFoudre Déchaînée§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());

                                // Cree une liste des joueurs autour
                                List<Player> nearbyPlayers = new ArrayList<>();
                                for (Player p : world.getPlayers()) {
                                    if (!p.equals(joueur.getPlayer()) &&
                                            p.getLocation().distance(joueur.getPlayer().getLocation()) <= 15) {
                                        nearbyPlayers.add(p);
                                    }
                                }


                                // Activer l'état "immunité" pour ces joueurs
                                for (Player p : nearbyPlayers) {
                                    Joueur j = gameService.getJoueur(p);
                                    if (j != null) j.setIgnoreDamage(true);
                                    world.strikeLightningEffect(p.getLocation());
                                }



                                // appliquer 2 cœurs de dégâts && knockback
                                for (Player p : nearbyPlayers) {
                                    Joueur j = gameService.getJoueur(p);
                                    if (j != null) {
                                        if (!p.equals(joueur.getPlayer())) { // ✅ Kaigaku ne prend pas ses propres dégâts custom
                                            double newHealth = Math.max(0.5, p.getHealth() - 4.0);
                                            p.setHealth(newHealth);
                                            j.setIgnoreDamage(false);

                                            p.sendMessage("§eVous avez été frappé par la foudre déchaînée de Kaigaku !");
                                        }

                                    }
                                }
                            }else {
                                joueur.getPlayer().sendMessage("§cPouvoir en cooldown !" + joueur.getCooldown());
                            }

                        }else {
                            joueur.getPlayer().sendMessage("§cVous avez déjà utilisé ce pouvoir !");
                        }
                    }

                    //Yoriichi - Flamme Éternelle
                } else if (item.getType() == Material.NETHER_STAR && "§bFlamme Éternelle".equals(name)) {
                    if (joueur.getRole() == Role.Yoriichi) {
                        if (joueur.getRoleItem().getPower() > 0) {
                            if (joueur.getCooldown() == 0) {
                                joueur.getPlayer().sendMessage("§bVous avez activé la §o§bFlamme Éternelle§r§b !");
                                joueur.getRoleItem().setPower(joueur.getRoleItem().getPower() - 1);
                                joueur.setCooldown(joueur.getRoleItem().getCooldown());
                                joueur.setCanUseEternalFire(true);

                                Bukkit.getScheduler().runTaskLater(gameService.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        joueur.setCanUseEternalFire(false);
                                        joueur.getPlayer().sendMessage("§bLa §o§bFlamme Éternelle§r§b de Yoriichi s'est dissipée !");

                                    }
                                }, 20 * 60); // 1 minute
                            }



                                    }
                                }

                }
            }
        }
    }
}
