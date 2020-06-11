package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import com.alvarlagerlof.quake2.Weapons.Shotgun;
import com.alvarlagerlof.quake2.Weapons.Sniper;
import com.alvarlagerlof.quake2.Bullets.IBullet;
import com.alvarlagerlof.quake2.MathUtil;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

class Game implements Listener {
    Main plugin;
    public Set<QuakePlayer> players = new HashSet<>();
    public Set<IBullet> bullets = new HashSet<>();

    public Game(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                updateBullets();
                players.forEach(p -> {
                    p.showWeaponTimer();
                    p.decreaseWeaponTimers();
                    p.getFlyBoostTimer().decrease();
                    p.showFlyBoostTimer();
                });
            }
        }, 0L, 1L);
    }

    public void join(Player player) {
        player.setWalkSpeed(0.4f);

        QuakePlayer quakePlayer = new QuakePlayer(player);
        players.add(quakePlayer);

        quakePlayer.addWeapon(new Sniper(quakePlayer));
        quakePlayer.addWeapon(new Shotgun(quakePlayer));
        quakePlayer.updateInventory();
    }

    public void leave(QuakePlayer player) {
        player.clearInventrory();
        player.getPlayer().setExp(0f);
        player.getPlayer().setHealth(20.0);
        player.getPlayer().setWalkSpeed(0.2f);
        players.remove(player);
    }

    public QuakePlayer findPlayer(Player player) {
        if (player != null || players.size() == 0) {
            return players.stream().filter(p -> p.getPlayer() == player).findFirst().get();
        }
        return null;
    }

    public void updateBullets() {
        Set<IBullet> bulletsToRemove = new HashSet<>();

        for (IBullet bullet : bullets) {
            for (int i = 0; i < Math.round(bullet.getSpeed()); ++i) {
                if (bullet.getLifetimeTimer().getTime() > 0) {

                    Vector bulletspeed = bullet.getDirection().clone();
                    bullet.setLocation(bullet.getLocation().add(bulletspeed.multiply(0.8)));

                    BoundingBox boundingBox = bullet.getLocation().getBlock().getBoundingBox();

                    if (new MathUtil().intersectsRay(bullet.getLocation(), bullet.getDirection(), boundingBox)) {
                        bullet.getLifetimeTimer().set(0);
                        bulletsToRemove.add(bullet);
                    }

                    for (QuakePlayer player : players) {
                        Boolean dead = player.hit(bullet);
                        if (dead) {
                            player.getPlayer().setHealth(0);
                        }
                    }

                    bullet.spawnParticle();
                }

                bullet.getLifetimeTimer().decrease();
                ;

                if (bullet.getLifetimeTimer().getTime() == 0) {
                    bullet.getLifetimeTimer().set(0);
                    bulletsToRemove.add(bullet);
                }
            }
        }

        bullets.removeAll(bulletsToRemove);

    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (findPlayer(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // QuakePlayer player = (QuakePlayer) event.getEntity();
        // if (players.contains(player)) {
        // player.sendMessage("you died");
        // }

        // todo: tp player
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // QuakePlayer player = (QuakePlayer) event.getPlayer();
        // if (players.contains(player)) {
        // player.sendMessage("you quit");
        // }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // QuakePlayer player = (QuakePlayer) event.getPlayer();
        // if (players.contains(player)) {
        // player.sendMessage("you respawned");
        // }

        // todo: tp player

    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            e.setDamage(0.0);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("du ska inte droppa saker");

        if (findPlayer(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (findPlayer(player) != null) {
            event.setCancelled(true);
        }
    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        QuakePlayer quakePlayer = findPlayer(player);
        if (quakePlayer != null) {
            event.setCancelled(true);

            if (event.getItem() != null && event.getItem().getType() == Material.RED_BED) {
                leave(quakePlayer);
            }

            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                quakePlayer.flyBoost(players, event.getAction() == Action.LEFT_CLICK_BLOCK);
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = player.getEyeLocation();
                Vector vector = location.getDirection().normalize().multiply(0.5);
                bullets.addAll(quakePlayer.shoot(vector, location, players));
            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (findPlayer(player) != null) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (findPlayer(player) != null) {
                if (event.getCause() == DamageCause.FALL) {
                    player.setFallDistance(0);
                    event.setCancelled(true);
                }
            }
        }

    }
}
