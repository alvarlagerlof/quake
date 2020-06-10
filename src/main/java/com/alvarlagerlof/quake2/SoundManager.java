package com.alvarlagerlof.quake2;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public class SoundManager {
    World world;

    public SoundManager(World world) {
        this.world = world;
    }

    public void playForGroup(Location origin, Set<QuakePlayer> players, Sound sound) {
        for (QuakePlayer p : players) {
            double volume = 1.7 * 1 - (origin.distance(p.getPlayer().getLocation()) / 15);
            p.getPlayer().getWorld().playSound(origin, sound, (float) volume, 1);
        }
    }
}