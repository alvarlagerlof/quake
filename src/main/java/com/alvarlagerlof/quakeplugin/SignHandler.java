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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import com.alvarlagerlof.quakeplugin.Game;
import com.alvarlagerlof.quakeplugin.Message;


public class SignHandler {
    JavaPlugin plugin;
    HashMap<String, Game> games;

    public SignHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addSign(SignChangeEvent event) {
        FileConfiguration conf = plugin.getConfig();
        Location loc = event.getBlock().getLocation();

        if (event.getLines()[0].contains("[QUAKE]") && event.getLines()[1].equals("join")) {
            // Get next index
            int size = 0;
            if (conf.contains("signs")) {
                size = conf.getConfigurationSection("signs").getKeys(false).size();
            }

            String base = "signs." + String.valueOf(Integer.valueOf(size)+1);

            // Update conf
            conf.set(base+".world", event.getPlayer().getWorld().getName());
            conf.set(base+".x", loc.getX());
            conf.set(base+".y", loc.getY());
            conf.set(base+".z", loc.getZ());
        
            plugin.saveConfig();

            // Send message
            new Message().sendToPlayer(event.getPlayer(), "f", "Saved a sign");

        }
    }

    public void removeSign(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        FileConfiguration conf = plugin.getConfig();

        if (block.getType() == Material.BIRCH_SIGN || block.getType() == Material.OAK_SIGN ||
            block.getType() == Material.BIRCH_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN) {   

            if (conf.contains("signs")) {
                Set<String> set = conf.getConfigurationSection("signs").getKeys(false);
                ArrayList keys = new ArrayList<String>(set);
            
                keys.forEach(item -> {
                    Location loc = new Location(
                        plugin.getServer().getWorld(conf.getString("signs."+item+".world")), 
                        conf.getDouble("signs."+item+".x"), 
                        conf.getDouble("signs."+item+".y"), 
                        conf.getDouble("signs."+item+".z")
                    );

                    if (location.getX() == loc.getX() &&
                        location.getY() == loc.getY() &&
                        location.getZ() == loc.getZ()) {

                        new Message().sendToPlayer(event.getPlayer(), "f", "Removed sign save");
                        
                        conf.set("signs."+item, null);
                        plugin.saveConfig();
                    }
                });
            }
        }
    }

    public void clickSign(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            FileConfiguration conf = plugin.getConfig();


            if (block.getType() == Material.BIRCH_SIGN || block.getType() == Material.OAK_SIGN ||
                block.getType() == Material.BIRCH_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN) {

                Sign sign = (Sign) event.getClickedBlock().getState();

                if (sign.getLines()[0].contains("[QUAKE]") && sign.getLines()[1].contains("join")) { 

                    // Create game if does not exist
                    if (!games.containsKey(sign.getLines()[2])) {
                        //new Message().sendToPlayer(player, "c", "Game " + sign.getLines()[2] + "does not exit, creating");
                        //new Message().sendToPlayer(player, "c", "Game " + games.toString());
                        games.put(sign.getLines()[2], new Game(plugin, sign.getLines()[2]));
                    }

                    // Check if already is on arena
                    Boolean playerIsAlreadyOnArena = false;

                    for (HashMap.Entry<String, Game> entry : games.entrySet()) {
                        String key = entry.getKey();
                        Game game = entry.getValue();
                    
                        if (game.getPlayers().contains(player)) {
                            playerIsAlreadyOnArena = true;
                        }
                    }



                    // Only join if possible
                    if (!playerIsAlreadyOnArena) {
                        if (conf.contains("arenas."+sign.getLines()[2])) {
                            if (conf.getBoolean("arenas."+sign.getLines()[2]+".enabled")) {
                                if (games.get(sign.getLines()[2]).getGameRunning() == false) {
                                    if (games.get(sign.getLines()[2]).getPlayers().size() < conf.getInt("arenas."+sign.getLines()[2]+".maxplayers")) {
                                        if (conf.getConfigurationSection("arenas." + sign.getLines()[2] + ".spawnpoints").getKeys(false).size() > 0) { 
                                            games.get(sign.getLines()[2]).join(player);

                                        } else {
                                            new Message().sendToPlayer(player, "c", "This arena has no spawnpoints");
                                        }

                                    } else {
                                        new Message().sendToPlayer(player, "c", "This game is full");
                                    }
                                    
                                } else {
                                    new Message().sendToPlayer(player, "c", "This game is aready running");
                                }
                            } else {
                                new Message().sendToPlayer(player, "c", "This arena is not enabled");
                            }

                        } else {
                            new Message().sendToPlayer(player, "c", "This arena does not exist");
                        }
                       
                    } else {
                        new Message().sendToPlayer(player, "c", "You are alredy on an arena");
                    }
                }
            }
        }
            
        
    }

    public void updateSigns(HashMap<String, Game> games) {
        this.games = games;
        FileConfiguration conf = plugin.getConfig();

        if (conf.contains("signs")) {
            Set<String> set = conf.getConfigurationSection("signs").getKeys(false);
            ArrayList keys = new ArrayList<String>(set);
        
            keys.forEach(item -> {
                Location loc = new Location(
                    plugin.getServer().getWorld(conf.getString("signs."+item+".world")), 
                    conf.getDouble("signs."+item+".x"), 
                    conf.getDouble("signs."+item+".y"), 
                    conf.getDouble("signs."+item+".z")
                );

                Block block = plugin.getServer().getWorld(conf.getString("signs."+item+".world")).getBlockAt(loc);

                if (block.getType() == Material.BIRCH_SIGN || block.getType() == Material.OAK_SIGN ||
                    block.getType() == Material.BIRCH_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN) {

                    Sign sign = (Sign)block.getState();
                
                    if (sign.getLines()[0].contains("[QUAKE]") && sign.getLines()[1].contains("join")) {
                        String signArena = sign.getLines()[2];

                        // Edit apperance
                        String joinColor = (!conf.contains("arenas."+signArena) || !(conf.getConfigurationSection("arenas." + signArena + ".spawnpoints").getKeys(false).size() > 0) || !conf.getBoolean("arenas." + signArena + ".enabled")) ? "&c" : "&0";
                            

                        sign.setLine(0, ChatColor.translateAlternateColorCodes('&', "&l[QUAKE]"));
                        sign.setLine(1, ChatColor.translateAlternateColorCodes('&', joinColor + "&ljoin"));


                        String joined = games.containsKey(signArena) ? String.valueOf(games.get(signArena).getPlayers().size()) : "0";
                        String max = String.valueOf(conf.getInt("arenas."+signArena+".maxplayers"));
                        String playersColor = (joined.equals(max) || (games.containsKey(signArena) ? games.get(signArena).getGameRunning() : false)) ? "&c&l" : "&2&l";

                        if (conf.contains("arenas."+signArena)) {
                            sign.setLine(3, ChatColor.translateAlternateColorCodes('&', playersColor + joined + "&r / " + playersColor + max));
                        } else {
                            sign.setLine(3, ChatColor.translateAlternateColorCodes('&', "&cArena not found"));
                        }


                        sign.update();
                    }
                }
            });
        }
    }

}