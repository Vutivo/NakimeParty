package fr.vutivo;

import fr.vutivo.game.GameService;

import fr.vutivo.task.GAutoStart;
import org.bukkit.plugin.java.JavaPlugin;

public final class NakimeParty extends JavaPlugin {
    private GameService gameService;
    private static NakimeParty instance;
    public String MAP_NAME = "world";




    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Plugin startup logic
        gameService = new GameService();
        gameService.register();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    public static NakimeParty getInstance() {
        return instance;
    }
    public GameService getGameService() {
        return gameService;
    }
}
