package fr.vutivo.listeners;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import fr.vutivo.game.State;
import fr.vutivo.roles.Camp;
import fr.vutivo.roles.Role;
import fr.vutivo.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDeath implements Listener {
    private final GameService gameService;

    public PlayerDeath(GameService service) {
        this.gameService = service;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameService.getState() != State.PLAYING) {
            return;
        }

        event.setDeathMessage(null);
        Player victim = event.getEntity();
        Player killer = victim.getKiller(); // Peut être null

        Joueur jKiller = gameService.getJoueur(killer);
        Joueur jVictim = gameService.getJoueur(victim);

        if (jKiller != null && jVictim != null) {
            jKiller.addKill();
            // Récompenses
            killer.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE, 2).build());


        }
        if(jKiller.getRole() == Role.Muzan){
          jKiller.getPlayer().setMaxHealth( jKiller.getPlayer().getMaxHealth() + 1);
          jKiller.setStrength(jKiller.getStrength()+3);
          gameService.reloadEffectScoreboard(jKiller);


        }
        if(jKiller.getRole() == Role.Kokushibo) {
            jKiller.getPlayer().setMaxHealth(jKiller.getPlayer().getMaxHealth() + 1);
            jKiller.getPlayer().setHealth(jKiller.getPlayer().getMaxHealth());
        }


        if (jVictim.getRole() == Role.Shinobu) {
            givePoisonForDemon(jVictim);

        }
        if(jVictim.getRole() == Role.Akaza){
            akazaExplosion(jVictim);

        }



        if(jVictim.getRole() ==Role.Nakime && jVictim.isNakimeRevived()){
            jVictim.setNakimeRevived(false);
            victim.spigot().respawn();
            victim.setGameMode(GameMode.SURVIVAL);
            //on tp le joueur a un point de spawn prédéfini avec SpawnManager
            List<Location> spawns = gameService.getSpawnManager().getSpawns(gameService.MAP_NAME);
            Collections.shuffle(spawns);
            victim.teleport(spawns.get(0));


            for (Joueur Demon : gameService.getDemons()) {
                if(Demon.getRole() == Role.Doma) continue;
                if(!Demon.isAlive()) continue;
                Demon.getPlayer().sendMessage("§cNakime a été tuée, vous perdez donc 1 coueur permanent.");
                Demon.getPlayer().setMaxHealth(Math.max(1, Demon.getPlayer().getMaxHealth() - 2));
            }
            return;
        }

        if (jVictim == null) {
            return;
        }
        Location location = victim.getLocation();
        victim.spigot().respawn();
        victim.teleport(location);
        victim.setGameMode(GameMode.SPECTATOR);
        jVictim.setAlive(false);

        Bukkit.broadcastMessage("§8==========================");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7" + victim.getName() + " est mort. Il était " + jVictim.getRole().getName());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8==========================");

        // Retirer du camp
        gameService.removeJoueur(jVictim);
        gameService.getScoreBoardManager().updateLineAll(2, ChatColor.WHITE + "Joueurs : " + ChatColor.AQUA + gameService.getJoueurs().size() + ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxJoueurs);
        Camp victimCamp = jVictim.getCamp();
        switch (victimCamp) {
            case Slayer:
                gameService.removeSlayer(jVictim);
                gameService.getScoreBoardManager().updateLineAll(3, ChatColor.WHITE + "Slayers : " + ChatColor.AQUA + gameService.getSlayers().size() + ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxSlayers);
                break;
            case Demon:
                gameService.removeDemon(jVictim);
                gameService.getScoreBoardManager().updateLineAll(4, ChatColor.WHITE + "Démons : " + ChatColor.AQUA + gameService.getDemons().size() + ChatColor.WHITE + " / " + ChatColor.AQUA + gameService.maxDemons);

                break;
        }

    }

    private void givePoisonForDemon(Joueur joueur) {
        List<Player> nearbyPlayers = new ArrayList<>();

        // Pour chaque démon
        for (Joueur demon : gameService.getDemons()) {
            // Pour chaque joueur en jeu
            for (Joueur j : gameService.getJoueurs()) { // Utilisez getJoueurs() au lieu de getPlayers()
                // Vérifications
                if (j.getRole() == Role.Doma) continue; // Exclure Doma
                if (j.getPlayer().equals(joueur.getPlayer())) continue; // Ne pas se cibler soi
                if (!j.getPlayer().getWorld().equals(demon.getPlayer().getWorld())) continue; // Même monde

                // Vérifier la distance (8 blocs du démon)
                if (j.getPlayer().getLocation().distance(demon.getPlayer().getLocation()) <= 8) {
                    if (!nearbyPlayers.contains(j.getPlayer())) { // Éviter les doublons
                        nearbyPlayers.add(j.getPlayer());
                    }
                }
            }
        }

        // Appliquer le poison (sans remove dans la boucle)
        for (Player player : nearbyPlayers) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));

        }
    }
    private  void akazaExplosion(Joueur joueur){
        for (Joueur j : gameService.getJoueurs()) {
            if (j.getPlayer().equals(joueur.getPlayer())) continue; // Ne pas se cibler soi
            if (!j.getPlayer().getWorld().equals(joueur.getPlayer().getWorld())) continue; // Même monde

            // Vérifier la distance (5 blocs du démon)
            if (j.getPlayer().getLocation().distance(joueur.getPlayer().getLocation()) <= 5) {
               j.getPlayer().setHealth(Math.max(0.5, j.getPlayer().getHealth() - 4));
               j.getPlayer().sendMessage("§cVous avez été touché par l'explosion d'Akaza et avez perdu §c2❤§e !");
            }
        }



    }
}
