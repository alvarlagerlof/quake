package com.alvarlagerlof.quake2;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;



abstract class QuakePlayer {

    Player player;
    Integer kills;
    List<ItemStack> inventory; 

    public QuakePlayer(Player player) {
        this.player = player;
        this.kills = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void addToInvetory(ItemStack item) {
        inventory.add(item);
    }

    public void updateInventory() {
        for (ItemStack item : inventory) {
            player.getInventory().addItem(item);
        }
    }

    public void clearInventrory() {
        ((PlayerInventory)player.getInventory()).clear();
    }

}
