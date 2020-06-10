package com.alvarlagerlof.quake2;

import org.bukkit.Server;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.alvarlagerlof.quake2.DefaultFontInfo;


import java.util.List;

public class Message {
    public Message() {}

    public void sendToAll(Server server, String color, String message) {
        server.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message));
    }

    public void sendToPlayerGroup(Server server, List<Player> players, Boolean sendQuake2, String color, String message) {
        if (sendQuake2) {
            players.forEach((player) -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message))); 
        } else {
            players.forEach((player) -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r &"+color + message))); 
        }
    }

    public void sendTitleToPlayerGroup(Server server, List<Player> players, String title, String message) {
        players.forEach((player) -> player.sendTitle(title, message, 20*1, 20*7, 20*1));
    }

    public void sendToPlayer(Player player, String color, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[QUAKE]&r &"+color + message));
    }

    public String centerMessage(String message) {
        int CENTER_PX = 154;
 
        if (message == null || message.equals("")) return "";
            message = ChatColor.translateAlternateColorCodes('&', message);
            
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;
            
            for(char c : message.toCharArray()){
                if (c == '§'){
                        previousCode = true;
                        continue;
                } else if(previousCode == true){
                        previousCode = false;
                        if (c == 'l' || c == 'L') {
                                isBold = true;
                                continue;
                        } else isBold = false;
                } else {
                        DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                        messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                        messagePxSize++;
                }
            }
            
            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();

            while(compensated < toCompensate){
                    sb.append(" ");
                    compensated += spaceLength;
            }
            return sb.toString() + message;
        }

}


 