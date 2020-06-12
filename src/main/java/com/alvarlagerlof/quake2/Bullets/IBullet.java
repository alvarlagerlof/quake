package com.alvarlagerlof.quake2.Bullets;

import java.util.Set;

import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Timer;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IBullet {

    QuakePlayer shooter = null;
    Location location = null;
    Vector direction = null;
    Timer lifetimeTimer = new Timer(100);
    Double speed = null;
    Double damage = null;
    Set<QuakePlayer> killedPlayers = null;
    Set<QuakePlayer> hitPlayers = null;

    public QuakePlayer getShooter();

    public Location getLocation();

    public void setLocation(Location location);

    public Vector getDirection();

    public void setDirection(Vector direction);

    public Double getSpeed();

    public Double getDamage();

    public Timer getLifetimeTimer();

    public Set<QuakePlayer> getKilledPlayers();

    public Set<QuakePlayer> getHitPlayers();

    public void addKilledPlayer(QuakePlayer player);

    public void addHitPlayer(QuakePlayer player);

    public void spawnParticle();

}
