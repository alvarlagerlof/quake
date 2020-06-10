package com.alvarlagerlof.quake2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Game { 

    String arena; 
    Main plugin;
    Boolean debug = false;

    Integer defaultGunTimer = 30;
    Integer defaultFlyBoostTimer = 80;

    Integer winScore = 25;
    Integer playersToStart = 2;
    Boolean fastTimer = false;
    Integer bulletLifetime = 4;
    Double bulletSpeed = 0.5;
    Integer bulletChecks = 50;
    Integer bulletParticleNum = 2;

    Boolean gameRunning = false;
    Boolean gameWon = false;
    Boolean gameCountDownStarted = false;
    Boolean firstKill = true;
    

    List<Player> players = new ArrayList<Player>();
    List<Bullet> bullets = new ArrayList<Bullet>();
   
    HashMap<Player, Integer> kills = new HashMap<Player, Integer>();
    HashMap<Player, Integer> gunTimers = new HashMap<Player, Integer>();
    HashMap<Player, Integer> flyBoostTimers = new HashMap<Player, Integer>();
    
    HashMap<Player, CustomScoreboard> scoreboards = new HashMap<Player, CustomScoreboard>();

    public Game(Main plugin, String arena, Boolean debug) {
        this.plugin = plugin;
        this.arena = arena;

        if (plugin.getDebug()) updateDebug();

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

                    player.setExp(1f-((float)time/defaultFlyBoostTimer));

                    // 100 -> 0 | 0 -> 6.9

                });

            

                List<Bullet> bulletsToRemove = new ArrayList<Bullet>();

                for (Bullet bullet : bullets) {

                    // Increase lifetime
                    bullet.increaseLifetime();

                    if (bullet.getLifeTime() < bulletLifetime) {


                        for (int i = 0; i < bulletChecks; ++i) {

                            // Finally move bullet forwards
                            bullet.setLocation(bullet.getLocation().add(bullet.getDirection()));

                            // Remove if hit block
                            List<Block> nearbyBlocks = getNearbyBlocks(bullet.getLocation(), 1);

                            for (Block block : nearbyBlocks) {
                                BoundingBox boundingBox = block.getBoundingBox();

                                if (plugin.getDebug()) particleHighLightBoundingBox(Particle.DRIP_WATER, block.getWorld(), block.getBoundingBox());
                            
                                if (intersectsRay(bullet.getLocation(), bullet.getDirection(), boundingBox)) {
                                    if (plugin.getDebug()) particleHighLightBoundingBox(Particle.DRIP_LAVA, block.getWorld(), boundingBox);
                                    bullet.setLifetime(bulletLifetime);
                                    bullet.setActive(false);
                                }
                            }

                            

                            
                            // Remove if hit player
                            if (bullet.getLifeTime() < bulletLifetime && bullet.isActive()) {
                                for (Player player : players) {
                                    BoundingBox boundingBox = player.getBoundingBox();
        
                                    if (plugin.getDebug()) particleHighLightBoundingBox(Particle.DRIP_WATER, player.getWorld(), player.getBoundingBox());
                                
                                    if (intersectsRay(bullet.getLocation(), bullet.getDirection(), boundingBox) &&
                                        player != bullet.getPlayer() && (
                                            bullet.getLocation().distance(player.getLocation().add(0, 1, 0)) <= 1.5 ||
                                            bullet.getLocation().distance(player.getLocation().add(0, 2, 0)) <= 1.5)) {

                                        if (plugin.getDebug()) particleHighLightBoundingBox(Particle.DRIP_LAVA, player.getWorld(), boundingBox);

                                        if (!bullet.getkilledPlayers().contains(player) && !player.isDead() && bullet.getLifeTime() < bulletLifetime) {
                                            kill(bullet, player);
                                        }
                                    }
                                }

                                // Particles
                                if (i % bulletParticleNum == 0) {
                                    bullet.getPlayer().getWorld().spawnParticle(Particle.END_ROD, bullet.getLocation(), 1, 0, 0, 0, 0.005, null, true);
                                } else {
                                    bullet.getPlayer().getWorld().spawnParticle(Particle.END_ROD, bullet.getLocation(), 1, 0, 0, 0, 0.005, null, false);
                                }
                        
                            }

                          
                        }                  
                        
                    } else {
                        bulletsToRemove.add(bullet);
                    }
                }

                bullets.removeAll(bulletsToRemove);
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

    public void updateDebug() {
        if (plugin.getDebug()) {
            fastTimer = true;
            winScore = 5;
            playersToStart = 1;
        }
      
    }


    public void soundForAllPlayers(Player origin, Sound sound) {
        for (Player p: players){
            double volume = 2.0 * 1 - (origin.getLocation().distance(p.getLocation()) / 15);
            p.getWorld().playSound(origin.getLocation(), sound, (float) volume, 1);
        }
    }


    public void join(Player player) {

        // Player meta
        player.setGameMode(GameMode.SURVIVAL);
        ((PlayerInventory)player.getInventory()).clear();
        player.setWalkSpeed(0.4f);

        // Delay giving bed to not leave immediately
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getInventory().addItem(new ItemStack(Material.WOODEN_HOE));
                player.getInventory().addItem(new ItemStack(Material.GREEN_BED));
            }
            
        }.runTaskLater(this.plugin, 5);

        players.add(player);
        kills.put(player, 0);
        gunTimers.put(player, defaultGunTimer);
        flyBoostTimers.put(player, defaultFlyBoostTimer);

        // Scoreboard
        scoreboards.put(player, new CustomScoreboard(plugin, player));
        players.forEach((p) -> scoreboards.get(p).update("&6&lQuake2", getScoreboardList(), true));


        // Random spawn point
        Location spawnPoint = new Teleport(plugin, player).getSpawnPoint(arena, players);
        if (spawnPoint != null) player.teleport(spawnPoint);

        // Announce
        FileConfiguration conf = plugin.getConfig();
        new Message().sendToAll(plugin.getServer(), "f", "(" + String.valueOf(players.size()) + " / " + String.valueOf(conf.getInt("arenas."+arena+".maxplayers")) + ") " + player.getDisplayName() + " joined the arena: " + arena);         


        // Start game
        if (players.size() >= playersToStart && !gameCountDownStarted) {
            gameCountDownStarted = true;

            new BukkitRunnable() {

                int i = fastTimer ? 5 : 31;

                @Override 
                public void run() {
                    // Play sound
                    if (i <= 11) {
                        soundForAllPlayers(player, Sound.BLOCK_NOTE_BLOCK_PLING);
                        players.forEach((p) -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
                    }

                    // Reset if not enough players
                    if (players.size() < playersToStart) {
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
        if (players.contains(player)) {
            // Announce
            new Message().sendToAll(plugin.getServer(), "f", player.getDisplayName() + " left the arena: " + arena); 

            // Player meta
            player.setWalkSpeed(0.2f);
            ((PlayerInventory) player.getInventory()).clear();

            // Remove from lists
            players.remove(player);
            kills.remove(player);
            gunTimers.remove(player);
            flyBoostTimers.remove(player);
            scoreboards.get(player).update("&6&lQuake2", getScoreboardList(), false);
            scoreboards.entrySet().forEach(entry -> {
                entry.getValue().update("&6&lQuake2", getScoreboardList());
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
                gunTimers.clear();
                flyBoostTimers.clear();
                scoreboards.clear();
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
                    players.forEach((p) -> p.getInventory().clear());

                } else {
                    return;
                }
               
            }
        }, 0L, 20L);
        

        // Announce
        new Message().sendTitleToPlayerGroup(plugin.getServer(), players, "§6Winner", player.getDisplayName());

        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", new Message().centerMessage("-----------------------------------------------------"));
        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", "");
        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "f", new Message().centerMessage("&6&l&nTOP 3"));
        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", "");

        kills.entrySet().stream()
        .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
        .limit(3)
        .forEach(k -> {
            Player key = k.getKey();
            Integer killCount = k.getValue();
        
            new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", new Message().centerMessage(key.getDisplayName() + ": " + killCount + " kills"));

        });
        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", new Message().centerMessage("-----------------------------------------------------"));
        new Message().sendToPlayerGroup(plugin.getServer(), players, false, "7", "");


        // Leave all
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (players.size() > 0) {
                    gameRunning = false;
                    gameWon = false;
                    gameCountDownStarted = false;
                    firstKill = true;
    
                    // Leave
                    List<Player> newList = new ArrayList<Player>(players);
                    newList.forEach((p) -> leave(p));
    
                     // Clear lists
                    players.clear();
                    kills.clear();
                    gunTimers.clear();
                    flyBoostTimers.clear();
                    scoreboards.clear();
                }
            }
        }, 20*7);
       
    }

    public void kill(Bullet bullet, Player playerHit) {

        gameRunning = true;

        // Save to kills hashmap
        kills.put(bullet.getPlayer(), kills.get(bullet.getPlayer()) + 1);
   
        // Update scoreboards
        players.forEach((player) -> scoreboards.get(player).update("&6&lQuake2", getScoreboardList())); 

        // Kill player
        playerHit.setHealth(0);

        // Spawn firework
        new Fireworks(plugin, playerHit.getWorld(), playerHit.getLocation()).spawnDeath();

        // Broadcast
        new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", bullet.getPlayer().getDisplayName() + " dissolved " + playerHit.getDisplayName());         

        // First kill
        if (firstKill) {
            new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", "&lFIRST BLOOD");         
            firstKill = false;
        }

        // Sniper
        if (gunTimers.get(bullet.getPlayer()) < 5) {
            new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", "&lSNIPER");  
        }
        
        // Add killed players to bullet
        bullet.addKilledPlayer(playerHit);

        // Display potential achievement
        switch (bullet.getkilledPlayers().size()) {
            case 1: break;            
            case 2:
                new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", "&lDOUBLE KILL");      
                break;
            case 3: 
                new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", "&lTRIPLE KILL");         
                break; 
            default: 
                new Message().sendToPlayerGroup(plugin.getServer(), players, true, "c", "&lMULTI KILL!!!!! CHAMPION!!");         
                break;
        }

        // Check if win
        if (kills.get(bullet.getPlayer()) == winScore) {
            win(bullet.getPlayer());
        }
    }







    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (players.contains(player)) {
            event.setKeepInventory(true);
        }
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

    //Stop players from dropping items
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (players.contains(player)) {
            event.setCancelled(true);
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material material = player.getInventory().getItemInMainHand().getType();

        if (players.contains(player)) {

        
            event.setCancelled(true);
        
            // Leave
            if (material == Material.GREEN_BED && (
                event.getAction() == Action.LEFT_CLICK_AIR || 
                event.getAction() == Action.LEFT_CLICK_BLOCK || 
                event.getAction() == Action.RIGHT_CLICK_AIR || 
                event.getAction() == Action.RIGHT_CLICK_BLOCK
            )) {
                leave(player);
            }

            // Fly boost
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (flyBoostTimers.containsKey(player) && flyBoostTimers.get(player) < 1) {
                    soundForAllPlayers(player, Sound.ENTITY_ENDER_DRAGON_FLAP);

                    double pitch = ((player.getLocation().getPitch() + 90) * Math.PI) / 180;
                    double yaw = ((player.getLocation().getYaw() + 90)  * Math.PI) / 180;

                    double x = Math.cos(yaw) * Math.sin(pitch); 
                    double z = Math.sin(yaw) * Math.sin(pitch);
                    double y = Math.cos(pitch);

                    Vector vector = new Vector(x, y, z);

                    //Rocket jump or fly
                    if (event.getAction() == Action.LEFT_CLICK_AIR) {
                        vector.multiply(1.2);
                    } else {
                        vector = new Vector(x, y*0.5, z);
                        vector.multiply(-2.2);
                    }
                   
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
    
    
                // Set timer
                gunTimers.put(player, defaultGunTimer);
    
                // Sound
                soundForAllPlayers(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
    
                // Vector stuff
                final Location loc = player.getEyeLocation();
                final Vector vector = loc.getDirection().normalize().multiply(bulletSpeed);
     
                bullets.add(new Bullet(player, loc, vector));
    
            } else if (material == Material.WOODEN_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                       (gameWon || !gameRunning)) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 1);
                new Message().sendToPlayer(player, "7", "The game has not started");         
            
            } else if (material == Material.WOODEN_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 1);
      
            }
        }

    }

    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (players.contains(player)) {
            // Cancel default
            event.setCancelled(true);
        }
    }







    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> foundBlocks = new ArrayList<Block>();

        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (!new MaterialLists().getBulletBypassMaterials().contains(block.getType())) {
                        foundBlocks.add(block);
                    }
                   
                }
            }
        }

        return foundBlocks;
    }

    public Boolean getGameRunning() {
        return gameRunning;
    }

    public String getArena() {
        return arena;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<String> getScoreboardList() {
        List<String> list = new ArrayList<String>();
       
        list.add("&c&lKills");

        kills.entrySet().stream()
            .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
            .forEach(k -> {
                Player key = k.getKey();
                Integer killCount = k.getValue();
            
                list.add(key.getDisplayName() + ": &e" + killCount);
            });

        list.add("--------------");
        list.add("&2&lMap: &r" + arena);

        return list;
    }

    






    public void particleHighLightBoundingBox(Particle particle, World world, BoundingBox box) {
        world.spawnParticle(particle, box.getMaxX(), box.getMaxY(), box.getMaxZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMaxX(), box.getMaxY(), box.getMinZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMinX(), box.getMaxY(), box.getMaxZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMinX(), box.getMaxY(), box.getMinZ(), 2, 0, 0, 0, 0.001);

        world.spawnParticle(particle, box.getMaxX(), box.getMinY(), box.getMaxZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMaxX(), box.getMinY(), box.getMinZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMinX(), box.getMinY(), box.getMaxZ(), 2, 0, 0, 0, 0.001);
        world.spawnParticle(particle, box.getMinX(), box.getMinY(), box.getMinZ(), 2, 0, 0, 0, 0.001);
    }

    public Boolean intersectsRay(Location origin, Vector direction, BoundingBox box) {
        float t = 0f;

        float t1 = ((float) box.getMinX() - (float) origin.getX()) / (float) direction.getX();
        float t2 = ((float) box.getMaxX() - (float) origin.getX()) / (float) direction.getX();
        float t3 = ((float) box.getMinY() - (float) origin.getY()) / (float) direction.getY();
        float t4 = ((float) box.getMaxY() - (float) origin.getY()) / (float) direction.getY();
        float t5 = ((float) box.getMinZ() - (float) origin.getZ()) / (float) direction.getZ();
        float t6 = ((float) box.getMaxZ() - (float) origin.getZ()) / (float) direction.getZ();

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
        if (tmax < 0)
        {
            t = tmax;
            return false;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax)
        {
            t = tmax;
            return false;
        }

        // if tmin < 0 then the ray origin is inside of the AABB and tmin is behind the start of the ray so tmax is the first intersection
        if (tmin < 0) {
            t = tmax;
        } else {
            t = tmin;
        }
        return true;
    }

}