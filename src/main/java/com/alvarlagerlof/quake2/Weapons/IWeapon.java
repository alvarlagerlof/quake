package com.alvarlagerlof.quake2.Weapons;

import java.util.List;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.alvarlagerlof.quake2.Bullets.IBullet;
import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Timer;

public interface IWeapon {
    QuakePlayer owner = null;

    String name = "Basic gun";
    List<String> lore = Arrays.asList("lore", "multiline");

    Sound sound = Sound.AMBIENT_CAVE;

    ItemStack item = new ItemStack(Material.WOODEN_HOE);
    Timer shootTimer = new Timer(100);
    Integer durability = 0;

    public String getName();

    public ItemStack getItem();

    public List<String> getLore();

    public Sound getSound();

    public Timer getShootTimer();

    public void showTimer();

    public Set<IBullet> shoot(Vector direction, Location location, Set<QuakePlayer> gamePlayers);

}