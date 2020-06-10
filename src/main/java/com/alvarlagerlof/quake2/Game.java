package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

class Game implements Listener {
    Main plugin;

    public Game(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Set<String> bullets = new HashSet<>();


    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.sendMessage("du dog");
    }
}
   
  