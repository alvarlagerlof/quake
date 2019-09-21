package com.alvarlagerlof.quakeplugin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Firework;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.Particle;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.FireworkEffect;
import org.bukkit.Color;
import org.bukkit.util.BoundingBox;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.Sound;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Random;
import java.util.Map;

import com.alvarlagerlof.quakeplugin.PressurePlateMaterials;

public class PressurePlateHandler {
    JavaPlugin plugin;
    HashMap<Player, Vector> playerVectors = new HashMap<Player, Vector>();
    HashMap<Player, Boolean> usedPressurePlate = new HashMap<Player, Boolean>();
    HashMap<Player, HashMap<Integer, Integer>> deleteTimers = new HashMap<Player, HashMap<Integer, Integer>>();


    public PressurePlateHandler(JavaPlugin plugin) {
        this.plugin = plugin;
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
                    new Message().sendToPlayer(player, "f", String.valueOf(id) + " : " + String.valueOf(time));

                    Location loc = new Location(
                        player.getWorld(),
                        conf.getInt("pressureplates."+id+".x"),
                        conf.getInt("pressureplates."+id+".y"),
                        conf.getInt("pressureplates."+id+".z")
                    );

                    if (new PressurePlateMaterials().getMaterials().contains(loc.getBlock().getType())) {
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

                    new Message().sendToPlayer(player, "f", "Removed boost pad plate at x:" + String.valueOf(conf.getInt("pressureplates."+id+".x")) + " y:" + String.valueOf(conf.getInt("pressureplates."+id+".y")) + " z:" + String.valueOf(conf.getInt("pressureplates."+id+".z")));
                }
            }
        }
    }

    public void onPlayerMove(PlayerMoveEvent e) {
        Vector diff = new Vector(e.getTo().getX() - e.getFrom().getX(), e.getTo().getY() - e.getFrom().getY(), e.getTo().getZ() - e.getFrom().getZ());
        playerVectors.put(e.getPlayer(), diff);

       
        if (new PressurePlateMaterials().getMaterials().contains(e.getTo().getBlock().getType())) {
        
            Player player = e.getPlayer();
            FileConfiguration conf = plugin.getConfig();

            Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
            ArrayList<String> keys = new ArrayList<String>(set);

            String correctKey = null;
            
            for (String key : keys) {
                if (conf.getInt("pressureplates."+key+".x") == (int) Math.floor(e.getTo().getBlock().getX()) && 
                    conf.getInt("pressureplates."+key+".y") == (int) Math.floor(e.getTo().getBlock().getY()) && 
                    conf.getInt("pressureplates."+key+".z") == (int) Math.floor(e.getTo().getBlock().getZ())) {
                    correctKey = key;
                }
            }

            if (correctKey != null && (!usedPressurePlate.containsKey(player) || !usedPressurePlate.get(player))) {
                String[] effects = conf.getString("pressureplates."+correctKey+".effects").split(" ");

                if (effects[0].equals("add")) {
                    usedPressurePlate.put(player, true);

                    Double total = Math.abs(playerVectors.get(player).getX()) + Math.abs(playerVectors.get(player).getZ());
                    Double factor = Double.valueOf(effects[1]) / total;
                    
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setVelocity(new Vector(playerVectors.get(player).getX()*factor, Double.valueOf(effects[2]),  playerVectors.get(player).getZ()*factor));
                        }
                    }.runTaskLater(plugin, 2L);
                }

                if (effects[0].equals("replace")) {
                    usedPressurePlate.put(player, true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setVelocity(new Vector(Double.valueOf(effects[1]), Double.valueOf(effects[2]), Double.valueOf(effects[3])));
                        }
                    }.runTaskLater(plugin, 2L);
                }

            }
            
        }
        
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (new PressurePlateMaterials().getMaterials().contains(event.getBlock().getType())) {
            Player player = event.getPlayer();
            FileConfiguration conf = plugin.getConfig();
            Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
            ArrayList<String> keys = new ArrayList<String>(set);

            String correctKey = null;
            
            for (String key : keys) {
                if (conf.getInt("pressureplates."+key+".x") == (int) Math.floor(event.getBlock().getX()) && 
                    conf.getInt("pressureplates."+key+".y") == (int) Math.floor(event.getBlock().getY()) && 
                    conf.getInt("pressureplates."+key+".z") == (int) Math.floor(event.getBlock().getZ())) {
                    correctKey = key;
                }
            }

            if (correctKey != null) {
                new Message().sendToPlayer(player, "c", "Removing boost pad in 10 seconds!");

                if (deleteTimers.get(player) == null) {
                    deleteTimers.put(player, new HashMap<Integer, Integer>());
                }

                HashMap<Integer, Integer> times = deleteTimers.get(player);
                times.put(Integer.valueOf(correctKey), 60);

                deleteTimers.put(player, times);
            }
        }
    }
}