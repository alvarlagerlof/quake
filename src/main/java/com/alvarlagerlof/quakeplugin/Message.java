package com.alvarlagerlof.quakeplugin;

import org.bukkit.Server;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {
    public Message() {}

    public void sendToAll(Server server, String color, String message) {
        server.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message));
    }

    public void sendToPlayerGroup(Server server, List<Player> players, String color, String message) {
        players.forEach((player) -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message))); 
    }

    public void sendToPlayer(Player player, String color, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message));
    }
}
 