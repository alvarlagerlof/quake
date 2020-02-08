package com.alvarlagerlof.quakeplugin;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Bullet {   
    Player player;
    Location location;
    Vector direction;
    Integer lifetime;

    public Bullet(Player player, Location location, Vector direction) {
        this.player = player;
        this.location = location;
        this.direction = direction;
        this.lifetime = 0;
    }
  
    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getDirection() {
        return this.direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Integer getLifeTime() {
        return this.lifetime;
    }

    public void increaseLifeTime() {
        this.lifetime++;
    }
    

}