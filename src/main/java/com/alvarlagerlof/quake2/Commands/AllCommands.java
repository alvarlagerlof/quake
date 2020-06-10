package com.alvarlagerlof.quake2;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.netty.util.internal.PlatformDependent;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;

import java.util.List;
import java.security.KeyStoreSpi;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.alvarlagerlof.quake2.Main;


@CommandAlias("quake|q")
@CommandPermission("quake.admin")
public class AllCommands extends BaseCommand {

    Main plugin;

    public AllCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(Player player) { 
        
    }

    @Subcommand("lobby")
    public class Lobby extends BaseCommand {

        @Subcommand("sethere")
        public void doSetLobby(Player player) {

        }
    }

    @Subcommand("debug")
    public class Debug extends BaseCommand {

        @Subcommand("on")
        public void enableDebug(Player player) {
           

        }

        @Subcommand("off")
        public void disableDebug(Player player) {
           
        }
    }    

    @Subcommand("arenas")
    public class Arenas extends BaseCommand {

        @Subcommand("list")
        public void doList(Player player) {
           
        }
    
    
        @Subcommand("create")
        public void doCreate(Player player, String name) {
         
        }
    
        @Subcommand("delete")
        @CommandCompletion("@arenas")
        public void doDelete(Player player, String arena) {
           
        }
      
        @Subcommand("enable")
        @CommandCompletion("@arenas")
        public void doEnable(Player player, String arena) {
           
        }
    
        @Subcommand("disable")
        @CommandCompletion("@arenas")
        public void doDisable(Player player, String arena) {
         
        }
    
        @Subcommand("rename")
        @CommandCompletion("@arenas")
        public void doRename(Player player, String oldName, String newName) {
           
        }
    
        @Subcommand("setmaxplayers")
        @CommandCompletion("@arenas")
        public void doSetMaxPlayers(Player player, String arena, Integer maxPlayers) {
           
        }

    }    

    @Subcommand("spawnpoints")
    public class Spawnpoints extends BaseCommand {
   
        @Subcommand("create")
        @CommandCompletion("@arenas")
        public void doCreateSpawnpoint(Player player, String arena) {
           
        }

        @Subcommand("delete")
        @CommandCompletion("@arenas")
        public void doDeleteSpawnpoint(Player player, String arena) {
            
        }
    }

    @Subcommand("pp|pressureplates")
    public class PressurePlates extends BaseCommand {
   
        @Subcommand("set replace")
        public void doSetReplacePressurePlate(Player player, String[] args) {
           
        }

        @Subcommand("set add")
        public void doSetAddPressurePlate(Player player, String[] args) {
          
        } 

    }

}