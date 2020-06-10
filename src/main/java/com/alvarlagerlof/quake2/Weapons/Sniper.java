package com.alvarlagerlof.quake2.Weapons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alvarlagerlof.quake2.Bullets.IBullet;
import com.alvarlagerlof.quake2.Bullets.ShotgunBullet;
import com.alvarlagerlof.quake2.Bullets.SniperBullet;
import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.SoundManager;
import com.alvarlagerlof.quake2.Weapons.IWeapon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Sniper implements IWeapon {

    public String name = "Sniper";
    public List<String> lore = Arrays.asList("1 kula", "Snabb");

    public Sound sound = Sound.ENTITY_PIG_DEATH;

    public ItemStack item = new ItemStack(Material.WOODEN_HOE);
    public Integer durability = 100;
    public Integer gunTimer = 80;

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public List<String> getLore() {
        return lore;
    }

    public Sound getSound() {
        return sound;
    }

    public void runTimer() {
        if (gunTimer > 0) {
            gunTimer--;
        }
    }

    public Set<IBullet> shoot(Vector direction, Location location, QuakePlayer shooter, Set<QuakePlayer> gamePlayers) {
        new SoundManager(shooter.getPlayer().getWorld()).playForGroup(shooter.getPlayer().getLocation(), gamePlayers,
                sound);

        Set<IBullet> set = new HashSet<>();
        set.add(new SniperBullet(shooter, location, direction));

        return set;
    }

}