package fr.vutivo.commands;

import fr.vutivo.NakimeParty;
import fr.vutivo.game.GameService;
import fr.vutivo.game.State;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    private final GameService service;
    private final NakimeParty instance ;
    public StartCommand(GameService service, NakimeParty instance) {
        this.service = service;
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }
        Player player = (Player) sender;
        if(!player.isOp()){
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        if(instance.getGameService().getState() != State.WAITING) {
            player.sendMessage("§cLa partie est déjà en cours.");
            return true;
        }
        instance.getGameService().checkAutoStart();
        return false;
    }
}
