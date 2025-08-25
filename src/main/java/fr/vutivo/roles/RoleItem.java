package fr.vutivo.roles;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum RoleItem {
    Tanjiro(Role.Tanjiro, null, null, 0, 20, 0,0,0),
    Nezuko(Role.Nezuko, Material.NETHER_STAR, "§cRage du Démon", 20, 0, 0,1,0),
    Zenitsu(Role.Zenitsu, Material.NETHER_STAR, "§eFrappe Foudroyante", 0, 20, 0,1,0),
    Inosuke(Role.Inosuke, Material.NETHER_STAR, "§bSixième Sens", 0, 0, 20,1,0),
    Gyomei(Role.Gyomei, Material.STONE_AXE, "§bOnde de choc", 0, 0, 20,2,30),
    Tomioka(Role.Tomioka, null, null, 0, 0, 0,0,0),
    Sanemi(Role.Sanemi, null, null, 0, 0, 0,0,0),
    Mitsuri(Role.Mitsuri, null, null, 0, 0, 0,0,0),
    Shinobu(Role.Shinobu, null, null, 0, 0, 0,0,0),

    Nakime(Role.Nakime, null, null, 0, 0, 0,0,0),
    Muzan(Role.Muzan, null, null, 0, 0, 0,0,0),
    Kokushibo(Role.Kokushibo, null, null, 0, 0, 0,0,0),
    Doma(Role.Doma, null, null, 0, 0, 0,0,0),
    Akaza(Role.Akaza, null, null, 0, 0, 0,0,0),
    Gyokko(Role.Gyokko, null, null, 0, 0, 0,0,0),
    Gyutaro(Role.Gyutaro, null, null, 0, 0, 0,0,0),
    Kaigaku(Role.Kaigaku, null, null, 0, 0, 0,0,0),
    Susamaru(Role.Susamaru, null, null, 0, 0, 0,0,0),

    Yoriichi(Role.Yoriichi, null, null, 0, 0, 0,0,0);

    private final Role role;
    private final Material material;
    private final String displayName;
    private final int strength;
    private final int speed;
    private final int resistance;
    private int power;
    private int Cooldown;

    RoleItem(Role role, Material material, String displayName, int strength, int speed, int resistance, int power, int Cooldown) {
        this.role = role;
        this.material = material;
        this.displayName = displayName;
        this.strength = strength;
        this.speed = speed;
        this.resistance = resistance;
        this.power = power;
        this.Cooldown = Cooldown;
    }

    public Role getRole() {
        return role;
    }
    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public ItemStack getItem() {
        if (material == null) {
            return null;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        return item;
    }

    public int getStrength() {
        return strength;
    }

    public int getSpeed() {
        return speed;
    }

    public int getResistance() {
        return resistance;
    }
    public int getCooldown() {
        return Cooldown;
    }

    public static RoleItem getByRole(Role role) {
        for (RoleItem ri : values()) {
            if (ri.getRole() == role) {
                return ri;
            }
        }
        return null;
    }
}
