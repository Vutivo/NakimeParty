package fr.vutivo.task;

import fr.vutivo.game.GameService;
import fr.vutivo.game.State;
import org.bukkit.scheduler.BukkitRunnable;



public class GameTask extends BukkitRunnable {
    private final GameService gameService;
    public int timer = 0;

    public GameTask(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void run() {
        if(gameService.getState() == State.PLAYING) {

            timer ++;
            gameService.getScoreBoardManager().updateLineAll(8, "§6Temps: §e" + gameService.Chrono(timer));
            // on Check si Tanjiro est en mode rage
            gameService.tanjiroRage();


        }


    }
}
