package com.alvarlagerlof.quakeplugin;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class CustomScoreboard {

    List<String> list = new ArrayList<String>();
    String displayName = "";
    ScoreboardManager manager;
    Scoreboard board; 
    Objective objective;
    Player player;
    JavaPlugin plugin;
    List<String> oldList = new ArrayList<String>();
    Boolean enabled = true;

    public CustomScoreboard(JavaPlugin plugin, Player player) {

        this.plugin = plugin;

        this.player = player;
        this.manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();
        this.objective = board.registerNewObjective("test", "dummy", "Test");
    }

    public void update(String displayName, List<String> list, Boolean enabled) {
        this.displayName = displayName;
        this.list = list;
        this.enabled = enabled;



    }

    public void update(String displayName, List<String> list) {
        this.displayName = displayName;
        this.list = list;
    }

    public void updateScoreboard() {

        // Set meta and title
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        // Reset old
        for (int i = 0; i < oldList.size(); i++) {
            board.resetScores(ChatColor.translateAlternateColorCodes('&', oldList.get(i)));
        }

        // Copy and reverse list
        List<String> reverse = new ArrayList<String>(list); 
        Collections.reverse(reverse);

        // Set scores
        for (int i = 0; i < reverse.size(); i++) {
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', reverse.get(i)));
            score.setScore(i);
        }

        // Show to player
        if (enabled) {
            player.setScoreboard(board);
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        // Update old list
        oldList = list;
    }


}
 