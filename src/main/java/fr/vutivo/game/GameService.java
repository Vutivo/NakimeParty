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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.List;

public class GameService implements NakimeService {

    private final NakimeParty instance;
    private final ScoreBoardManager scoreBoardManager;
    private final SpawnManager spawnManager;
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


    public GameService() {
        this.instance = NakimeParty.getInstance();
        this.scoreBoardManager = new ScoreBoardManager();
        this.joueurs = new ArrayList<>();
        this.Slayers = new ArrayList<>();
        this.Demons = new ArrayList<>();
        this.spawnManager = new SpawnManager(instance);
        compo.add(Role.Tanjiro);
        compo.add(Role.Nezuko);
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

    public GameTask getGameTask() {
        return gameTask;
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
        // Donne une armure type UHC avec 16 gap arc et 32 flèches
        clearPlayerInventory(joueur);

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

        ItemStack boots = new ItemBuilder(Material.IRON_BOOTS, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .build();

        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.DAMAGE_ALL, 3)
                .build();

        ItemStack bow = new ItemBuilder(Material.BOW, 1)
                .addEnchant(Enchantment.DURABILITY, 2)
                .addEnchant(Enchantment.ARROW_DAMAGE, 3)
                .build();

        ItemStack arrows = new ItemBuilder(Material.ARROW, 32).build();
        ItemStack gapple = new ItemBuilder(Material.GOLDEN_APPLE, 16).build();

        joueur.getPlayer().getInventory().setHelmet(helmet);
        joueur.getPlayer().getInventory().setChestplate(chestplate);
        joueur.getPlayer().getInventory().setLeggings(leggings);
        joueur.getPlayer().getInventory().setBoots(boots);
        joueur.getPlayer().getInventory().addItem(sword);
        joueur.getPlayer().getInventory().addItem(bow);
        joueur.getPlayer().getInventory().addItem(arrows);
        joueur.getPlayer().getInventory().addItem(gapple);
        joueur.getPlayer().updateInventory();
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
        World world = Bukkit.getWorld(instance.MAP_NAME);
        if (world == null) {
            Bukkit.getLogger().info("Le monde " + instance.MAP_NAME + " n'est pas chargé !");
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

    }

    private void listenersManager() {
        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), instance);
        instance.getServer().getPluginManager().registerEvents(new FoodChangeListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerDamage(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerDeath(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerInteract(this), instance);


    }
    private void commandManager() {
        instance.getCommand("start").setExecutor(new StartCommand(this, instance));
    }


}