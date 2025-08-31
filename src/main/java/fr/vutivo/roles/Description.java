package fr.vutivo.roles;

import fr.vutivo.game.GameService;
import fr.vutivo.game.Joueur;
import org.bukkit.entity.Player;


public enum Description {

    Tanjiro(Role.Tanjiro, "§6Effet :  §7Vous avez un bonus de §b20% de vitesse§7 permanent. \n \n" +
            "§6Flaire : §7Grâce à votre flaire hors du commun, vous disposez d'une flèche au niveau de votre hot-bar pour vous guider vers le démon le plus proche. \n \n"+
            "§6Passif : §7Vous disposez du pouvoir de la §6danse du dieu du feu qui vous donne §620%§7 de chance de §6mettre en feu§7 votre adversaire uniquement si vous êtes en dessous de 5 cœurs à l'éxeptrion de Doma. \n" +
            "§7Vous gagnez également §c10% de force§7 lorsque vous êtes en dessous de 5 coeurs. \n\n" +
            "§7 Vous connaissez également l'identité de votre sœur §cNezuko§7.\n"
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

    Tomioka(Role.Tomioka,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §e20% de vitesse§7 et  de l'enchantement§bDepth Strider III.\n\n" +
                    "§6Item : §bSphère d’Eau§7. Pouvoir utilisable deux fois par partie, avec un cooldown de §e45 secondes§7.\n" +
                    "§7Lorsque vous l’activez, vous ciblez un joueur ennemi. Ce joueur est téléporté au centre d’une sphère d’eau avec vous.\n" +
                    "§7La sphère disparait après §e10 secondes§7.\n"
    ),

    Sanemi(Role.Sanemi,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §e20% de vitesse§7.\n\n" +
                    "§6Item : §bRafale Violente§7. Pouvoir utilisable deux fois par partie, avec un cooldown de §e30 secondes§7.\n" +
                    "§7Lorsque vous l’activez, vous effectuez un dash rapide sur quelques blocs.\n" +
                    "§7Tous les joueurs dans un rayon de 3 blocs subissent §c1❤§7 de dégâts et sont repoussés légèrement.\n" +
                    "§7Idéal pour désorienter vos adversaires ou vous repositionner stratégiquement.\n"
    ),

    Mitsuri(Role.Mitsuri,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §a+10% de force§7.\n\n" +
                    "§6Item : §bFlamme de l’Amour§7. Pouvoir utilisable deux fois par partie, avec un cooldown de §e45 secondes§7.\n" +
                    "§7Lorsque vous l’activez, vous pouvez charmer un joueur ennemi. Un joueur charmé ne peut pas vous attaquer.\n" +
                    "§7Vous pouvez charmer jusqu’à deux joueurs par partie.\n"
    ),
    Shinobu(Role.Shinobu,
            "§6Effet : §7Vous possédez deux passifs uniques.\n\n" +
                    "§7Passif 1 : §eLorsque vous attaquez un joueur, vous avez §e15%§7 de chances de l’empoisonner pendant §e5 secondes§7.\n" +
                    "§7Passif 2 : §eÀ votre mort, tous les démons autour de vous reçoivent un effet de poison pendant §e7 secondes§7.\n"
    ),


    Nakime(Role.Nakime,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §c+20% de force§7 et §e+20% de vitesse§7.\n\n" +
                    "§6Item : §bBiwa§7. Pouvoir utilisable §e1 fois par partie§7.\n" +
                    "§7Lorsque vous l’activez, tous les slayers sont téléportés aléatoirement dans la map, les désorientant et leur compliquant la vie.\n\n" +
                    "§6Passif : §7Si vous mourrez une première fois, tous les démons perdent §c1❤§7.\n" +
                    "§7Si vous mourrez une seconde fois, vous êtes définitivement éliminée.\n\n"+
                    "§7Tous les démons connaissent votre identité.\n"
    ),

    Muzan(Role.Muzan,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §b20% de résistance§7.\n\n" +
                    "§6Passif : §7Chaque fois que vous tuez un slayer, vous gagnez §c+3% de force§7 et §c+0.5❤§7 permanent.\n" +
                    "§7De plus, vous avez 20% de chance de donner un effet de saignement aux joueurs attaqués pendant §e3 secondes§7, leur faisant perdre des PV progressivement.\n"
    ),

    Kokushibo(Role.Kokushibo,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §e+20% de vitesse§7.\n\n" +
                    "§6Passif : §7Chaque fois que vous tuez un slayer, vous regagnez toute votre vie et gagnez §c+0.5❤§7 permanent.\n"
    ),

    Doma(Role.Doma,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §b20% de résistance§7 et vous êtes §eimmunisé à tous les effets passifs négatifs§7.\n\n" +
                    "§6Item : §bSérénité Mortelle§7. Pouvoir utilisable §e1 fois par partie§7.\n" +
                    "§7Lorsque vous l’activez, tous les joueurs ennemis dans un rayon de §e20 blocs§7 voient leur vitesse réduite de §e20%§7 tant qu'ils seront autour de vous pendant 30 secondes.\n"
    ),

    Akaza(Role.Akaza,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §c20% de force§7.\n\n" +
                    "§6Passif : §7À votre mort, vous créez une explosion qui inflige §c2❤§7 de dégâts à tous les joueurs autour de vous dans un rayon de quelques blocs.\n"
    ),

    Gyokko(Role.Gyokko,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §b20% de résistance§7.\n\n" +
                    "§6Item : §bTransposition§7. Pouvoir utilisable trois fois par partie, avec un cooldown de §e30 secondes§7 entre chaque utilisation.\n" +
                    "§7Lorsque vous l’activez, vous vous téléportez aléatoirement à un endroit de la map.\n" +
                    "§7Après chaque téléportation, vous gagnez un bonus de §c10% de force§7 pendant §e10 secondes§7.\n"
    ),

    Gyutaro(Role.Gyutaro,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §c20% de force§7.\n\n" +
                    "§6Item : §Houe de Souffrance§7. Pouvoir utilisable une seule fois dans la partie.\n" +
                    "§7Lorsque vous cliquez droit sur un joueur, vous lui retirez §c1❤§7 pendant §e2 minutes§7.\n" +
                    "§7Pendant ce temps, l’adversaire subit un effet §cWither§7 pendant §e10 secondes§7.\n" +
                    "§7À la fin des 2 minutes, vous perdez le cœur retiré et l’adversaire le récupère.\n"
    ),
    Kaigaku(Role.Kaigaku,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §e20% de vitesse§7.\n\n" +
                    "§6Item : §bFoudre Déchaînée§7. Pouvoir utilisable §e2 fois par partie§7, avec un cooldown de §e1 minute 30§7.\n" +
                    "§7Lorsque vous l’activez, vous envoyez un éclair qui touche tous les joueurs autour de vous.\n" +
                    "§7Les joueurs touchés subissent §c2❤§7 de dégâts.\n"
    ),

    Susamaru(Role.Susamaru,
            "§6Effet : §7Aucun bonus permanent.\n\n" +
                    "§6Item : §bArc Explosif§7. Pouvoir utilisable avec un cooldown de §e15 secondes§7 entre chaque tir.\n" +
                    "§7Lorsque vous tirez une flèche, elle crée une explosion à l’impact.\n" +
                    "§7Tous les joueurs touchés par l’explosion perdent §c2❤§7.\n"
    ),




    Yoriichi(Role.Yoriichi,
            "§6Effet : §7Vous bénéficiez d’un bonus permanent de §c+20% de force§7, §e+10% de vitesse§7 et §c+2❤§7.\n" +
                    "§7De plus, vous commencez la partie avec §e24 pommes dorées§7 au lieu de 16.\n\n" +
                    "§6Passif : §7À chaque attaque, vous avez §e15% de chances§7 de désactiver temporairement tous les effets actifs du joueur touché pendant §e10 secondes§7.\n\n" +
                    "§6Item : §bFlamme Éternelle§7. Pouvoir utilisable §e1 fois par partie§7 et durant §e1 minute§7.\n" +
                    "§7Pendant ce temps, chaque fois qu'un joueur autour de vous mange une pomme dorée, §cil ne gagnera aucun cœur d’absorption.\n"
    );



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
        Joueur nakime = gameService.getRole(Role.Nakime);

        // Tous les Slayers voient Nezuko
        for (Joueur slayer : gameService.getSlayers()) {
            if (nezuko != null) {
                sendRoleToJoueur(nezuko.getRole(), nezuko, slayer.getPlayer());
            }
        }
        // Tous les Démons voient Nakime
        for (Joueur demon : gameService.getDemons()) {
            if (nakime != null) {
                sendRoleToJoueur(nakime.getRole(), nakime, demon.getPlayer());
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