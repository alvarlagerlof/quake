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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.Sound;
import java.util.HashMap;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.util.BoundingBox;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Random;
import java.util.List;
import java.util.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;


import com.alvarlagerlof.quakeplugin.Message;
import com.alvarlagerlof.quakeplugin.CustomScoreboard;
import com.alvarlagerlof.quakeplugin.Teleport;
import com.alvarlagerlof.quakeplugin.SignHandler;
import com.alvarlagerlof.quakeplugin.Fireworks;
import com.alvarlagerlof.quakeplugin.Bullet;

public class Game {   
    Integer defaultGunTimer = 30;
    Integer defaultFlyBoostTimer = 10;
    Integer winScore = 25;
    Boolean gameRunning = false;
    Boolean gameWon = false;
    Boolean gameCountDownStarted = false;
    Boolean firstKill = true;
    String arena; 
    JavaPlugin plugin;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
   
    HashMap<Player, Integer> kills = new HashMap<Player, Integer>();
    HashMap<Player, ArrayList<Long>> killTimes = new HashMap<Player, ArrayList<Long>>();
    //HashMap<Player, Boolean> currentlyFlying = new HashMap<Player, Boolean>();
    HashMap<Player, Integer> gunTimers = new HashMap<Player, Integer>();
    HashMap<Player, Integer> flyBoostTimers = new HashMap<Player, Integer>();
    HashMap<Player, Integer> joinActionTimers = new HashMap<Player, Integer>();
    
    HashMap<Player, CustomScoreboard> scoreboards = new HashMap<Player, CustomScoreboard>();

    public Game(JavaPlugin plugin, String arena) {
        this.plugin = plugin;
        this.arena = arena;

        new BukkitRunnable() {
            public void run(){
                gunTimers.entrySet().forEach(entry -> {
                    Player player = entry.getKey();
                    Integer time = entry.getValue();
                
                    if (time > 0) {
                        gunTimers.put(player, time - 1);

                        double filip = (((double) (time/20.0)-1.5)*-1.0)+0.001;
                        String round = new DecimalFormat("#.#").format(filip);

                        String cubes = "";

                        for (int i = 0; i < 10; i++) {
                            cubes += ((double) i/10.0*1.5 > Double.parseDouble(round)) ? "&c►" : "&2◄";
                        }

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7[" + cubes + "&7] &6&L" + String.valueOf(round) + " seconds")));
                    }
                });

                flyBoostTimers.entrySet().forEach(entry -> {
                    Player player = entry.getKey();
                    Integer time = entry.getValue();
                
                    if (time > 0) {
                        flyBoostTimers.put(player, time - 1);
                    }

                    player.setTotalExperience​(time);
                    //player.setExp((float) ((100.0-((double) time))/(100.0/6.9)));

                    // 100 -> 0 | 0 -> 6.9

                });

                joinActionTimers.entrySet().forEach(entry -> {
                    Player player = entry.getKey();
                    Integer time = entry.getValue();
                
                    if (time > 0) {
                        joinActionTimers.put(player, time - 1);
                    }
                });

                ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();

                for (Bullet bullet : bullets) { 

                    //new Message().sendToPlayer(bullet.getPlayer(), "f", String.valueOf(bullet.getLocation()));
            
                    // Increase lifetime
                    bullet.increaseLifeTime();

                    if (bullet.getLifeTime() < 100) {
                        bullet.setLocation(bullet.getLocation().add(bullet.getDirection()));

                        // Kill if hit
                        // Player playerShooter = (Player) arrow.getShooter();
                        // Player playerHit = (Player) entityHit;
        
                        // if (!playerShooter.getDisplayName().equals(playerHit.getDisplayName()) &&
                        //     players.contains(playerShooter) && 
                        //     players.contains(playerHit)) {
        
                        //     kill(playerShooter, playerHit);
                        // }

                        // Remove if hit block
                        List<Block> nearbyBlocks = getNearbyBlocks(bullet.getLocation(), 3);

                        for (Block block : nearbyBlocks) {
                            BoundingBox box = block.getBoundingBox();
                            Location point1 = new Location(bullet.getPlayer().getWorld(), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ());
                            Location point2 = new Location(bullet.getPlayer().getWorld(), block.getLocation().getX()+1.0, block.getLocation().getY()+1.0, block.getLocation().getZ()+1.0);
                            
                            //new Message().sendToAll(plugin.getServer(), "f", String.valueOf(point1) + " " + String.valueOf(point2));


                            if (checkAABB(bullet.getLocation(), bullet.getDirection(), point1, point2)) {

                               new Message().sendToAll(plugin.getServer(), "f", "bullet hit block");

                               bulletsToRemove.add(bullet);
                            }
                        }

                        
                        // Particles
                        bullet.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, bullet.getLocation().getX(), bullet.getLocation().getY(), bullet.getLocation().getZ(), 2, 0, 0, 0, 0.001);


                        // Location particleLocation = bullet.getLocation();

                        // for (int i = 0; i < 5; ++i) {
                        //     Vector particleSpeed = bullet.getDirection().clone().multiply(0.2);
                        //     particleLocation.add(particleSpeed);
                        //     bullet.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLocation.getX(), particleLocation.getY(), particleLocation.getZ(), 2, 0, 0, 0, 0.001);
                        // }

                    } else {
                        bulletsToRemove.add(bullet);
                    }
                }

                bullets.remove(bulletsToRemove);
            }

        }.runTaskTimer(plugin, 0, 1);

