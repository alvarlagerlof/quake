package com.alvarlagerlof.quake2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.alvarlagerlof.quake2.QuakePlayer;

public class HealthScoreboard {

    public void showForPlayer(QuakePlayer player) {
        Objective objective = player.getScoreboard().registerNewObjective("showhealth", Criterias.HEALTH);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.DARK_RED + "❤");
    }

    public void hideForPlayer(QuakePlayer player) {
        player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
