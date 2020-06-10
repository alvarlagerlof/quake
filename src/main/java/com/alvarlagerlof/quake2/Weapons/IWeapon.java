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

public interface IWeapon {
    public String name = "Basic gun";
    public List<String> lore = Arrays.asList("lore", "multiline");

    public Sound sound = Sound.AMBIENT_CAVE;

    public ItemStack item = new ItemStack(Material.WOODEN_HOE);
    public Integer gunTimer = 0;
    public Integer durability = 0;

    public String getName();

    public ItemStack getItem();

    public List<String> getLore();

    public Sound getSound();

    public void runTimer();

    public Set<IBullet> shoot(Vector direction, Location location, QuakePlayer shooter, Set<QuakePlayer> gamePlayers);

}