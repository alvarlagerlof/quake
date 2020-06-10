package com.alvarlagerlof.quake2;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

import com.alvarlagerlof.quake2.Message;


@CommandAlias("quake|q")
@CommandPermission("quake.admin")
public class QuakeCommand extends co.aikar.commands.BaseCommand {

    JavaPlugin plugin;

    public QuakeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(Player player) { 
        new Message().sendToPlayer(player, "c", "Please specify a command");
    }

    @Subcommand("lobby")
    public void doLobby(Player player, String[] args) {
        Location spawnPoint = new Teleport(plugin, player).getLobby();
        if (spawnPoint != null) player.teleport(spawnPoint);
    }


    
}