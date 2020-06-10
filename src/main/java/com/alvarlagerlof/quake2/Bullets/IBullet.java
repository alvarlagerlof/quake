package com.alvarlagerlof.quake2.Bullets;

import java.util.Set;

import com.alvarlagerlof.quake2.QuakePlayer;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IBullet {

    QuakePlayer shooter = null;
    Location location = null;
    Vector direction = null;
    Integer lifetime = null;
    Double speed = null;
    Set<QuakePlayer> killedPlayers = null;

    public QuakePlayer getShooter();

    public Location getLocation();

    public void setLocation(Location location);

    public Vector getDirection();

    public void setDirection(Vector direction);

    public Integer getLifetime();

    public void setLifetime(Integer lifetime);

    public void decreaseLifetime();

    public Double getSpeed();

    public Set<QuakePlayer> getkilledPlayers();

    public void addKilledPlayer(QuakePlayer player);

    public void spawnParticle();

}
