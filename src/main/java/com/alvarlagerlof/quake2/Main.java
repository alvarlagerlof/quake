package com.alvarlagerlof.quake2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;

import com.alvarlagerlof.quake2.Commands.*;

public final class Main extends JavaPlugin implements Listener {

    public Set<Game> games = new HashSet<>();

    public void onEnable() {

        // Test game
        games.add(new Game(this));

        getServer().getPluginManager().registerEvents(this, this);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new DefaultCommand(this));
        manager.registerCommand(new LobbyCommand(this));
        manager.registerCommand(new ArenaCommand(this));
        manager.registerCommand(new PreasurePlateCommand(this));
        manager.registerCommand(new SpawnPointCommand(this));
        manager.registerCommand(new EditCommand(this));

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        games.stream().findFirst().get().join(player);
        // todo: Tp to lobby
    }

}
