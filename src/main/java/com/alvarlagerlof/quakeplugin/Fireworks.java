package com.alvarlagerlof.quakeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.FireworkEffect;
import org.bukkit.Color;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Random;


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