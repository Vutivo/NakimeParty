package fr.vutivo.roles;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum RoleItem {
    Tanjiro(Role.Tanjiro, Material.DIAMOND_SWORD, "Épée de Tanjiro"),
    Nakime(Role.Nakime, Material.IRON_AXE, "Épée de Nakime");

    private final Role role;
    private final Material material;
    private final String itemName;


    RoleItem(Role role , Material material, String itemName) {
        this.role = role;
        this.material = material;
        this.itemName = itemName;
    }
    public Role getRole() {
        return role;
    }
    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }

        return item;

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
