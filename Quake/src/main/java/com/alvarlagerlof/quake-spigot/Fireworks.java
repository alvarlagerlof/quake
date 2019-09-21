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
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.World;

import co.aikar.commands.PaperCommandManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Random;
import java.util.List;
import java.util.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.alvarlagerlof.quakeplugin.PlayerDeathListener;
import com.alvarlagerlof.quakeplugin.Message;
import com.alvarlagerlof.quakeplugin.CustomScoreboard;
import com.alvarlagerlof.quakeplugin.QuakeCommand;
import com.alvarlagerlof.quakeplugin.QSetupCommand;
import com.alvarlagerlof.quakeplugin.Game;

public class Fireworks {
    JavaPlugin plugin;
    Location location;
    World world;

    public Fireworks(JavaPlugin plugin, World world, Location location) {
        this.plugin = plugin;
        this.location = location;
        this.world = world;
    }

    public void spawnWin(){
        ArrayList<Color> colors = new ArrayList<Color>();
        colors.add(Color.ORANGE);
        colors.add(Color.SILVER);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.AQUA);

        ArrayList<FireworkEffect.Type> types = new ArrayList<FireworkEffect.Type>();
        types.add(FireworkEffect.Type.BALL);
        types.add(FireworkEffect.Type.BALL_LARGE);
        types.add(FireworkEffect.Type.BURST);
        types.add(FireworkEffect.Type.CREEPER);
        types.add(FireworkEffect.Type.STAR);


        Firework winFirework = (Firework) world.spawn(location, Firework.class);
        FireworkMeta fm = winFirework.getFireworkMeta();
        fm.setPower(8);
        fm.addEffect(FireworkEffect
            .builder()
            .flicker(true)
            .trail(true)
            .with(types.get(new Random().nextInt(types.size())))
            .withColor(colors.get(new Random().nextInt(colors.size())))
            .build()
        );
        
        winFirework.setFireworkMeta(fm);

        new BukkitRunnable() {
            @Override
            public void run() {
                winFirework.detonate();
            }
        }.runTaskLater(plugin, 40L);
    }
    
    public void spawnDeath(){
        Firework deadFirework = (Firework) world.spawn(location, Firework.class);
        FireworkMeta fm = deadFirework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(false).trail(false).with(FireworkEffect.Type.BALL).withColor(Color.YELLOW).build());
        deadFirework.setFireworkMeta(fm);
        deadFirework.detonate();
    }
}