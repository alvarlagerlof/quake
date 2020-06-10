package com.alvarlagerlof.quake2;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Bullet {   
    Player player;
    Location location;
    Vector direction;
    Integer lifetime;
    Boolean active;
    List<Player> killedPlayers;

    public Bullet(Player player, Location location, Vector direction) {
        this.player = player;
        this.location = location;
        this.direction = direction;
        this.lifetime = 0;
        this.active = true;
        this.killedPlayers = new ArrayList<Player>();
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

    public Boolean isActive() {
        return this.active;
    }

    public void increaseLifetime() {
        this.lifetime++;
    }

    public void setLifetime(Integer lifetime) {
        this.lifetime = lifetime;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Player> getkilledPlayers(){
        return killedPlayers;
    }

    public void addKilledPlayer(Player player) {
        killedPlayers.add(player);
    }
    

}