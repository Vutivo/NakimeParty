package fr.vutivo.game;

import fr.vutivo.NakimeParty;
import fr.vutivo.listeners.*;
import fr.vutivo.roles.Role;
import fr.vutivo.task.GameTask;
import fr.vutivo.utils.NakimeService;
import fr.vutivo.commands.StartCommand;
import fr.vutivo.game.map.SpawnManager;
import fr.vutivo.scoreboard.ScoreBoardManager;
import fr.vutivo.task.GAutoStart;
import fr.vutivo.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;


import java.util.ArrayList;
import java.util.List;

public class GameService implements NakimeService {

    private final NakimeParty instance;
    private final ScoreBoardManager scoreBoardManager;
    private final SpawnManager spawnManager;
    private ScoreboardManager scoreboardManager ;
    private State state;
    public int minPlayers = 10;
    public int maxPlayers = 17;
    private final  GAutoStart gAutoStart = new GAutoStart(this);
    private  GameTask gameTask ;
    public boolean autoStarting = false;
    public List <Role> compo = new ArrayList<>();
    public List<Joueur> joueurs;
    public List <Joueur> Slayers ;
    public List <Joueur> Demons ;
    public int maxSlayers ;
    public int maxDemons ;
    public int maxJoueurs;
    public String MAP_NAME = "world";


    public GameService() {
        this.instance = NakimeParty.getInstance();
        this.scoreBoardManager = new ScoreBoardManager();
        this.joueurs = new ArrayList<>();
        this.Slayers = new ArrayList<>();
        this.Demons = new ArrayList<>();
        this.spawnManager = new SpawnManager(instance);
        this.scoreboardManager = Bukkit.getScoreboardManager();


    }

    @Override
    public void register() {
        listenersManager();
        commandManager();
        setGamerules();
        scoreBoardManager.register();
        setState(State.WAITING);

    }

    @Override
    public void unregister() {
        scoreBoardManager.unregister();

        // Nettoyage des ressources si nécessaire
    }

    /**
     * Définit l'état du jeu
     * @param state L'état à définir
     */
    public void setState(State state) {
        this.state = state;
    }
    public State getState() {
        return state;
    }

    /**
     * gère la liste des joueurs
     */

    public List <Role> getCompo() {
        return compo;
    }
    public List<Joueur> getJoueurs() {
        return joueurs;
    }
    public void addJoueur(Joueur joueur) {
        joueurs.add(joueur);
    }
    public void removeJoueur(Joueur joueur) {
        joueurs.remove(joueur);
    }
    public List<Joueur> getSlayers() {
        return Slayers;
    }
    public void addSlayer(Joueur joueur) {
        Slayers.add(joueur);
    }
    public void removeSlayer(Joueur joueur) {
        Slayers.remove(joueur);
    }
    public List<Joueur> getDemons() {
        return Demons;
    }
    public void addDemon(Joueur joueur) {
        Demons.add(joueur);
    }
    public void removeDemon(Joueur joueur) {
        Demons.remove(joueur);
    }

    public Joueur getRole(Role role) {
        for (Joueur joueur : joueurs) {
            if (joueur.getRole() == role) {
                return joueur;
            }
        }
        return null;
    }


    /**
     * Récupère le gestionnaire de scoreboards
     */
    public ScoreBoardManager getScoreBoardManager() {
        return scoreBoardManager;
    }