        new BukkitRunnable() {
            public void run(){
                scoreboards.entrySet().forEach(entry -> {
                    entry.getValue().updateScoreboard();
                });
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getArena() {
        return arena;
    }

    public Boolean getGameRunning() {
        return gameRunning;
    }

    public void join(Player player) {

        // Player meta
        player.setGameMode(GameMode.SURVIVAL);
        ((PlayerInventory)player.getInventory()).clear();
        player.setWalkSpeed(0.4f);
        player.getInventory().addItem(new ItemStack(Material.WOODEN_HOE));
        player.getInventory().addItem(new ItemStack(Material.GREEN_BED));

        // Add to lists
        players.add(player);
        kills.put(player, 0);
        joinActionTimers.put(player, 20);
        killTimes.put(player, new ArrayList<Long>());
        gunTimers.put(player, defaultGunTimer);
        flyBoostTimers.put(player, defaultFlyBoostTimer);

        // Scoreboard
        scoreboards.put(player, new CustomScoreboard(plugin, player));
        players.forEach((p) -> scoreboards.get(p).update("&6&lQuake", getScoreboardList(), true));


        // Random spawn point
        Location spawnPoint = new Teleport(plugin, player).getSpawnPoint(arena, players);
        if (spawnPoint != null) player.teleport(spawnPoint);

        // Announce
        FileConfiguration conf = plugin.getConfig();
        new Message().sendToAll(plugin.getServer(), "f", "(" + String.valueOf(players.size()) + " / " + String.valueOf(conf.getInt("arenas."+arena+".maxplayers")) + ") " + player.getDisplayName() + " joined the arena: " + arena);         


        // Start game
        if (players.size() >= 2 && !gameCountDownStarted) {
            gameCountDownStarted = true;

            new BukkitRunnable() {

                int i = 5;

                @Override
                public void run() {
                    // Play sound
                    if (i <= 11) {
                        players.forEach((player) ->  player.playSound(player.getLocation(), Sound. BLOCK_NOTE_BLOCK_PLING, 2, 1));
                    }

                    // Reset if not enough players
                    if (players.size() < 2) {
                        i = 31; 
                        this.cancel();
                        return;
                    }

                    // Speed up if 4 players join
                    if (players.size() >= 4 && i > 21) {
                        i = 21;
                    }

                    i--;  
                        
                    if (i >= 11) {
                        players.forEach((p) -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6&l" + String.valueOf(i) + " seconds to game start"))));

                    } else if (i > 0 && i != 1) {
                        players.forEach((p) -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&l" + String.valueOf(i) + " seconds to game start"))));

                    } else if (i == 1) {
                        players.forEach((p) -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&l" + String.valueOf(i) + " second to game start"))));

                    } else if (i == 0) {
                        players.forEach((p) -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&2&lThe game has started!"))));
                        players.forEach((p) -> {
                            Location spawnPoint = new Teleport(plugin, player).getSpawnPoint(arena, players);
                            if (spawnPoint != null) p.teleport(spawnPoint);
                        });
                      
                        gameRunning = true;
                        this.cancel();
                        return;
                    }
                }

            }.runTaskTimer(plugin, 0L, 20L);
        }
        
    }

    public void leave(Player player) {

        if (players.contains(player) && joinActionTimers.get(player) == 0) {
            // Announce
            new Message().sendToAll(plugin.getServer(), "f", player.getDisplayName() + " left the arena: " + arena); 

            // Player meta
            player.setWalkSpeed(0.2f);
            ((PlayerInventory)player.getInventory()).clear();

            // Remove from lists
            players.remove(player);
            kills.remove(player);
            joinActionTimers.remove(player);
            gunTimers.remove(player);
            flyBoostTimers.remove(player);
            scoreboards.get(player).update("&6&lQuake", getScoreboardList(), false);
            scoreboards.entrySet().forEach(entry -> {
                entry.getValue().update("&6&lQuake", getScoreboardList());
                entry.getValue().updateScoreboard();
            });
            scoreboards.remove(player);

            // Reset if no players
            if (players.size() == 1 && gameRunning && !gameWon) {
                win(players.get(0));
            }

            if (players.size() == 1) {
                gameCountDownStarted = false;
            }


            if (players.size() == 0) {
                gameRunning = false;
                gameWon = false;
                gameCountDownStarted = false;
                firstKill = true;
                players.clear();
                kills.clear();
                joinActionTimers.clear();
                gunTimers.clear();
                flyBoostTimers.clear();
                scoreboards.clear();
                killTimes.clear();
            }

            // Move to lobby
            Location spawnPoint = new Teleport(plugin, player).getLobby();
            if (spawnPoint != null) player.teleport(spawnPoint);       
        }
       
        
    }

    public void win(Player player) {
        // Set state to won
        gameWon = true;

        // Gigantic fireworks
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 0;
            public void run() {
                i++;

                if (i < 10) {
                    new Fireworks(plugin, player.getWorld(), player.getLocation()).spawnWin();

                } else {
                    return;
                }
               
            }
        }, 0L, 20L);
        
        // Announce
        new Message().sendToPlayerGroup(plugin.getServer(), players, "f", player.getDisplayName() + " won the game");

        // Leave all
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (players.size() > 0) {
                    gameRunning = false;
                    gameWon = false;
                    gameCountDownStarted = false;
                    firstKill = true;
    
                    // Leave
                    ArrayList<Player> newList = new ArrayList<Player>(players);
                    newList.forEach((p) -> leave(p));
    
                     // Clear lists
                    players.clear();
                    kills.clear();
                    joinActionTimers.clear();
                    gunTimers.clear();
                    flyBoostTimers.clear();
                    scoreboards.clear();
                    killTimes.clear();
                }
            }
        }, 20*10);
       
    }

    public void kill(Player playerShooter, Player playerHit) {

        gameRunning = true;

        // Save to kills hashmap
        kills.put(playerShooter, kills.get(playerShooter) + 1);
        ArrayList<Long> prevKillTimes = killTimes.get(playerShooter);
        prevKillTimes.add(System.currentTimeMillis());
        killTimes.put(playerShooter, prevKillTimes);

        // Update scoreboards
        players.forEach((player) -> scoreboards.get(player).update("&6&lQuake", getScoreboardList())); 

        // Kill player
        playerHit.setHealth(0);

        // Spawn firework
        new Fireworks(plugin, playerHit.getWorld(), playerHit.getLocation()).spawnDeath();

        // Broadcast
        new Message().sendToPlayerGroup(plugin.getServer(), players, "c", playerShooter.getDisplayName() + " dissolved " + playerHit.getDisplayName());         

        // First kill
        if (firstKill) {
            new Message().sendToPlayerGroup(plugin.getServer(), players, "c", "&lFIRST BLOOD");         
            firstKill = false;
        }

        // Sniper
        if (gunTimers.get(playerShooter) < 12) {
            new Message().sendToPlayerGroup(plugin.getServer(), players, "c", "&lSNIPER");  
        }

        // Multi-kill
        ArrayList<Long> killTimesPlayer = killTimes.get(playerShooter);

        if (killTimesPlayer.size() > 2) {
            Long latest = killTimesPlayer.get(killTimesPlayer.size() - 1);
            Long nextNextLatest = killTimesPlayer.get(killTimesPlayer.size() - 3);

            if (latest - nextNextLatest < 1450) {
                new Message().sendToPlayerGroup(plugin.getServer(), players, "c", "&lTRIPLE KILL");         
            }
        } else if (killTimesPlayer.size() > 1) {
            Long latest = killTimesPlayer.get(killTimesPlayer.size() - 1);
            Long nextLatest = killTimesPlayer.get(killTimesPlayer.size() - 2);

            if (latest - nextLatest < 1450) {
                new Message().sendToPlayerGroup(plugin.getServer(), players, "c", "&lDOUBLE KILL");         
            }

        }

        // Check if win
        if (kills.get(playerShooter) == winScore) {
            win(playerShooter);
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (players.contains(player)) {
            if (!gameRunning) {
                Location spawnPoint = new Teleport(plugin, player).getLobby();
                if (spawnPoint != null) event.setRespawnLocation(spawnPoint);
            } else {
                Location spawnPoint = new Teleport(plugin, player).getSpawnPoint(arena, players);
                if (spawnPoint != null) event.setRespawnLocation(spawnPoint);
            }
        }
       
    }

    public void onProjectileHit(ProjectileHitEvent event) {
        // Entity damager = event.getEntity();

        // if (damager instanceof Arrow && event.getHitBlock() != null) {
        //     damager.remove();
        //     currentlyFlying.put((Player) ((Arrow)damager).getShooter(), false);
        // }
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Entity damager = event.getDamager();

        // if (damager instanceof Arrow) { // check if the damager is an arrow
        //     Arrow arrow = (Arrow) damager;
        //     Entity entityHit = (Entity) event.getEntity();

        //     if (arrow.getShooter() instanceof Player && entityHit instanceof Player) {
        //         Player playerShooter = (Player) arrow.getShooter();
        //         Player playerHit = (Player) entityHit;

        //         if (!playerShooter.getDisplayName().equals(playerHit.getDisplayName()) &&
        //             players.contains(playerShooter) && 
        //             players.contains(playerHit)) {

        //             kill(playerShooter, playerHit);
        //         }
        //     }
        // }
    }

    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public Boolean checkAABB(Location origin, Vector direction, Location point1, Location point2) {
        Location center = point1.add(point2).multiply(0.5);


        Location origo = new Location(origin.getWorld(), 0, 0, 0);
        Vector distanceOriginOrigo = new Vector(origo.getX()-origin.getX(), origo.getY()-origin.getY(), origo.getZ()-origin.getZ());

        new Message().sendToAll(plugin.getServer(), "c", String.valueOf(point1.getX()));

        point1.subtract(distanceOriginOrigo);
        point2.subtract(distanceOriginOrigo);

        new Message().sendToAll(plugin.getServer(), "f", String.valueOf(point1.getX()));


        return direction.isInAABB​(point1.toVector(), point2.toVector());
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material material = player.getItemInHand().getType();

        if (players.contains(player) && joinActionTimers.get(player) == 0) {
        
            // Leave
            if (material == Material.GREEN_BED && joinActionTimers.get(player) == 0 && (
                event.getAction() == Action.LEFT_CLICK_AIR || 
                event.getAction() == Action.LEFT_CLICK_BLOCK || 
                event.getAction() == Action.RIGHT_CLICK_AIR || 
                event.getAction() == Action.RIGHT_CLICK_BLOCK
            )) {
                leave(player);
            }

            // Fly boost
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (flyBoostTimers.get(player) < 1) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

                    double pitch = ((player.getLocation().getPitch() + 90) * Math.PI) / 180;
                    double yaw  = ((player.getLocation().getYaw() + 90)  * Math.PI) / 180;
                    double x = Math.sin(pitch) * Math.cos(yaw);
                    double y = Math.sin(pitch) * Math.sin(yaw);
                    double z = Math.cos(pitch);

                    Vector vector = new Vector(x, z, y);
                    vector.multiply(1.2);
                    player.setVelocity(vector);

                    flyBoostTimers.put(player, defaultFlyBoostTimer);

                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                }
            }
    
            // Shoot
            if (material == Material.WOODEN_HOE && 
                gunTimers.get(player) == 0 && 
                (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
                gameRunning && !gameWon) {
    
                // Add to currently flying
                //currentlyFlying.put(player, true);
    
                // Set timer
                gunTimers.put(player, defaultGunTimer);
    
                // Sound
                for (Player pl: plugin.getServer().getOnlinePlayers()){
                    double volume = 2.0 * 1 - (player.getLocation().distance(pl.getLocation()) / 15);
                    pl.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, (float) volume, 1);
                }
    
                // Vector stuff
                final Location loc = player.getEyeLocation();
                final Vector vector = loc.getDirection().normalize().multiply(5);
     
                bullets.add(new Bullet(player, loc, vector));

                new Message().sendToPlayer(player, "f", "added a bullet at " + String.valueOf(vector));

                // // Spawn arrow
                // Arrow arrow = (Arrow) player.getWorld().spawnEntity(loc, EntityType.ARROW);
                // arrow.setShooter(player); 
    
    
                // Trail
                // new BukkitRunnable() {
                //     int tickCount = 0;
                //     Location prevArrowLocation = loc;
    
                //     public void run(){
    
                //         // Count
                //         if (tickCount > 55/* || currentlyFlying.get(player) == false*/) {
                //             this.cancel();
                //             // arrow.remove();
                //             //currentlyFlying.put(player, false);
                //             return;
                //         } else {
                //             tickCount++;
                //             //currentlyFlying.put(player, true);

                //             // Calculate new pos
                //             loc.add(vector);

                //             // Move arrow
                //             // arrow.setVelocity(vector);
                //             // arrow.teleport(loc);

                //             // Spawn particles
                //             Location particleLocation = prevArrowLocation;

                //             for (int i = 0; i < 5; ++i) {
                //                 Vector particleSpeed = vector.clone().divide(new Vector(5, 5, 5));
                //                 particleLocation.add(particleSpeed);
                               
                //                 // Spawn particle trail
                //                 player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLocation.getX(), particleLocation.getY(), particleLocation.getZ(), 2, 0, 0, 0, 0.001);
                //             }

                //             // Set prev location
                //             prevArrowLocation = loc;
                //         }
        
                //     }
                // }.runTaskTimer(plugin, 0, 1);
    
            } else if (material == Material.WOODEN_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                       (gameWon || !gameRunning)) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 1);
                new Message().sendToPlayer(player, "7", "The game is not running");         
            
            } else if (material == Material.WOODEN_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 1);
      
            }
        }

    }


    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = player.getItemInHand().getType();

        if (players.contains(player) && joinActionTimers.get(player) == 0) {
            // Cancel default
            event.setCancelled(true);
        }
    }

    public ArrayList getScoreboardList() {
        ArrayList list = new ArrayList();
       
        list.add("&c&lKills");

        for (Map.Entry<Player, Integer> entry : kills.entrySet()) {
            Player key = entry.getKey();
            Integer killCount = entry.getValue();
        
            list.add(key.getDisplayName() + ": &e" + killCount);
        }
        
        list.add("--------------");

        list.add("&2&lMap: &r" + arena);


        return list;
    }

    

}