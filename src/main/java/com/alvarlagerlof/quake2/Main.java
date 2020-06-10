package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;

import com.alvarlagerlof.quake2.Game;



public final class Main extends JavaPlugin implements Listener {

    public Set<Game> games = new HashSet<>();
    Boolean debug = false;


    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

        PaperCommandManager manager = new PaperCommandManager(this);
        //manager.registerCommand(new Quake2Command(this));
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Boolean getDebug() {
        return this.debug;
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Hejsan TODO: TP");
    }

    /*@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        games.forEach(g -> g.getValue().leave(event.getPlayer()));
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        games.forEach(g -> g.getValue().onPlayerDeath(event));
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
        
            game.onPlayerRespawn(event);
        });
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        games.entrySet().forEach(entry -> {
            Game game = entry.getValue();
        
            game.onPlayerDropItem(event);
        });
    }


    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
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
    }*/

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        Player player = (Player) e.getEntity();
        if (e.getCause() == DamageCause.FALL) {
            player.setFallDistance(0);
            e.setCancelled(true);
        }
    }
    
    /*@EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }*/

}                                    

