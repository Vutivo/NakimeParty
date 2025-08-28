package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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



}