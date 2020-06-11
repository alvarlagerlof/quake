package com.alvarlagerlof.quake2.Weapons;

import java.text.DecimalFormat;

import com.alvarlagerlof.quake2.QuakePlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WeaponUtils {
    public void showActionBar(QuakePlayer player, Integer timer, Integer currentTimer) {
        double filipNumber = (double) (currentTimer / 20.0);
        double filipBar = (((double) (currentTimer / 20.0) - (timer / 20.0)) * -1.0) + 0.001;

        String roundNumber = new DecimalFormat("#.#").format(filipNumber);
        String roundBar = new DecimalFormat("#.#").format(filipBar);

        String cubes = "";

        for (int i = 0; i < 10; i++) {
            cubes += ((double) i / 10.0 * (timer / 20.0) > Double.parseDouble(roundBar)) ? "&c►" : "&2◄";
        }

        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                        "&7[" + cubes + "&7] &6&L" + String.valueOf(roundNumber) + " seconds")));
    }
}