package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.alvarlagerlof.quake2.QuakePlayer;
import com.alvarlagerlof.quake2.Timer;
import com.alvarlagerlof.quake2.Weapons.IWeapon;
import com.alvarlagerlof.quake2.Bullets.IBullet;

public class QuakePlayer {

    Player player;
    Integer kills;
    Timer flyBoostTimer = new Timer(20 * 4);
    public Set<IWeapon> weapons = new HashSet<>();

    public QuakePlayer(Player player) {
        this.player = player;
        this.kills = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getKills() {
        return kills;
    }

    public void increaseKills() {
        kills++;
    }

    public Boolean isAlive() {
        return !player.isDead();
    }

    public void showWeaponTimer() {
        IWeapon weaponInHand = findCurrentWeapon();
        if (weaponInHand != null) {
            weaponInHand.showTimer();
        }
    }

    public void decreaseWeaponTimers() {
        weapons.forEach(w -> w.getShootTimer().decrease());
    }

    private IWeapon findCurrentWeapon() {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType() != Material.AIR && itemInMainHand.getType() != Material.RED_BED) {
            Material itemInHand = itemInMainHand.getType();
            return weapons.stream().filter(w -> w.getItem().getType() == itemInHand).findAny().orElse(null);
        }

        return null;
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

        ItemStack bed = new ItemStack(Material.RED_BED);
        ItemMeta meta = bed.getItemMeta();
        meta.setDisplayName("Leave");
        bed.setItemMeta(meta);
        player.getInventory().setItem(8, bed);
    }

    public void clearInventrory() {
        ((PlayerInventory) player.getInventory()).clear();
    }

    public Set<IBullet> shoot(Vector direction, Location location, Set<QuakePlayer> gamePlayers) {
        IWeapon weaponInHand = findCurrentWeapon();

        if (weaponInHand != null) {
            return weaponInHand.shoot(direction, location, gamePlayers);
        }

        return new HashSet<>();
    }

    public Boolean hit(IBullet bullet) {
        if (bullet.getLifetimeTimer().getTime() > 0 && bullet.getShooter() != this) {
            BoundingBox boundingBox = player.getBoundingBox();

            if (new MathUtil().intersectsRay(bullet.getLocation(), bullet.getDirection(), boundingBox)
                    && player != bullet.getShooter()
                    && (bullet.getLocation().distance(player.getLocation().add(0, 1, 0)) <= 1.5
                            || bullet.getLocation().distance(player.getLocation().add(0, 2, 0)) <= 1.5)) {

                if (!bullet.getkilledPlayers().contains(this) && !player.isDead()
                        && bullet.getLifetimeTimer().getTime() > 0) {
                    player.setHealth(player.getHealth() - bullet.getDamage());
                    if (player.isDead()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Timer getFlyBoostTimer() {
        return flyBoostTimer;
    }

    public void showFlyBoostTimer() {
        player.setExp(1f - ((float) flyBoostTimer.getTime() / flyBoostTimer.getResetTime()));
    }

    public void flyBoost(Set<QuakePlayer> gamePlayers, Boolean backwards) {
        if (flyBoostTimer.getTime() == 0) {
            flyBoostTimer.reset();

            new SoundManager(player.getWorld()).playForGroup(player.getLocation(), gamePlayers,
                    Sound.ENTITY_ENDER_DRAGON_FLAP);

            double pitch = ((player.getLocation().getPitch() + 90) * Math.PI) / 180;
            double yaw = ((player.getLocation().getYaw() + 90) * Math.PI) / 180;

            double x = Math.cos(yaw) * Math.sin(pitch);
            double z = Math.sin(yaw) * Math.sin(pitch);
            double y = Math.cos(pitch);

            Vector vector = new Vector(x, y, z);

            // Rocket jump or fly boost
            if (backwards) {
                vector = new Vector(x, y * 0.5, z);
                vector.multiply(-1.8);
            } else {
                vector.multiply(1.2);
            }

            player.setVelocity(vector);

        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }

}
