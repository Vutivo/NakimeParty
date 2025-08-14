package fr.vutivo.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import fr.vutivo.NakimeParty;
import fr.vutivo.NakimeService;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gestionnaire de scoreboards avec FastBoard
 */
public class ScoreBoardManager implements NakimeService {

    private final Map<UUID, FastBoard> scoreboards;

    public ScoreBoardManager() {
        this.scoreboards = new HashMap<>();
    }

    @Override
    public void register() {



    }

    @Override
    public void unregister() {
        // Supprime tous les scoreboards avant l'arrêt du plugin
        for (FastBoard board : scoreboards.values()) {
            board.delete();
        }
        scoreboards.clear();
    }

    /**
     * Cree un scoreboard pour un joueur.
     * Supprime l'ancien s'il existe deja.
     */
    public void create(Player player, String title, String... lines) {
        remove(player);
        FastBoard board = new FastBoard(player);
        board.updateTitle(title);
        board.updateLines(Arrays.asList(lines));
        scoreboards.put(player.getUniqueId(), board);
    }

    /**
     * Supprime le scoreboard d'un joueur.
     */
    public void remove(Player player) {
        FastBoard board = scoreboards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    /**
     * Met a jour le titre et les lignes du scoreboard d'un joueur.
     */
    public void update(Player player, String title, List<String> lines) {
        FastBoard board = getScoreboard(player);
        if (board != null) {
            board.updateTitle(title);
            board.updateLines(lines);
        }
    }

    /**
     * Met a jour une seule ligne du scoreboard d'un joueur.
     */
    public void updateLine(Player player, int line, String content) {
        FastBoard board = getScoreboard(player);
        if (board != null) {
            board.updateLine(line, content);
        }
    }

    /**
     * Met a jour une seule ligne pour tous les joueurs.
     */
    public void updateLineAll(int line, String content) {
        for (FastBoard board : scoreboards.values()) {
            board.updateLine(line, content);
        }
    }

    /**
     * Met a jour toutes les lignes d'un joueur.
     */
    public void updateLines(Player player, String... lines) {
        FastBoard board = getScoreboard(player);
        if (board != null) {
            board.updateLines(Arrays.asList(lines));
        }
    }

    /**
     * Met a jour toutes les lignes pour tous les joueurs.
     */
    public void updateLinesAll(String... lines) {
        List<String> list = Arrays.asList(lines);
        for (FastBoard board : scoreboards.values()) {
            board.updateLines(list);
        }
    }

    /**
     * Met a jour toutes les lignes pour tous les joueurs avec une liste.
     */
    public void updateLinesAll(List<String> lines) {
        for (FastBoard board : scoreboards.values()) {
            board.updateLines(lines);
        }
    }

    /**
     * Recupere le scoreboard d'un joueur.
     */
    public FastBoard getScoreboard(Player player) {
        return scoreboards.get(player.getUniqueId());
    }

    /**
     * Verifie si un joueur a un scoreboard.
     */
    public boolean hasScoreboard(Player player) {
        return scoreboards.containsKey(player.getUniqueId());
    }

    /**
     * Met a jour le titre pour tous les joueurs.
     */
    public void updateTitleAll(String title) {
        for (FastBoard board : scoreboards.values()) {
            board.updateTitle(title);
        }
    }
}