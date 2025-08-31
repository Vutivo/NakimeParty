package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PlayerDamage implements Listener {
    private final GameService gameService;
    private final Random random = new Random();

    public PlayerDamage(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Vérifier l'état du jeu
        if (gameService.getState() != State.PLAYING) {
            event.setCancelled(true);
            return;
        }

        // Vérifier que la victime est un joueur
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Joueur jVictim = gameService.getJoueur(victim);
        if (jVictim == null) return;


        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Joueur jAttacker = gameService.getJoueur(attacker);

            if (jAttacker == null) return;

            // Calculer les nouveaux dégâts
            double newDamage = calculateDamage(jAttacker, jVictim, event.getDamage());
            event.setDamage(newDamage);

            // Appliquer les effets
            applyTanjiroRageMode(jAttacker, victim);
            applyZenitsuThunderStrike(jAttacker, victim);
            applyShinobuPoison(jAttacker, victim);
            applyMuzanBlood(jAttacker, victim);
            applyYoriichiSunBreath(jAttacker, victim);
            //On rergarde si l'attaquant de Mitsuri est charmé
            if(jVictim.getRole()== Role.Mitsuri && !jAttacker.isCanDamageMitsuri()){
                event.setCancelled(true);
            }



        }


        else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                Joueur jShooter = gameService.getJoueur(shooter);

                if (jShooter != null) {
                    // Appliquer les effets (si applicables aux flèches)
                    applyShinobuPoison(jShooter, victim);

                }
            }
        }
    }


    @EventHandler
    public void onPlayerDamageNoEntity(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Joueur joueur = gameService.getJoueur((Player) event.getEntity());
            // Vérifier l'état du jeu
            if (gameService.getState() != State.PLAYING) {
                event.setCancelled(true);
                return; // Important : arrêter l'exécution si le jeu n'est pas en cours
            }
            //empecher les dégats de chute
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (joueur != null && joueur.isInDash()) {
                event.setCancelled(true);
                return;

            }
            // Empêcher les dégâts si le joueur ignore les dégâts
            if (joueur != null && joueur.isIgnoreDamage()) {
                event.setCancelled(true);

            }
        }
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getEntity();

        if (!(arrow.getShooter() instanceof Player)) return;
        Player shooter = (Player) arrow.getShooter();
        Joueur jShooter = gameService.getJoueur(shooter);
        if (jShooter == null) return;

        // Vérifie si le joueur peut utiliser son arc et si l'item s'appelle "Arc explosif"
        if (!jShooter.isCanUseBow()) {
            shooter.sendMessage("§cVotre arc est en cooldown, veuillez patienter." +
                    " Temps restant : " + jShooter.getCooldown() + " secondes.");
            return;
        }
        if (shooter.getItemInHand() == null
                || !shooter.getItemInHand().hasItemMeta()
                || !shooter.getItemInHand().getItemMeta().hasDisplayName()) return;

        String name = shooter.getItemInHand().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase("§bArc explosif")) return;

        // Explosion (pas de dégâts aux blocs)
        Location loc = arrow.getLocation();
       gameService.createCustomExplosion(loc,2f);
       jShooter.setCanUseBow(false);

        // Dégâts aux joueurs proches
        double radius = 3.0;
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (entity instanceof Player) {
                Joueur target = gameService.getJoueur((Player) entity);
                if (target != null && target.isAlive() && !target.equals(jShooter)) {
                    double newHealth = Math.max(0.5, target.getPlayer().getHealth() - 4); // -2 coeurs
                    target.getPlayer().setHealth(newHealth);
                    target.getPlayer().sendMessage("§cTu as été touché par une flèche explosive !");
                }
            }
        }

        // Supprimer la flèche
        arrow.remove();
        // Démarrer le cooldown de l'arc

        jShooter.setCooldown(jShooter.getRoleItem().getCooldown());
        new BukkitRunnable(){

            int cooldown = jShooter.getCooldown();
            @Override
            public void run() {
                if (cooldown <= 0) {
                    jShooter.setCanUseBow(true);
                    shooter.sendMessage("§aVotre arc est de nouveau utilisable !");
                    cancel();
                } else {
                    cooldown--;

                }


            }
        }.runTaskTimer(gameService.getInstance(), 0L, 20L);

    }


    private double calculateDamage(Joueur attacker, Joueur victim, double baseDamage) {
        // Bonus de force en pourcentage (20 de force = +20% de dégâts)
        double forceMultiplier = 1.0 + (attacker.getStrength() / 100.0);
        double attackDamage = baseDamage * forceMultiplier;

        // Réduction des dégâts en pourcentage (20 de résistance = -20% de dégâts)
        double resistanceReduction = victim.getResistance() / 100.0;
        double finalDamage = attackDamage * (1.0 - resistanceReduction);

        // S'assurer que les dégâts ne deviennent pas négatifs
        return Math.max(finalDamage, 0.5); // Minimum 0.5 de dégât
    }
    private void applyTanjiroRageMode(Joueur joueur, Player victim) {
        Joueur jVictim = gameService.getJoueur(victim);
        if (joueur.getRole().equals(Role.Tanjiro) && joueur.getRageMode()) {
            int chance = random.nextInt(100);
            if (chance < 20 ) { // 20% de chance d'activer le mode rage
                //mettre en feu l'adversaiere
                // Tanjiro ne peut pas mettre en feu Doma
                if(jVictim.getRole().equals(Role.Doma)){
                    return;
                }
                victim.setFireTicks(80); // 4 secondes de feu (20 ticks = 1 seconde)

            }
        }

    }

    private void applyZenitsuThunderStrike(Joueur joueur, Player victim) {
        Joueur jVictim = gameService.getJoueur(victim);
        World world = victim.getWorld();
        if (joueur.getRole().equals(Role.Zenitsu)) {
            int chance = random.nextInt(100);
            if (chance < 15) { // 15% de chance d'invoquer un éclair
                if(jVictim.getRole().equals(Role.Doma)){
                    return;
                }
                // Infliger 1 cœur supplémentaire (2 points de vie)
                double additionalDamage = 2.0;
                world.strikeLightningEffect(victim.getLocation());
                victim.setHealth(Math.max(0.5, victim.getHealth() - additionalDamage));
                victim.sendMessage("§eUn éclair vous a frappé, infligeant §c1❤§e de dégat !");
            }
        }
    }
    private void applyShinobuPoison(Joueur joueur, Player victim) {
        Joueur jVictim = gameService.getJoueur(victim);
        if (joueur.getRole().equals(Role.Shinobu)) {
            if (jVictim.getRole() == Role.Doma) return;
            int chance = random.nextInt(100);
            if (chance < 15) { // 15% de chance d'appliquer le poison
                // Appliquer l'effet de poison pendant 5 secondes (100 ticks)
                victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));

            }
        }
    }
    private void applyMuzanBlood(Joueur joueur, Player victim) {
        Joueur jVictim = gameService.getJoueur(victim);
        if (joueur.getRole().equals(Role.Muzan)) {
            int chance = random.nextInt(100);
            if (chance < 20) { // 20% de chance d'appliquer le poison
                //toute les sec faire 0.5 de défat au joueurs 3 fois sans potion
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(gameService.getInstance(), () -> {
                        if (jVictim.getRole() == Role.Doma) return;
                        victim.setHealth(Math.max(0.5, victim.getHealth() - 1));
                        victim.sendMessage("§cVous subissez les effets du sang de Muzan et perdez §c0.5❤§e !");
                    }, i * 20L); // 20 ticks = 1 seconde
                }

            }
        }
    }



    private void applyYoriichiSunBreath(Joueur joueur, Player victim) {
        Joueur jVictim = gameService.getJoueur(victim);

        if (joueur.getRole() != Role.Yoriichi) return;

        int chance = random.nextInt(100);
        if (chance >= 15) return; // 15% de chance
        if (jVictim.isNoEffect()) return;

        // Cas particuliers : si Nezuko ou Tanjiro en rage, Yoriichi ne peut rien faire
        if ((jVictim.getRole() == Role.Nezuko && jVictim.getRageMode()) ||
                (jVictim.getRole() == Role.Tanjiro && jVictim.getRageMode())) {
            return;
        }

        // On sauvegarde les stats actuelles
        int originalStrength = jVictim.getStrength();
        int originalResistance = jVictim.getResistance();
        int originalSpeed = jVictim.getSpeed();

        // On applique le vol d'effets (tout tombe à 0 sauf si déjà négatif)
        jVictim.setStrength(originalStrength < 0 ? originalStrength : 0);
        jVictim.setResistance(originalResistance < 0 ? originalResistance : 0);
        jVictim.setSpeed(originalSpeed < 0 ? originalSpeed : 0);
        jVictim.setNoEffect(true);

        gameService.reloadEffectScoreboard(jVictim);
        victim.sendMessage("§cVous subissez le souffle du soleil ! Vos effets sont désactivés pendant 10 secondes.");

        // On planifie le retour après 10 secondes
        Bukkit.getScheduler().runTaskLater(gameService.getInstance(), () -> {

            // ---- TANJIRO ----
            if (jVictim.getRole() == Role.Tanjiro) {
                if (jVictim.getRageMode()) {
                    // En rage -> il récupère ses vrais bonus
                    jVictim.setStrength(originalStrength);
                    jVictim.setSpeed(originalSpeed);
                } else {
                    // Hors rage -> vitesse 20%, force 0
                    jVictim.setStrength(0);
                    jVictim.setSpeed(20);
                }
                jVictim.setResistance(originalResistance);
            }

            // ---- NEZUKO ----
            else if (jVictim.getRole() == Role.Nezuko) {
                if (jVictim.getRageMode()) {
                    // En rage -> elle récupère tout normalement
                    jVictim.setStrength(originalStrength +10);
                    jVictim.setSpeed(originalSpeed +10);
                    jVictim.setResistance(originalResistance);
                } else {
                    if(jVictim.getRoleItem().getPower() == 1){
                        // Hors rage -> Sans avoir ult
                        jVictim.setStrength(20);    // 20% base
                        jVictim.setSpeed(0);        // pas de vitesse de base
                        jVictim.setResistance(0); // pas de résistance de base
                    } else {
                        // rage -> Apres avoir ult
                        jVictim.setStrength(10);    // 10% base
                        jVictim.setSpeed(0);        // pas de vitesse de base
                        jVictim.setResistance(-10); // -10% de résistance de base
                    }

                }
            }

            // ---- AUTRES ROLES ----
            else {
                jVictim.setStrength(originalStrength);
                jVictim.setResistance(originalResistance);
                jVictim.setSpeed(originalSpeed);
            }

            gameService.reloadEffectScoreboard(jVictim);
            victim.sendMessage("§aVos effets sont de nouveau actifs !");
            jVictim.setNoEffect(false);
        }, 200L); // 10 secondes
    }



}