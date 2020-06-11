package com.alvarlagerlof.quake2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SidebarScoreboard {

    String name;
    List<String> content;

    ScoreboardManager manager;
    Scoreboard board;
    Objective objective;

    public SidebarScoreboard(String name) {
        this.name = name;

        reset();
    }

    void reset() {
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective(name, "dummy criteria", name);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
    }

    public void update(List<String> newContent) {
        Collections.reverse(newContent);
        this.content = newContent;
    }

    public void setForPlayer(QuakePlayer player) {
        reset();

        for (int i = 0; i < content.size(); i++) {
            String entry = ChatColor.translateAlternateColorCodes('&', content.get(i));
            Score score = objective.getScore(entry);
            score.setScore(i);
        }

        player.getPlayer().setScoreboard(board);
    }

    public void hideForPlayer(QuakePlayer player) {
        player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
