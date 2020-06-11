package com.alvarlagerlof.quake2.Bullets;

import java.util.HashSet;
import java.util.Set;

import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Timer;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ShotgunBullet implements IBullet {

    QuakePlayer shooter;
    Location location;
    Vector direction;
    Timer lifetimeTimer = new Timer(100);
    Double speed = 1.0;
    Double damage = 15.0;
    Set<QuakePlayer> killedPlayers = new HashSet<>();

    public ShotgunBullet(QuakePlayer shooter, Location location, Vector direction) {
        this.shooter = shooter;
        this.location = location;
        this.direction = direction;
    }

    public QuakePlayer getShooter() {
        return shooter;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getDamage() {
        return damage;
    }

    public Set<QuakePlayer> getkilledPlayers() {
        return killedPlayers;
    }

    public Timer getLifetimeTimer() {
        return lifetimeTimer;
    }

    public void addKilledPlayer(QuakePlayer player) {
        killedPlayers.add(player);
    }

    public void spawnParticle() {
        shooter.getPlayer().getWorld().spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0.005, null, true);
    }

}
