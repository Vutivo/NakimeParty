package fr.vutivo.roles;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public enum Description {

    Tanjiro(Role.Tanjiro, "§6Effet :  §7Vous avez un bonus de §b20% de vitesse§7 permanent. \n \n" +
            "§6Flaire : §7Grâce à votre flaire hors du commun, vous disposez d'une flèche au niveau de votre hot-bar pour vous guider vers le démon le plus proche. \n \n"+
            "§6Passif : §7Vous disposez du pouvoir de la §6danse du dieu du feu§7 qui vous donne §620%§7 de chance de §6mettre en feu§7 votre adversaire uniquement si vous êtes en dessous de 5 cœurs à l'éxeptrion de Doma. \n" +
            "Vous gagnez également §c10% de force§7 lorsque vous êtes en dessous de 5 coeurs. \n"),

    Nezuko(Role.Nezuko,
            "§6Effet : §7Vous avez un bonus de §c20% de force§7 permanent ainsi que 2 cœurs supplémentaires.\n\n" +
                    "Étant un démon, vous apparaissez aux yeux des démons comme un membre de leur équipe.\n" +
                    "Par conséquent, c'est à vous de trahir l'équipe des démons.\n\n" +
                    "Pour vous aider dans cette tâche, tous les slayers connaissent votre identité et vous connaissez l'identité de votre frère Tanjiro.\n\n" +
                    "§6Item : §cRage du Démon§7. Il s'agit d'un pouvoir utilisable 1 fois dans la partie, qui vous octroie un bonus de 10% de force et 10% de vitesse pendant 15 secondes.\n" +
                    "Attention : à la fin de ce pouvoir, vous perdez votre bonus ainsi que 10% de force et 10% de resistance.\n"),

    Nakime(Role.Nakime, "Vous êtes un Démon. Votre objectif est de tuer tous les Slayers."),

    Yoriichi(Role.Yoriichi, "");

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
                return "§aVous gagnez avec les Slayers.";
            case Demon:
                return "§cVous gagnez avec les Démons.";
            case Solo:
                return "§6Vous êtes seul, vous gagnez en étant le dernier en vie.";
            default:
                return "";
        }
    }

    public static void sendDescriptionToPlayer(Joueur joueur) {
        Description desc = getDescriptionForRole(joueur.getRole());
        if (desc == null) return;

        Player player = joueur.getPlayer();
        StringBuilder sb = new StringBuilder();
        sb.append("§8=====================================================\n");
        sb.append("§8[Privé] Vous êtes §o§9").append(joueur.getRole().getName()).append("§r\n");
        sb.append("\n");
        sb.append("§8Camp: ").append(getCampMessage(joueur.getCamp())).append("\n");
        sb.append("\n");
        sb.append("§8Description:\n");
        sb.append("\n");
        sb.append(desc.getDescription()).append("\n");
        sb.append("§8=====================================================");

        // Envoi ligne par ligne pour garder les sauts visibles
        for (String line : sb.toString().split("\n")) {
            player.sendMessage(line);
        }

    }



    public static Description getDescriptionForRole(Role role) {
        for (Description d : values()) {
            if (d.getRole() == role) return d;
        }
        return null;
    }

    public static void otherInfo(GameService gameService) {
        Joueur nezuko = gameService.getRole(Role.Nezuko);
        for (Joueur slayer : gameService.getSlayers()) {
            if (!slayer.equals(nezuko)) { // Nezuko ne doit pas s'auto-connaître
                sendRoleToJoueur(nezuko.getRole(), nezuko, slayer.getPlayer());
            }
        }
    }

    private static void sendRoleToJoueur(Role rolePlayer, Joueur roleFromJoueur, Player receiver) {
        if (roleFromJoueur == null || receiver == null || rolePlayer == null) return;

        // Affiche le rôle et le pseudo du joueur
        String message = "§6" + rolePlayer.getName() + " §7: §c" + roleFromJoueur.getPlayer().getName();
        receiver.sendMessage(message);
    }


}