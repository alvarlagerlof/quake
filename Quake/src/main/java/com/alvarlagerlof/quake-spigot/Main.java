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
import org.bukkit.event.player.PlayerMoveEvent;


import co.aikar.commands.PaperCommandManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;


import com.alvarlagerlof.quakeplugin.PlayerDeathListener;
import com.alvarlagerlof.quakeplugin.Message;
import com.alvarlagerlof.quakeplugin.CustomScoreboard;
import com.alvarlagerlof.quakeplugin.QuakeCommand;
import com.alvarlagerlof.quakeplugin.QSetupCommand;
import com.alvarlagerlof.quakeplugin.Game;
import com.alvarlagerlof.quakeplugin.PressurePlateHandler;



public final class Main extends JavaPlugin implements Listener {
    HashMap<String, Game> games = new HashMap<String, Game>();

    SignHandler signHandler;
    PressurePlateHandler pressurePlateHandler;

    public void onEnable() {

        // Enable our class to check for new players using onPlayerJoin()
        getServer().getPluginManager().registerEvents(this, this);

        // Set death listner
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        // Register commands
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new QuakeCommand(this));
        manager.registerCommand(new QSetupCommand(this));

        manager.getCommandCompletions().registerAsyncCompletion("arenas", c -> {
            Set<String> set = this.getConfig().getConfigurationSection("arenas").getKeys(false);
            return new ArrayList<>(set);
        });

        // Create sign handler and pressureplatehandler
        signHandler = new SignHandler(this);
        pressurePlateHandler = new PressurePlateHandler(this);
 
        // Update signs each tick
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                signHandler.updateSigns(games); 
                pressurePlateHandler.updatePressurePlates();
            }
        }, 0L, 1L);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Location spawnPoint = new Teleport(this, player).getLobby();
        if (spawnPoint != null) player.teleport(spawnPoint);


        //getServer().getPlayer("Ideaman02").addPassenger((Entity) player);


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
        
            game.leave(player);
        });
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
        
            game.onPlayerDeath(event);
        });
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
        
            game.onPlayerRespawn(event);
        });
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        signHandler.addSign(event);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        signHandler.removeSign(event);
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
            game.onPlayerBreakBlock(event);
        });
    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
            game.onPlayerInteract(event);
        });

        signHandler.clickSign(event);
            
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        Player player = (Player) e.getEntity();
        if (e.getCause() == DamageCause.FALL) {
            player.setFallDistance(0);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
            game.onEntityDamageByEntity(event);
        });
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
            game.onProjectileHit(event);
        });

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        pressurePlateHandler.onPlayerMove(e);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        pressurePlateHandler.onBlockBreak(event);
    }



}                                    

