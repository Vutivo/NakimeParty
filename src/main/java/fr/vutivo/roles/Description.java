package fr.vutivo.roles;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import org.bukkit.entity.Player;


public enum Description {

    Tanjiro(Role.Tanjiro, "§6Effet :  §7Vous avez un bonus de §b20% de vitesse§7 permanent. \n \n" +
            "§6Flaire : §7Grâce à votre flaire hors du commun, vous disposez d'une flèche au niveau de votre hot-bar pour vous guider vers le démon le plus proche. \n \n"+
            "§6Passif : §7Vous disposez du pouvoir de la §6danse du dieu du feu qui vous donne §620%§7 de chance de §6mettre en feu§7 votre adversaire uniquement si vous êtes en dessous de 5 cœurs à l'éxeptrion de Doma. \n" +
            "§7Vous gagnez également §c10% de force§7 lorsque vous êtes en dessous de 5 coeurs. \n"
    ),

    Nezuko(Role.Nezuko,
            "§6Effet : §7Vous avez un bonus de §c20% de force§7 permanent ainsi que 2 cœurs supplémentaires.\n\n" +
                    "§7Étant un démon, vous apparaissez aux yeux des démons comme un membre de leur équipe.\n" +
                    "§7Par conséquent, c'est à vous de les trahir.\n\n" +
                    "§7Pour vous aider dans cette tâche, tous les slayers connaissent votre identité et vous connaissez l'identité de votre frère Tanjiro.\n\n" +
                    "§6Item : §cRage du Démon§7. Il s'agit d'un pouvoir utilisable 1 fois dans la partie, qui vous octroie un bonus de 10% de force et 10% de vitesse pendant 15 secondes.\n" +
                    "§7Attention : à la fin de ce pouvoir, vous perdez votre bonus ainsi que 10% de force et 10% de resistance.\n"
    ),

    Zenitsu(Role.Zenitsu,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §e20% de vitesse§7.\n\n" +
                    "§7Étant en possession du souffle de la foudre, vous avez §e15%§7 de chances d’invoquer un éclair sur votre adversaire, lui infligeant §c1❤ supplémentaire§7.\n\n" +
                    "§6Item : §eFrappe Foudroyante§7. Pouvoir utilisable une seule fois dans la partie, qui vous permet d’effectuer un dash très rapide vers l’avant sur une courte distance.\n" +
                    "§7Un éclair ainsi qu’une explosion sont générés à votre position finale, infligeant §c1❤§7 aux ennemis dans un rayon de 3 blocs.\n"
    ),
    Inosuke(Role.Inosuke,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §b20% de résistance§7.\n\n" +
                    "§6Item : §bSixième Sens§7. Pouvoir utilisable une seule fois dans la partie, qui vous permet de voir les §aPV actuels de tous les joueurs§7 au-dessus de leur tête pendant §e1 minute§7.\n" +
                    "§7Une fois le temps écoulé, la vision disparaît automatiquement.\n"
    ),

    Gyomei(Role.Gyomei,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §b20% de résistance§7 et §c+2❤§7.\n\n" +
                    "§6Item : §bOnde de Force§7. Pouvoir utilisable deux fois par partie, avec un cooldown de §e30 secondes§7.\n" +
                    "§7Lorsque vous l’activez, votre hache crée une onde de choc autour de vous, infligeant §c2❤§7 aux ennemis dans un rayon de 4 blocs et les repoussant légèrement.\n" +
                    "§7Vous est vos alliés êtes imunisé au knockback ainsi qu'aux dégats.\n"
    ),



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
        Joueur tanjiro = gameService.getRole(Role.Tanjiro);

        // Tous les Slayers voient Nezuko
        for (Joueur slayer : gameService.getSlayers()) {
            if (nezuko != null) {
                sendRoleToJoueur(nezuko.getRole(), nezuko, slayer.getPlayer());
            }
        }

        // Nezuko voit Tanjiro
        if (nezuko != null && tanjiro != null) {
            sendRoleToJoueur(tanjiro.getRole(), tanjiro, nezuko.getPlayer());
        }
    }


    private static void sendRoleToJoueur(Role rolePlayer, Joueur roleFromJoueur, Player receiver) {
        if (roleFromJoueur == null || receiver == null || rolePlayer == null) return;

        // Affiche le rôle et le pseudo du joueur
        String message = "§6" + rolePlayer.getName() + " §7: §c" + roleFromJoueur.getPlayer().getName();
        receiver.sendMessage(message);
    }


}