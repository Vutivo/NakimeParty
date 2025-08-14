package fr.vutivo.game;

import fr.vutivo.NakimeParty;
import fr.vutivo.NakimeService;
import fr.vutivo.listeners.PlayerJoin;
import fr.vutivo.scoreboard.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class GameService implements NakimeService {

    private final NakimeParty instance;
    private final ScoreBoardManager scoreBoardManager;
    private World world;
    public Location spawnLocation;
    private State state;
    public int minPlayers = 10;
    public int maxPlayers = 17;

    public List<Joueur> joueurs;

    public GameService() {
        this.instance = NakimeParty.getInstance();
        this.scoreBoardManager = new ScoreBoardManager();
        this.joueurs = new ArrayList<>();
    }

    @Override
    public void register() {
        listenersManager();
        loadConfig();
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
    public void isState(State state) {
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



    private void listenersManager() {
        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(this), instance);
    }
    private void loadConfig() {
        instance.saveDefaultConfig();
        world = Bukkit.getWorld(instance.getConfig().getString("Spawn.worldName"));
        spawnLocation = getLocation(instance.getConfig().getString("Spawn.location"));
    }

    private Location getLocation(String loc) {
        String[] args = loc.split(",");

        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        float yaw = Float.parseFloat(args[3]);
        float pitch = Float.parseFloat(args[4]);

        return new Location(world, x, y, z, yaw, pitch);
    }
}