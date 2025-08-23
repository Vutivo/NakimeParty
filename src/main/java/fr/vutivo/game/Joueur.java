package fr.vutivo.game;

import fr.vutivo.roles.Camp;
import fr.vutivo.roles.Role;
import fr.vutivo.roles.RoleItem;
import org.bukkit.entity.Player;


public class Joueur {
    private Player player;
    private Role role;
    private Camp camp;
    private int kills;
    private int strength;
    private int resistance;
    private int speed;

    public Joueur(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public Camp getCamp() {
        return camp;
    }
    public void setCamp(Camp camp) {
        this.camp = camp;
    }
    public int getKills() {
        return kills;
    }
    public void addKill() {
        this.kills++;
    }
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void aplyItems(Role role) {
        RoleItem roleItem = RoleItem.getByRole(role);
        if (roleItem != null) {
            this.player.getInventory().addItem(roleItem.getItem());
        }
    }

}
