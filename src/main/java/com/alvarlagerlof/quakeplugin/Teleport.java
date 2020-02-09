package com.alvarlagerlof.quakeplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Location;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;


public class Teleport {
    JavaPlugin plugin;
    Player player;

    public Teleport(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Location getLobby() {
        FileConfiguration conf = plugin.getConfig();

        if (!conf.contains("lobby")) {
            new Message().sendToPlayer(player, "f", "Lobby is not set");
            return null;
        } else {
            Location location = new Location(
                player.getWorld(), 
                conf.getDouble("lobby.x"), 
                conf.getDouble("lobby.y"), 
                conf.getDouble("lobby.z"));
            location.setYaw((float) conf.getDouble("lobby.yaw"));
            location.setPitch((float) conf.getDouble("lobby.pitch"));

            return location;
        }
    }

    public Location getSpawnPoint(String arena, List<Player> players) {

        FileConfiguration conf = plugin.getConfig();

        if (conf.getConfigurationSection("arenas." + arena + ".spawnpoints").getKeys(false).size() > 0) {
            // Get best spawnpoint
            Set<String> set = conf.getConfigurationSection("arenas." + arena + ".spawnpoints").getKeys(false);
            List<String> keys = new ArrayList<String>(set);

            double longestDistance = 0.0;
            String longestKey = null;
            
            for (String key : keys) {
                Location location = new Location(
                    player.getWorld(), 
                    conf.getDouble("arenas."+arena+".spawnpoints."+key+".x"), 
                    conf.getDouble("arenas."+arena+".spawnpoints."+key+".y"), 
                    conf.getDouble("arenas."+arena+".spawnpoints."+key+".z"));
                location.setYaw((float) conf.getDouble("arenas."+arena+".spawnpoints."+key+".yaw"));
                location.setPitch((float) conf.getDouble("arenas."+arena+".spawnpoints."+key+".pitch"));

                double closestPlayerDistance = 1000.0;

                for (Player p : players) {
                    double d = location.distance(p.getLocation());

                    if (d < closestPlayerDistance) {
                        closestPlayerDistance = d;
                    }
                }

                if (closestPlayerDistance > longestDistance) {
                    longestDistance = closestPlayerDistance;
                  longestKey = key;
                }
            }

            Location finalLocation = new Location(
                player.getWorld(), 
                conf.getDouble("arenas."+arena+".spawnpoints."+longestKey+".x"), 
                conf.getDouble("arenas."+arena+".spawnpoints."+longestKey+".y"), 
                conf.getDouble("arenas."+arena+".spawnpoints."+longestKey+".z"));
            finalLocation.setYaw((float) conf.getDouble("arenas."+arena+".spawnpoints."+longestKey+".yaw"));
            finalLocation.setPitch((float) conf.getDouble("arenas."+arena+".spawnpoints."+longestKey+".pitch"));
           
            return finalLocation;

        } else {
            new Message().sendToPlayer(player, "f", "No spawn points for " + arena + " found");
            return null;
        }

    }


}