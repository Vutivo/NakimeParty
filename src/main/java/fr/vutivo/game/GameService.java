package fr.vutivo.game;

import fr.vutivo.NakimeParty;
import fr.vutivo.utils.NakimeService;
import fr.vutivo.commands.StartCommand;
import fr.vutivo.game.map.SpawnManager;
import fr.vutivo.listeners.FoodChangeListener;
import fr.vutivo.listeners.PlayerJoin;
import fr.vutivo.listeners.PlayerQuitListener;
import fr.vutivo.scoreboard.ScoreBoardManager;
import fr.vutivo.task.GAutoStart;
import fr.vutivo.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
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
    private final   GAutoStart gAutoStart = new GAutoStart(this);
    public boolean autoStarting = false;
    public List<Joueur> joueurs;

    public GameService() {
        this.instance = NakimeParty.getInstance();
        this.scoreBoardManager = new ScoreBoardManager();
        this.joueurs = new ArrayList<>();
        this.spawnManager = new SpawnManager(instance);
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

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
    public void addJoueur(Joueur joueur) {
        joueurs.add(joueur);
    }
    public void removeJoueur(Joueur joueur) {
        joueurs.remove(joueur);
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
    public void initScoreboardGame(){

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
        world.setGameRuleValue("keepInventory", "false");

        Bukkit.getLogger().info("Gamerules appliquées sur le monde " + world.getName());
    }


    public void clearPlayerInventory(Joueur joueur) {
        joueur.getPlayer().getInventory().clear();
        joueur.getPlayer().setHealth(20);
        joueur.getPlayer().setFoodLevel(20);
        joueur.getPlayer().setLevel(0);
        joueur.getPlayer().setExp(0);
        joueur.getPlayer().getInventory().setArmorContents(null);
        joueur.getPlayer().updateInventory();

    }

    private void listenersManager() {
        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(this), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), instance);
        instance.getServer().getPluginManager().registerEvents(new FoodChangeListener(), instance);

    }
    private void commandManager() {
        instance.getCommand("start").setExecutor(new StartCommand(this, instance));
    }


}