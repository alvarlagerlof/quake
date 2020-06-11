package com.alvarlagerlof.quake2.Weapons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alvarlagerlof.quake2.Bullets.IBullet;
import com.alvarlagerlof.quake2.Bullets.MachinegunBullet;
import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.SoundManager;
import com.alvarlagerlof.quake2.Timer;
import com.alvarlagerlof.quake2.Weapons.IWeapon;
import com.google.common.graph.ElementOrder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Machinegun implements IWeapon {
    QuakePlayer owner;

    String name = "Machinegun";
    List<String> lore = Arrays.asList("1 kula", "Snabb");

    Sound sound = Sound.ENTITY_FIREWORK_ROCKET_LAUNCH;

    ItemStack item = new ItemStack(Material.STONE_HOE);
    Integer durability = 100;
    Timer shootTimer = new Timer(5);

    public Machinegun(QuakePlayer player) {
        this.owner = player;
    }

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

    public Timer getShootTimer() {
        return shootTimer;
    }

    public void showTimer() {
        new WeaponUtils().showActionBar(owner, shootTimer.getResetTime(), shootTimer.getTime());
    }

    public Set<IBullet> shoot(Vector direction, Location location, Set<QuakePlayer> gamePlayers) {
        if (shootTimer.getTime() == 0) {
            shootTimer.reset();

            new SoundManager(owner.getPlayer().getWorld()).playForGroup(owner.getPlayer().getLocation(), gamePlayers,
                    sound);

            Set<IBullet> set = new HashSet<>();
            set.add(new MachinegunBullet(owner, location, direction));

            return set;
        } else {
            owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 0.1f, 1f);
            return new HashSet<>();

        }

    }

}