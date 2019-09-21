package com.alvarlagerlof.quakeplugin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.Sound;
import java.util.HashMap;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Random;
import java.util.List;
import java.util.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;


import com.alvarlagerlof.quakeplugin.Message;
import com.alvarlagerlof.quakeplugin.CustomScoreboard;
import com.alvarlagerlof.quakeplugin.Teleport;
import com.alvarlagerlof.quakeplugin.SignHandler;
import com.alvarlagerlof.quakeplugin.Fireworks;


public class Bullet {   
    Player player;
    Location location;
    Vector direction;
    Integer lifetime;

    public Bullet(Player player, Location location, Vector direction) {
        this.player = player;
        this.location = location;
        this.direction = direction;
        this.lifetime = 0;
    }
  
    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getDirection() {
        return this.direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Integer getLifeTime() {
        return this.lifetime;
    }

    public void increaseLifeTime() {
        this.lifetime++;
    }
    

}