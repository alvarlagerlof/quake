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
    List<String> oldContent;

    ScoreboardManager manager;
    Scoreboard board;
    Objective objective;

    public SidebarScoreboard(String name) {
        this.name = name;
    }

    void reset(QuakePlayer player) {
        board = (Scoreboard) player.getScoreboard();

        if (board.getObjective(name) != null) {
            objective = board.getObjective(name);
        } else {
            objective = board.registerNewObjective(name, "s", name);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
    }

    public void update(List<String> newContent) {
        Collections.reverse(newContent);
        oldContent = content;
        content = newContent;
    }

    public void showForPlayer(QuakePlayer player) {
        reset(player);

        for (int i = 0; i < oldContent.size(); i++) {
            board.resetScores(ChatColor.translateAlternateColorCodes('&', oldContent.get(i)));
        }

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
