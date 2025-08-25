package fr.vutivo.game;

import fr.vutivo.roles.Camp;
import fr.vutivo.roles.Role;
import fr.vutivo.roles.RoleItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Joueur {
    private Player player;
    private Role role;
    private Camp camp;
    private int kills;
    private int strength;
    private int resistance;
    private int speed;
    private int cooldown;
    private boolean isRageMode = false;
    private boolean isAlive = true;
    private boolean isInDash = false;
    private boolean ignoreExplosion = false;


    public Joueur(Player player) {
        this.player = player;
    }

    public RoleItem getRoleItem() {
        return RoleItem.getByRole(this.role);
    }

    public boolean isAlive() {
        return isAlive;
    }
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

public boolean getRageMode() {
        return isRageMode;
    }

    public void setRageMode(boolean rageMode) {
        isRageMode = rageMode;
    }
    public Player getPlayer() {
        return player;
    }

    public boolean isInDash() {
        return isInDash;
    }

    public void setInDash(boolean inDash) {
        isInDash = inDash;
    }
    public boolean isIgnoreExplosion() {
        return ignoreExplosion;
    }
    public void setIgnoreExplosion(boolean ignoreExplosion) {
        this.ignoreExplosion = ignoreExplosion;
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

    public int getCooldown() {
        return cooldown;
    }
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setSpeed(int speed) {
        getPlayer().setWalkSpeed(0.20f + (speed * 0.002f));
        this.speed = speed;
    }
    private void addMaxHealth() {
        switch (this.getRole()){
            case Nezuko:
            case Gyomei:
            case Yoriichi:
                this.player.setMaxHealth(24.0);
                this.player.setHealth(24.0);
                break;


        }
    }

    public void aplyItems(Role role) {
        RoleItem roleItem = RoleItem.getByRole(role);
        if (roleItem != null) {
            ItemStack item = roleItem.getItem();
            if (item != null && item.getType() != Material.AIR) {
                this.player.getInventory().addItem(item);
            }

            this.strength = roleItem.getStrength();
            this.speed = roleItem.getSpeed();
            this.resistance = roleItem.getResistance();

            setSpeed(this.speed);
            addMaxHealth();
        }
    }


}
