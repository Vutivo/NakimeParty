package fr.vutivo.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SendActionBar {

    /**
     * Envoie un message dans l'ActionBar du joueur (Spigot 1.8.8).
     * @param player Le joueur cible
     * @param message Le message à afficher
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || !player.isOnline()) return;

        // Crée le composant JSON pour le message
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");

        // Packet type 2 = ActionBar
        PacketPlayOutChat packet = new PacketPlayOutChat(icbc, (byte) 2);

        // Envoie le packet au joueur
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
