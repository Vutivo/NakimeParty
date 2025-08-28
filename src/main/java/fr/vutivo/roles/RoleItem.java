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
    Tomioka(Role.Tomioka, Material.NETHER_STAR, "§bSphère d’Eau", 0, 20, 0,2,45),
    Sanemi(Role.Sanemi, Material.NETHER_STAR,"§bRafale Violente" , 0, 20, 0,2,30),
    Mitsuri(Role.Mitsuri, Material.NETHER_STAR, "§bFlamme de l’Amour", 10, 0, 0,2,45),
    Shinobu(Role.Shinobu, null, null, 0, 0, 0,0,0),

    Nakime(Role.Nakime, Material.NETHER_STAR, "§bBiwa", 20, 20, 0,1,0),
    Muzan(Role.Muzan, null, null, 0, 0, 20,0,0),
    Kokushibo(Role.Kokushibo, null, null, 0, 20, 0,0,0),
    Doma(Role.Doma, Material.NETHER_STAR, "§bSérénité Mortelle", 0, 0, 20,1,0),
    Akaza(Role.Akaza, null, null, 20, 0, 0,0,0),
    Gyokko(Role.Gyokko, Material.NETHER_STAR, "§bTransposition", 0, 0, 20,3,30),
    Gyutaro(Role.Gyutaro, Material.DIAMOND_HOE," §Houe de Souffrance", 20, 0, 0,1,0),
    Kaigaku(Role.Kaigaku, Material.NETHER_STAR, "§bFoudre Déchaînée", 0, 20, 0,2,90),
    Susamaru(Role.Susamaru, Material.BOW, "§bArc Explosif", 0, 0, 0,0,0),

    Yoriichi(Role.Yoriichi, Material.NETHER_STAR, "§bFlamme Éternelle", 20, 10, 0,1,0);

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
