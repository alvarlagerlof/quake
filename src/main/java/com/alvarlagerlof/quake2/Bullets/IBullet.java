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
    Set<QuakePlayer> killedPlayers = null;

    public QuakePlayer getShooter();

    public Location getLocation();

    public void setLocation(Location location);

    public Vector getDirection();

    public void setDirection(Vector direction);

    public Double getSpeed();

    public Timer getLifetimeTimer();

    public Set<QuakePlayer> getkilledPlayers();

    public void addKilledPlayer(QuakePlayer player);

    public void spawnParticle();

}
