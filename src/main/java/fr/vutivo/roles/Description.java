package fr.vutivo.roles;

import fr.vutivo.game.Joueur;
import org.bukkit.entity.Player;


public enum Description {

    Tanjiro(Role.Tanjiro, "\n§8Effet :  §7Vouys avez un bonus de 20% de vitesse permanant. \n \n " +
            "§8Flaire : Grace à votre flaire hors du commun, vous disposé d'une flèche au niveau de votre hoy-bar pour vous guidez vers le démon le plus proche.  \n \n"+
            "§8Passif : §7Vous disposer du pouvoir de la dance du dieu du feu qui vous donnera 30% de chance de mettre en feu votre adversaire uniquement si vous êtes en dessous de 5 coeurs. " +
            "Vous gagnez également 10% de force lorsque vous êtes en dessous de 5 coeurs. \n "),


    Nakime(Role.Nakime, "Vous êtes un Démon. Votre objectif est de tuer tous les Slayers.");

    private final Role role;
    private final String description;

    Description(Role role, String description) {
        this.role = role;
        this.description = description;
    }

    public Role getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    private static String getCampMessage(Camp camp) {
         switch (camp) {
            case Slayer:
                return "Vous gagnez avec les Slayers.";
            case Demon:
                return "Vous gagnez avec les Démons.";
            case Solo:
                return "Vous êtes seul, vous gagnez en étant le dernier en vie.";
            default:
                return "";
        }
    }

    public static void sendDescriptionToPlayer(Joueur joueur) {
        Description desc = getDescriptionForRole(joueur.getRole());
        if (desc == null) return;

        Player player = joueur.getPlayer();
        StringBuilder sb = new StringBuilder();
        sb.append("§8======================================================\n");
        sb.append("§9[Privé] Vous êtes §o§9").append(joueur.getRole().getName()).append(".§r\n");
        sb.append(" ");
        sb.append("Camp: ").append(getCampMessage(joueur.getCamp())).append("\n");
        sb.append(" ");
        sb.append("Description: ").append(desc.getDescription()).append("\n");
        sb.append(" ");
        sb.append("§8======================================================\n");

        player.sendMessage(sb.toString());
    }

    public static Description getDescriptionForRole(Role role) {
        for (Description d : values()) {
            if (d.getRole() == role) return d;
        }
        return null;
    }
}
