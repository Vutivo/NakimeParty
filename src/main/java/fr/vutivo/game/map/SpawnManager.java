package fr.vutivo.game.map;

import fr.vutivo.NakimeParty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnManager {

    private final NakimeParty plugin;
    private final Random random = new Random();
    private final Set<Location> occupiedSpawns = new HashSet<>();

    public SpawnManager(NakimeParty plugin) {
        this.plugin = plugin;
    }

    /**
     * Récupère la liste de spawns pour une map depuis le config.yml
     */
    public List<Location> getSpawns(String mapName) {
        FileConfiguration config = plugin.getConfig();

        // Récupération du monde
        String worldName = config.getString("Map." + mapName + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return Collections.emptyList();

        // Lecture des positions
        List<String> coordsList = config.getStringList("Map." + mapName + ".Spawn");
        List<Location> locations = new ArrayList<>();

        for (String coords : coordsList) {
            String[] parts = coords.split(",\\s*"); // sépare par ", " ou ","
            if (parts.length >= 3) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                float yaw = 0f;
                float pitch = 0f;

                if (parts.length >= 5) {
                    yaw = Float.parseFloat(parts[3]);
                    pitch = Float.parseFloat(parts[4]);
                }

                locations.add(new Location(world, x, y, z, yaw, pitch));
            }
        }

        return locations;
    }

    /**
     * Téléporte un joueur sur un spawn aléatoire de la map
     */
    public void teleportRandomSpawn(Player player, String mapName) {
        List<Location> spawns = getSpawns(mapName);
        if (spawns.isEmpty()) {
            player.sendMessage("§cAucun spawn trouvé pour la map " + mapName);
            return;
        }

        // Filtrer pour enlever les spawns déjà occupés
        List<Location> availableSpawns = spawns.stream()
                .filter(spawn -> !occupiedSpawns.contains(spawn))
                .collect(Collectors.toList());

        if (availableSpawns.isEmpty()) {
            player.sendMessage("§cTous les spawns de la map " + mapName + " sont déjà occupés !");
            return;
        }

        // Choisir un spawn dispo
        Location randomSpawn = availableSpawns.get(random.nextInt(availableSpawns.size()));
        player.teleport(randomSpawn);

        // Marquer comme occupé
        occupiedSpawns.add(randomSpawn);
    }

    /**
     * Récupère le WorldSpawn de la map
     */
    public Location getWorldSpawn(String mapName) {
        FileConfiguration config = plugin.getConfig();

        String worldName = config.getString("Map." + mapName + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        String spawnStr = config.getString("Map." + mapName + ".WorldSpawn");
        if (spawnStr == null || spawnStr.isEmpty()) return null;

        String[] parts = spawnStr.split(",\\s*");
        if (parts.length < 3) return null;

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = parts.length >= 5 ? Float.parseFloat(parts[3]) : 0f;
        float pitch = parts.length >= 5 ? Float.parseFloat(parts[4]) : 0f;

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Téléporte un joueur sur le WorldSpawn
     */
    public void teleportToWorldSpawn(Player player, String mapName) {
        Location spawn = getWorldSpawn(mapName);
        if (spawn != null) {
            player.teleport(spawn);
        } else {
            player.sendMessage("§cLe WorldSpawn de la map " + mapName + " n'a pas été défini !");
        }
    }
}
