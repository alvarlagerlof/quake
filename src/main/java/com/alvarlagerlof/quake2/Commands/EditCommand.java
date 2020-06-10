package com.alvarlagerlof.quake2;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.netty.util.internal.PlatformDependent;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;

import java.util.List;
import java.security.KeyStoreSpi;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.alvarlagerlof.quake2.Main;


@CommandAlias("quake|q")
@Subcommand("edit")
@CommandPermission("quake.admin")
public class EditCommand extends BaseCommand {

    Main plugin;

    public EditCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(Player player) { 
        player.sendMessage("Please specify a command");
    }

    @Subcommand("on")
    public void on(Player player) { 
        player.sendMessage("create");
    }

    @Subcommand("off")
    public void off(Player player) { 
        player.sendMessage("delete");
    }

}