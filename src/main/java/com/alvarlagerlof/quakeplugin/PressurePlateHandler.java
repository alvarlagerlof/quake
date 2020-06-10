package com.alvarlagerlof.quakeplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;

public class PressurePlateHandler {
    Main plugin;

    HashMap<Player, Vector> playerVectors = new HashMap<Player, Vector>();
    HashMap<Player, Boolean> usedPressurePlate = new HashMap<Player, Boolean>();
    HashMap<Player, HashMap<Integer, Integer>> deleteTimers = new HashMap<Player, HashMap<Integer, Integer>>();

    int particleShedulerId;


    public PressurePlateHandler(Main plugin) {
        this.plugin = plugin;
    }

    public void updateDebug() {

        if (plugin.getDebug()) {

            particleShedulerId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
    
                    FileConfiguration conf = plugin.getConfig();

                    if (conf.contains("pressureplates")) { 
                        Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
                        List<String> keys = new ArrayList<String>(set);
                            
                        for (String key : keys) {
                            plugin.getServer().getWorlds().get(0).spawnParticle(
                                Particle.DRIP_LAVA, 
                                (double)conf.getInt("pressureplates."+key+".x"),
                                (double)conf.getInt("pressureplates."+key+".y"),
                                (double)conf.getInt("pressureplates."+key+".z"),
                                1, 0, 0, 0, 0.001);

                            plugin.getServer().getWorlds().get(0).spawnParticle(
                                Particle.DRIP_LAVA, 
                                (double)conf.getInt("pressureplates."+key+".x")+1,
                                (double)conf.getInt("pressureplates."+key+".y"),
                                (double)conf.getInt("pressureplates."+key+".z"),
                                1, 0, 0, 0, 0.001);

                            plugin.getServer().getWorlds().get(0).spawnParticle(
                                Particle.DRIP_LAVA, 
                                (double)conf.getInt("pressureplates."+key+".x"),
                                (double)conf.getInt("pressureplates."+key+".y"),
                                (double)conf.getInt("pressureplates."+key+".z")+1,
                                1, 0, 0, 0, 0.001);

                            plugin.getServer().getWorlds().get(0).spawnParticle(
                                Particle.DRIP_LAVA, 
                                (double)conf.getInt("pressureplates."+key+".x")+1,
                                (double)conf.getInt("pressureplates."+key+".y"),
                                (double)conf.getInt("pressureplates."+key+".z")+1,
                                1, 0, 0, 0, 0.001);
                        }
                    }
                }
            }, 0L, 10L);
        } else {
            plugin.getServer().getScheduler().cancelTask(particleShedulerId);
        }

       
    }

    public void updatePressurePlates() {
        for (Map.Entry<Player, Boolean> entry : usedPressurePlate.entrySet()) {
            usedPressurePlate.put(entry.getKey(), false);
        }

        FileConfiguration conf = plugin.getConfig();

        for (Map.Entry<Player, HashMap<Integer, Integer>> entry : deleteTimers.entrySet()) {
            for (Map.Entry<Integer, Integer> block : deleteTimers.get(entry.getKey()).entrySet()) {
                Player player = entry.getKey();
                Integer id = block.getKey();
                Integer time = block.getValue();

                if (time > 0) {
                    if (time % 20 == 0) {
                        new Message().sendToPlayer(player, "c", "ID: " + String.valueOf(id) + " - " + String.valueOf(time / 20) + "s");
                    }

                    Location loc = new Location(
                        player.getWorld(),
                        conf.getInt("pressureplates."+id+".x"),
                        conf.getInt("pressureplates."+id+".y"),
                        conf.getInt("pressureplates."+id+".z")
                    );

                    if (new MaterialLists().getPressurePlateMaterials().contains(loc.getBlock().getType())) {
                        HashMap<Integer, Integer> times = deleteTimers.get(player);
                        times.put(id, -1);

                        deleteTimers.put(player, times);
                    } else {
                        HashMap<Integer, Integer> times = deleteTimers.get(player);
                        times.put(id, time - 1);

                        deleteTimers.put(player, times);
                    }
                      
                }

                if (time == 0) {
                    conf.set("pressureplates."+id, null);

                    HashMap<Integer, Integer> times = deleteTimers.get(player);
                    times.put(id, time - 1);
                    deleteTimers.put(player, times);

                    new Message().sendToPlayer(player, "c", "Removed boost " + id + " pad plate at x:" + String.valueOf(conf.getInt("pressureplates."+id+".x")) + " y:" + String.valueOf(conf.getInt("pressureplates."+id+".y")) + " z:" + String.valueOf(conf.getInt("pressureplates."+id+".z")));
                }
            }
        }
    }

    public void onPlayerMove(PlayerMoveEvent e) {

        Vector diff = new Vector(e.getTo().getX() - e.getFrom().getX(), e.getTo().getY() - e.getFrom().getY(), e.getTo().getZ() - e.getFrom().getZ());
        playerVectors.put(e.getPlayer(), diff);

       
        if (new MaterialLists().getPressurePlateMaterials().contains(e.getTo().getBlock().getType())) {
        
            Player player = e.getPlayer();

            FileConfiguration conf = plugin.getConfig();

            if (!conf.contains("pressureplates")) { conf.createSection("pressureplates"); }
            Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
            List<String> keys = new ArrayList<String>(set);

            String correctKey = null;
            
            for (String key : keys) {
                if (conf.getInt("pressureplates."+key+".x") == (int) Math.floor(e.getTo().getBlock().getX()) && 
                    conf.getInt("pressureplates."+key+".y") == (int) Math.floor(e.getTo().getBlock().getY()) && 
                    conf.getInt("pressureplates."+key+".z") == (int) Math.floor(e.getTo().getBlock().getZ())) {
                    correctKey = key;
                }
            }

            if (!usedPressurePlate.containsKey(player)) {
                usedPressurePlate.put(player, false);
            }

            if (correctKey != null) {
                if (usedPressurePlate.get(player) == null || usedPressurePlate.get(player) == false) {

                    ConfigurationSection preasurePlate = conf.getConfigurationSection("pressureplates."+correctKey);

                    if (preasurePlate.getString("effectType").equals("add")) {
                        usedPressurePlate.put(player, true);


                        Double total = Math.abs(playerVectors.get(player).getX()) + Math.abs(playerVectors.get(player).getZ());
                        Double factor = Double.valueOf(preasurePlate.getString("effectData.factor")) / total;
                        
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.setVelocity(
                                    new Vector(
                                        playerVectors.get(player).getX()*factor, 
                                        Double.valueOf(preasurePlate.getString("effectData.upforce")), 
                                        playerVectors.get(player).getZ()*factor
                                    )
                                );

                                if (plugin.getDebug()) {
                                    new Message().sendToPlayer(player, "f", "-----");
                                    new Message().sendToPlayer(player, "f", "factor: " + preasurePlate.getString("effectData.factor"));
                                    new Message().sendToPlayer(player, "f", "upforce: " + preasurePlate.getString("effectData.upforce"));
                                }
                            }
                        }.runTaskLater(plugin, 2L);
                    }

                    if (preasurePlate.getString("effectType").equals("replace")) {
                        usedPressurePlate.put(player, true);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.setVelocity(
                                    new Vector(
                                        Double.valueOf(preasurePlate.getString("effectData.x")), 
                                        Double.valueOf(preasurePlate.getString("effectData.y")), 
                                        Double.valueOf(preasurePlate.getString("effectData.z"))
                                    )
                                );

                                if (plugin.getDebug()) {
                                    new Message().sendToPlayer(player, "f", "-----");
                                    new Message().sendToPlayer(player, "f", "x: " + preasurePlate.getString("effectData.x"));
                                    new Message().sendToPlayer(player, "f", "y: " + preasurePlate.getString("effectData.y"));
                                    new Message().sendToPlayer(player, "f", "z: " + preasurePlate.getString("effectData.z"));
                                }
                            }
                        }.runTaskLater(plugin, 2L);
                    }
                }
            }
        }
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (new MaterialLists().getPressurePlateMaterials().contains(event.getBlock().getType())) {
            Player player = event.getPlayer();
            FileConfiguration conf = plugin.getConfig();
            Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
            List<String> keys = new ArrayList<String>(set);

            String correctKey = null;
            
            for (String key : keys) {
                if (conf.getInt("pressureplates."+key+".x") == (int) Math.floor(event.getBlock().getX()) && 
                    conf.getInt("pressureplates."+key+".y") == (int) Math.floor(event.getBlock().getY()) && 
                    conf.getInt("pressureplates."+key+".z") == (int) Math.floor(event.getBlock().getZ())) {
                    correctKey = key;
                }
            }

            if (correctKey != null) {
                new Message().sendToPlayer(player, "c", "Removing boost pad " + correctKey + " in 10 seconds!");

                if (deleteTimers.get(player) == null) {
                    deleteTimers.put(player, new HashMap<Integer, Integer>());
                }

                HashMap<Integer, Integer> times = deleteTimers.get(player);
                times.put(Integer.valueOf(correctKey), 20*10);

                deleteTimers.put(player, times);
            }
        }
    }
}