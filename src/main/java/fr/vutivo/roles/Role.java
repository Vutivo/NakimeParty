package fr.vutivo.roles;

public enum Role {
    Tanjiro("§aTanjiro", Camp.Slayer),
    Nezuko("§aNezuko", Camp.Slayer),
    Zenitsu("§aZenitsu", Camp.Slayer),
    Inosuke("§aInosuke", Camp.Slayer),
    Gyomei("§aGyomei", Camp.Slayer),
    Tomioka("§aTomioka", Camp.Slayer),
    Sanemi("§aSanemi", Camp.Slayer),
    Mitsuri("§aMitsuri", Camp.Slayer),
    Shinobu("§aShinobu", Camp.Slayer),

    Nakime("§cNakime", Camp.Demon),
    Muzan("§cMuzan", Camp.Demon),
    Kokushibo("§cKokushibo", Camp.Demon),
    Doma("§cDoma", Camp.Demon),
    Akaza("§cAkaza", Camp.Demon),
    Gyokko("§cGyokko", Camp.Demon),
    Gyutaro("§cGyutaro", Camp.Demon),
    Kaigaku("§cKaigaku", Camp.Demon),
    Susamaru("§cSusamaru", Camp.Demon),

    Yoriichi("§aYoriichi", Camp.Solo);


    private final String name;
    private final Camp camp;

    Role(String displayName, Camp camp) {
        this.name = displayName;
        this.camp = camp;
    }


    public String getName() {
        return name;
    }
    public Camp getCamp() {
        return camp;
    }
}
