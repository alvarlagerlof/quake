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

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.CatchUnknown;

import java.util.ArrayList;
import java.util.HashMap;

import com.alvarlagerlof.quakeplugin.Message;

import com.alvarlagerlof.quakeplugin.Game;


@CommandAlias("quake|q")
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