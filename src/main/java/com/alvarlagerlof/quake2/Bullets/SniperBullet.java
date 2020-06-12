package com.alvarlagerlof.quake2.Bullets;

import java.util.HashSet;
import java.util.Set;

import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Timer;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class SniperBullet implements IBullet {

    QuakePlayer shooter;
    Location location;
    Vector direction;
    Timer lifetimeTimer = new Timer(5);
    Double speed = 40.0;
    Double damage = 10.0;
    Set<QuakePlayer> killedPlayers = new HashSet<>();
    Set<QuakePlayer> hitPlayers = new HashSet<>();

    public SniperBullet(QuakePlayer shooter, Location location, Vector direction) {
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

    public Timer getLifetimeTimer() {
        return lifetimeTimer;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getDamage() {
        return damage;
    }

    public Set<QuakePlayer> getKilledPlayers() {
        return killedPlayers;
    }

    public void addKilledPlayer(QuakePlayer player) {
        killedPlayers.add(player);
    }

    public Set<QuakePlayer> getHitPlayers() {
        return killedPlayers;
    }

    public void addHitPlayer(QuakePlayer player) {
        killedPlayers.add(player);
    }

    public void spawnParticle() {
        shooter.getPlayer().getWorld().spawnParticle(Particle.END_ROD, location, 1, 0, 0, 0, 0.005, null, true);
    }
}
