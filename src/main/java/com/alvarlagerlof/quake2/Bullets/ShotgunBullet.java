package com.alvarlagerlof.quake2.Bullets;

import java.util.HashSet;
import java.util.Set;

import com.alvarlagerlof.quake2.QuakePlayer;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ShotgunBullet implements IBullet {

    QuakePlayer shooter;
    Location location;
    Vector direction;
    Integer lifetime;
    Double speed;
    Set<QuakePlayer> killedPlayers;

    public ShotgunBullet(QuakePlayer shooter, Location location, Vector direction) {
        this.shooter = shooter;
        this.location = location;
        this.direction = direction;
        this.lifetime = 100;
        this.speed = 1.0;
        this.killedPlayers = new HashSet<>();
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

    public Integer getLifetime() {
        return lifetime;
    }

    public void setLifetime(Integer lifetime) {
        this.lifetime = lifetime;
    }

    public void decreaseLifetime() {
        if (lifetime > 0) {
            lifetime--;
        }
    }

    public Double getSpeed() {
        return speed;
    }

    public Set<QuakePlayer> getkilledPlayers() {
        return killedPlayers;
    }

    public void addKilledPlayer(QuakePlayer player) {
        killedPlayers.add(player);
    }

    public void spawnParticle() {
        shooter.getPlayer().getWorld().spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0.005, null, true);
    }

}
