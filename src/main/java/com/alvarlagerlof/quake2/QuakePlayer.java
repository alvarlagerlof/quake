package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.alvarlagerlof.quake2.Weapons.IWeapon;
import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Bullets.IBullet;

public class QuakePlayer {

    Player player;
    Integer kills;
    public Set<IWeapon> weapons = new HashSet<>();

    public QuakePlayer(Player player) {
        this.player = player;
        this.kills = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean isAlive() {
        return !player.isDead();
    }

    public Set<IBullet> shoot(Vector direction, Location location, Set<QuakePlayer> gamePlayers) {
        Material itemInHand = player.getInventory().getItemInMainHand().getType();
        IWeapon weaponInHand = weapons.stream().filter(w -> w.getItem().getType() == itemInHand).findFirst().get();

        if (weaponInHand != null) {
            player.getWorld().playSound(player.getLocation(), weaponInHand.getSound(), 1, 1);

            return weaponInHand.shoot(direction, location, this, gamePlayers);
        }

        return new HashSet<>();
    }

    public void addWeapon(IWeapon weapon) {
        weapons.add(weapon);
    }

    public void updateInventory() {
        clearInventrory();

        for (IWeapon weapon : weapons) {
            ItemStack newItemStack = new ItemStack(weapon.getItem().getType());
            ItemMeta meta = weapon.getItem().getItemMeta();
            meta.setLore(weapon.getLore());
            meta.setDisplayName(weapon.getName());
            newItemStack.setItemMeta(meta);
            player.getInventory().addItem(newItemStack);
        }
    }

    public void clearInventrory() {
        ((PlayerInventory) player.getInventory()).clear();
    }

    public Boolean hit(IBullet bullet) {

        if (bullet.getLifetime() > 0 && bullet.getShooter() != this) {
            BoundingBox boundingBox = player.getBoundingBox();

            if (new MathUtil().intersectsRay(bullet.getLocation(), bullet.getDirection(), boundingBox)
                    && player != bullet.getShooter()
                    && (bullet.getLocation().distance(player.getLocation().add(0, 1, 0)) <= 1.5
                            || bullet.getLocation().distance(player.getLocation().add(0, 2, 0)) <= 1.5)) {

                if (!bullet.getkilledPlayers().contains(this) && !player.isDead() && bullet.getLifetime() > 0) {
                    return true;
                }
            }
        }

        return false;
    }

}