    public GAutoStart getgAutoStart() {
        return gAutoStart;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
    public NakimeParty getInstance() {
        return instance;
    }

    public void setGameTask(GameTask gameTask) {
        this.gameTask = gameTask;
    }



    /**
     * On verifie si le jeu est sur WAITING
     * et si oui, on lance la tâche de démarrage automatique
     * Si le jeu est déjà en cours, on ne fait rien
     * @see GAutoStart
     */
    public void checkAutoStart(){

        if(getState() != State.WAITING) return;
        setState(State.STARTING);
        if (!autoStarting){
            autoStarting = true;
            gAutoStart.runTaskTimer(instance, 0, 20);
        }


    }
    public void initScoreboardGame(Joueur joueur) {
        Player player = joueur.getPlayer();
        getScoreBoardManager().create(player, ChatColor.RED + "Nakime" + ChatColor.DARK_RED + ChatColor.BOLD + "Party",
                "",
                ChatColor.RED +"   >> Informations",
                ChatColor.WHITE+"Joueurs : " + ChatColor.AQUA + getJoueurs().size(),
                ChatColor.WHITE+"Slayers :" + ChatColor.AQUA + getSlayers().size(),
                ChatColor.WHITE+"Démons :" + ChatColor.AQUA + getDemons().size(),
                "",
                ChatColor.RED + "   >>Role",
                ChatColor.WHITE +"Role : "+ joueur.getRole().getName(),
                ChatColor.WHITE +"Timer : " + Chrono(gameTask.timer),
                "",
                "⚔"+ joueur.getStrength() + " ✦"+ joueur.getResistance() + " ⚡"+ joueur.getSpeed()
        );
    }

    public void givePlayerArmor(Joueur joueur) {
        clearPlayerInventory(joueur);

        // Armure
        ItemStack helmet = new ItemBuilder(Material.DIAMOND_HELMET, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build();

        ItemStack chestplate = new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .build();

        ItemStack leggings = new ItemBuilder(Material.IRON_LEGGINGS, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .build();

        ItemBuilder bootsBuilder = new ItemBuilder(Material.IRON_BOOTS, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

        if (joueur.getRole() == Role.Tomioka) {
            bootsBuilder.addEnchant(Enchantment.DEPTH_STRIDER, 3);
        }
        ItemStack boots = bootsBuilder.build();

        // Armes
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.DAMAGE_ALL, 3)
                .build();

        ItemStack bow = new ItemBuilder(Material.BOW, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.ARROW_DAMAGE, 3)
                .build();

        // Consommables
        int arrowCount = (joueur.getRole() == Role.Susamaru) ? 64 : 32;
        ItemStack arrows = new ItemBuilder(Material.ARROW, arrowCount).build();

        int gapCount = (joueur.getRole() == Role.Yoriichi) ? 24 : 16;
        ItemStack gapple = new ItemBuilder(Material.GOLDEN_APPLE, gapCount).build();

        // Attribution
        Player player = joueur.getPlayer();
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.getInventory().addItem(sword, bow, arrows, gapple);
        player.updateInventory();
    }


    public String Chrono(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public Joueur getJoueur(Player player) {
        return joueurs.stream()
                .filter(joueur -> joueur.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    private void setGamerules() {
        World world = Bukkit.getWorld(MAP_NAME);
        if (world == null) {
            Bukkit.getLogger().info("Le monde " + MAP_NAME + " n'est pas chargé !");
            return;
        }

        // Jour éternel
        world.setTime(1000);
        world.setGameRuleValue("doDaylightCycle", "false");

        // Pas de régénération
        world.setGameRuleValue("naturalRegeneration", "false");

        // Aucun mob
        world.setGameRuleValue("doMobSpawning", "false");

        // Inventaire normal
        world.setGameRuleValue("keepInventory", "true");

        Bukkit.getLogger().info("Gamerules appliquées sur le monde " + world.getName());
    }

    public void tanjiroRage() {
        for (Joueur joueur : getJoueurs()) {
            if (joueur.getRole().equals(Role.Tanjiro)){
                if(joueur.getPlayer().getHealth() < 10 && !joueur.getRageMode()){
                    joueur.setRageMode(true);
                    joueur.setStrength(joueur.getStrength() + 10);
                    reloadEffectScoreboard(joueur);


                } else if (joueur.getPlayer().getHealth() >= 10 && joueur.getRageMode()) {
                    joueur.setRageMode(false);
                    joueur.setStrength(joueur.getStrength() - 10);
                    reloadEffectScoreboard(joueur);

                }
            }
        }
    }
    public void viewHpPlayer(Player player) {
        // Créer un nouveau scoreboard pour ce joueur
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        // Créer l'objectif pour afficher la vie au-dessus de la tête
        Objective healthObjective = scoreboard.registerNewObjective("health", "health");
        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        healthObjective.setDisplayName("❤"); // Symbole cœur

        // Appliquer le scoreboard au joueur
        player.setScoreboard(scoreboard);
        // Mettre à jour la vie de tous les joueurs pour ce joueur
        updateAllPlayersHealthFor(player);
    }

    public void disableHealthDisplay(Player player) {
        player.setScoreboard(scoreboardManager.getMainScoreboard());


        }
    private void updateAllPlayersHealthFor(Player viewer) {
        Scoreboard scoreboard = viewer.getScoreboard();
        Objective healthObjective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

        if (healthObjective != null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // Calculer la santé actuelle (arrondie au supérieur)
                int health = (int) Math.ceil(onlinePlayer.getHealth());
                // Appliquer immédiatement le score
                healthObjective.getScore(onlinePlayer.getName()).setScore(health);
            }
        }
    }





    public double getRelativeAngle(Player from, Player to) {
        // Calcul de l'angle entre les deux joueurs sur l'axe horizontal (X, Z)
        double angle = Math.toDegrees(Math.atan2(to.getLocation().getZ() - from.getLocation().getZ(), to.getLocation().getX() - from.getLocation().getX()));

        // Normalisation de l'angle pour qu'il soit entre 0 et 360 degrés
        angle = (angle + 360) % 360;

        // Angle de la vue du joueur (yaw), normalisé pour être entre 0 et 360 degrés
        // Correction du yaw de Minecraft
        double playerYaw = (from.getLocation().getYaw() + 360) % 360;
        playerYaw = (playerYaw + 90) % 360; // Ajustement de la boussole de Minecraft

        // Calcul de l'angle relatif du démon par rapport à la vue du joueur
        return (angle - playerYaw + 360) % 360;
    }

    public Joueur getTargetJoueur(Joueur joueur, int maxDistance) {
        Player player = joueur.getPlayer();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        Joueur closest = null;
        double closestDistance = maxDistance;

        // Constante pour ajuster la tolérance de la détection
        final double hitboxTolerance = 0.5;

        // Avance petit à petit dans la direction du regard
        for (double i = 0; i <= maxDistance; i += 0.5) {
            Location point = eyeLocation.clone().add(direction.clone().multiply(i));

            // On parcourt la liste de tous les joueurs pour les cibler
            for (Joueur otherJoueur : getJoueurs()) {
                Player otherPlayer = otherJoueur.getPlayer();

                if (otherPlayer.equals(player) || !otherPlayer.isOnline() || !otherJoueur.isAlive()) {
                    continue; // Ignorer soi-même et les joueurs inactifs
                }

                Location playerLocation = otherPlayer.getLocation();

                // Vérifie si le point est dans la zone du joueur (hitbox simplifiée)
                if (Math.abs(point.getX() - playerLocation.getX()) < hitboxTolerance &&
                        Math.abs(point.getZ() - playerLocation.getZ()) < hitboxTolerance &&
                        point.getY() > playerLocation.getY() && point.getY() < playerLocation.getY() + 1.9) {

                    double dist = player.getLocation().distance(otherPlayer.getLocation());
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        closest = otherJoueur;
                    }
                }
            }
        }
        return closest;
    }
    public String getTanjiroArrow(Player from, Player to) {
        // Appel de la méthode extraite pour obtenir l'angle relatif
        double relativeAngle = getRelativeAngle(from, to);

        // Détermination de la flèche en fonction de l'angle relatif
        if (relativeAngle >= 337.5 || relativeAngle < 22.5) {
            return "↑";   // Devant
        } else if (relativeAngle >= 22.5 && relativeAngle < 67.5) {
            return "↗";  // Devant à droite
        } else if (relativeAngle >= 67.5 && relativeAngle < 112.5) {
            return "→";   // À droite
        } else if (relativeAngle >= 112.5 && relativeAngle < 157.5) {
            return "↘";  // Derrière à droite
        } else if (relativeAngle >= 157.5 && relativeAngle < 202.5) {
            return "↓";   // Derrière
        } else if (relativeAngle >= 202.5 && relativeAngle < 247.5) {
            return "↙";  // Derrière à gauche
        } else if (relativeAngle >= 247.5 && relativeAngle < 292.5) {
            return "←";   // À gauche
        } else if (relativeAngle >= 292.5 && relativeAngle < 337.5) {
            return "↖";  // Devant à gauche
        } else {
            return "•";   // Inconnu
        }
    }


    public void reloadEffectScoreboard(Joueur joueur) {
        getScoreBoardManager().updateLine(joueur.getPlayer(), 10,"⚔"+ joueur.getStrength() + " ✦"+ joueur.getResistance() + " ⚡"+ joueur.getSpeed());
    }


    public void clearPlayerInventory(Joueur joueur) {
        joueur.getPlayer().getInventory().clear();
        joueur.getPlayer().setMaxHealth(20);
        joueur.getPlayer().setHealth(20);
        joueur.getPlayer().setFoodLevel(20);
        joueur.getPlayer().setLevel(0);
        joueur.getPlayer().setExp(0);
        joueur.getPlayer().getInventory().setArmorContents(null);
        joueur.getPlayer().setWalkSpeed(0.2f);
        joueur.getPlayer().updateInventory();
        joueur.getPlayer().getActivePotionEffects().forEach(effect -> joueur.getPlayer().removePotionEffect(effect.getType()));

    }

    private void listenersManager() {
        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), instance);
        instance.getServer().getPluginManager().registerEvents(new FoodChangeListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerDamage(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerDeath(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerInteract(this), instance);
        instance.getServer().getPluginManager().registerEvents(new WaterFlow(), instance);



    }
    private void commandManager() {
        instance.getCommand("start").setExecutor(new StartCommand(this, instance));
    }


}