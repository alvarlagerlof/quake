package com.alvarlagerlof.quakeplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.alvarlagerlof.quakeplugin.Message;


@CommandAlias("qsetup|quakesetup")
public class QSetupCommand extends BaseCommand {

    JavaPlugin plugin;
    FileConfiguration conf;

    public QSetupCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.conf = plugin.getConfig();
    }

    @Default
    public void onDefault(Player player) { 
        new Message().sendToPlayer(player, "c", "Please specify a command");
    }

    @Subcommand("lobby")
    public class Lobby extends BaseCommand {

        @Subcommand("sethere")
        public void doSetLobby(Player player) {
            Location loc = player.getLocation();
    
            conf.set("lobby.world", player.getWorld().getName());
            conf.set("lobby.x", loc.getX());
            conf.set("lobby.y", loc.getY());
            conf.set("lobby.z", loc.getZ());
            conf.set("lobby.yaw", loc.getYaw());
            conf.set("lobby.pitch", loc.getPitch());
            plugin.saveConfig();
    
            new Message().sendToPlayer(player, "f", "Lobby postition updated");
        }

    }    

    @Subcommand("arenas")
    public class Arenas extends BaseCommand {

        @Subcommand("list")
        public void doList(Player player) {
            if (conf.contains("arenas")) {
                Set<String> set = conf.getConfigurationSection("arenas").getKeys(false);
                ArrayList<String> keys = new ArrayList<String>(set);
    
                new Message().sendToPlayer(player, "f", "Arenas: " + String.join(", ", keys));
    
            } else {
                new Message().sendToPlayer(player, "c", "No arenas found");
            }
        }
    
    
        @Subcommand("create")
        public void doCreate(Player player, String name) {
            conf.createSection("arenas." +name);
            conf.createSection("arenas." +name+".spawnpoints");
            conf.set("arenas." +name+".maxplayers", 10);
            conf.set("arenas." +name+".enabled", false);
            plugin.saveConfig();
            
            new Message().sendToPlayer(player, "f", "Created arena " + name);
        }
    
        @Subcommand("delete")
        @CommandCompletion("@arenas")
        public void doDelete(Player player, String arena) {
            if (conf.contains("arenas." + arena)) {
                conf.set("arenas." + arena, null);
                plugin.saveConfig();
            
                new Message().sendToPlayer(player, "f", "Deleted arena " + arena);
            } else {
                new Message().sendToPlayer(player, "c", "The arena " + arena + " does not exist");
            }
        }
      
        @Subcommand("enable")
        @CommandCompletion("@arenas")
        public void doEnable(Player player, String arena) {
            if (conf.contains("arenas." + arena)) {
                conf.set("arenas." + arena + ".enabled", true);
                plugin.saveConfig();
            
                new Message().sendToPlayer(player, "f", "Enabled arena " + arena);
            } else {
                new Message().sendToPlayer(player, "c", "The arena " + arena + " does not exist");
            }
        }
    
        @Subcommand("disable")
        @CommandCompletion("@arenas")
        public void doDisable(Player player, String arena) {
            if (conf.contains("arenas." + arena)) {
                conf.set("arenas." + arena + ".enabled", false);
                plugin.saveConfig();
            
                new Message().sendToPlayer(player, "f", "Disabled arena " + arena);
            } else {
                new Message().sendToPlayer(player, "c", "The arena " + arena + " does not exist");
            }
        }
    
        @Subcommand("rename")
        @CommandCompletion("@arenas")
        public void doRename(Player player, String oldName, String newName) {
            if (conf.contains("arenas." + oldName)) {
                if (!conf.contains("arenas." + newName)) {
                    conf.set("arenas." + newName + "", conf.getConfigurationSection("arenas." + oldName));
                    conf.set("arenas." + oldName + "", null);
                    plugin.saveConfig();
        
                    new Message().sendToPlayer(player, "f", "Renamed arena " + oldName + " to " + newName);
                } else {
                    new Message().sendToPlayer(player, "c", "An arena named " + newName + " does already exist");
                }
            } else {
                new Message().sendToPlayer(player, "c", "The arena " + oldName + " does not exist");
            }
        }
    
        @Subcommand("setmaxplayers")
        @CommandCompletion("@arenas")
        public void doSetMaxPlayers(Player player, String arena, Integer maxPlayers) {
            if (conf.contains("arenas." + arena)) {
                    if (maxPlayers < 4) {
                    new Message().sendToPlayer(player, "f", "Please set max to at least 4 players");
                } else {
                    conf.set("arenas." + arena + ".maxplayers", maxPlayers);
                    plugin.saveConfig();
                
                    new Message().sendToPlayer(player, "f", "Set max players of arnea " + arena + " to " + maxPlayers.toString());
                }
            } else {
                new Message().sendToPlayer(player, "c", "The arena " + arena + " does not exist");
            }
        }

    }    

    @Subcommand("spawnpoints")
    public class Spawnpoints extends BaseCommand {
   
        @Subcommand("create")
        @CommandCompletion("@arenas")
        public void doCreateSpawnpoint(Player player, String arena) {
            if (!conf.contains("arenas." + arena)) {
                new Message().sendToPlayer(player, "c", "The arena " + arena + " does not exist");

            } else {
                Location loc = player.getLocation();

                int size = 0;

                if (conf.getConfigurationSection("arenas." + arena +".spawnpoints").getKeys(false) != null) {
                    size = conf.getConfigurationSection("arenas." + arena +".spawnpoints").getKeys(false).size();
                }

                String base = "arenas." + arena + ".spawnpoints." + String.valueOf(Integer.valueOf(size)+1);

                conf.set(base+".world", player.getWorld().getName());
                conf.set(base+".x", loc.getX());
                conf.set(base+".y", loc.getY());
                conf.set(base+".z", loc.getZ());
                conf.set(base+".yaw", loc.getYaw());
                conf.set(base+".pitch", loc.getPitch());
                plugin.saveConfig();

                new Message().sendToPlayer(player, "f", "Created spawnpoint in arena " + arena);
            }
        }

        @Subcommand("delete")
        @CommandCompletion("@arenas")
        public void doDeleteSpawnpoint(Player player, String arena) {
            if (conf.getConfigurationSection("arenas." + arena + ".spawnpoints").getKeys(false).size() > 0) {
                Set<String> set = conf.getConfigurationSection("arenas." + arena + ".spawnpoints").getKeys(false);
                ArrayList<String> keys = new ArrayList<String>(set);
            
                double shortestDistance = 10000000;
                int shortestKey = 1000;

                for (int i = 0; i < keys.size(); i++) {
                    Location location = new Location(
                        player.getWorld(), 
                        conf.getDouble("arenas."+arena+".spawnpoints."+keys.get(i)+".x"), 
                        conf.getDouble("arenas."+arena+".spawnpoints."+keys.get(i)+".y"), 
                        conf.getDouble("arenas."+arena+".spawnpoints."+keys.get(i)+".z")
                    );
                    
                    double distance = player.getLocation().distance(location);

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        shortestKey = i;
                    }
                }

                conf.set("arenas." + arena + ".spawnpoints." + keys.get(shortestKey), null);
                plugin.saveConfig();

                new Message().sendToPlayer(player, "f", "Deleted spawnpoint in arena " + arena);


            } else {
                new Message().sendToPlayer(player, "f", "No spawnpoints for " + arena + " found");
            }
        }
    }

    @Subcommand("pp|pressureplates")
    public class PressurePlates extends BaseCommand {

        @Subcommand("write")
        public void doWritePressurePlate(Player player, String[] args) {
        
            Location loc = player.getLocation();

            // # of pressureplates
            int size = 0;
            String keyAtPlayerLocation = null;

            if (conf.contains("pressureplates")) {
                size = conf.getConfigurationSection("pressureplates").getKeys(false).size();

                 // If pressureplate is saved
                Set<String> set = conf.getConfigurationSection("pressureplates").getKeys(false);
                ArrayList<String> keys = new ArrayList<String>(set);

                
                for (String key : keys) {
                    if (conf.getInt("pressureplates."+key+".x") == (int) Math.floor(loc.getX()) && 
                        conf.getInt("pressureplates."+key+".y") == (int) Math.floor(loc.getY()) && 
                        conf.getInt("pressureplates."+key+".z") == (int) Math.floor(loc.getZ())) {
                        keyAtPlayerLocation = key;
                    }
                }
            }

           
            
            // Create base
            String base = "pressureplates.";

            if (!conf.contains("pressureplates")) {
                base += "1";
            } else if (keyAtPlayerLocation == null) {
                base += String.valueOf(size+1);
            } else {
                base += String.valueOf(keyAtPlayerLocation);
            }

            // Write
            conf.set(base+".world", player.getWorld().getName());
            conf.set(base+".x", (int) Math.floor(loc.getX()));
            conf.set(base+".y", (int) Math.floor(loc.getY()));
            conf.set(base+".z", (int) Math.floor(loc.getZ()));
            conf.set(base+".effects", String.join(" ", args));
            plugin.saveConfig();

            new Message().sendToPlayer(player, "f", keyAtPlayerLocation == null ? "Boost pad created" : "Boost pad overwritten");
        
        }

        // @Subcommand("clear")
        // public void doDeletePressurePlate(Player player, String[] args) {
        
          
        
        // }

    }

}