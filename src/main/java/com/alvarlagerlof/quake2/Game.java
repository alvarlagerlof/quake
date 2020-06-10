package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Bullet;


class Game implements Listener {
    Main plugin;
    public Set<QuakePlayer> players = new HashSet<>();
    public Set<Bullet> bullets = new HashSet<>();


    public Game(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    public void join(Player player) {
        QuakePlayer quakePlayer = new QuakePlayer(player);
        quakePlayer.getPlayer().sendMessage("join game");
        players.add(quakePlayer);
    }

    public void leave(QuakePlayer player) {
        players.remove(player);
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        QuakePlayer player = (QuakePlayer) event.getEntity();
        if (players.contains(player)) {
            player.sendMessage("you died");
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QuakePlayer player = (QuakePlayer) event.getPlayer();
        if (players.contains(player)) {
            player.sendMessage("you quit");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        QuakePlayer player = (QuakePlayer) event.getPlayer();
        if (players.contains(player)) {
            player.sendMessage("you respawned");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        QuakePlayer player = (QuakePlayer) event.getPlayer();
        if (players.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        QuakePlayer player = (QuakePlayer) event.getPlayer();
        if (players.contains(player)) {
            event.setCancelled(true);
        }
    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        QuakePlayer player = (QuakePlayer) event.getPlayer();
        if (players.contains(player)) {
            player.sendMessage("you interacted");
        }  
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            QuakePlayer player = (QuakePlayer) event.getEntity();
            if (players.contains(player)) {
                if (event.getCause() == DamageCause.FALL) {
                    player.setFallDistance(0);
                    event.setCancelled(true);
                }
            }
        }
       
      
    }
}
   
  