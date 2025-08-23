package fr.vutivo.roles;

public enum Role {
    Tanjiro("§aTanjiro", Camp.Slayer),

    Nakime("§cNakime", Camp.Demon);


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
