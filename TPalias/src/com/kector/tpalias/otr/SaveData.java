package com.kector.tpalias.otr;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SaveData {

    private final String name;
    private final Location location;
    private final Player player;

    public SaveData(String name, Location location, Player player) {
        this.name = name;
        this.location = location;
        this.player = player;
    }

    public String toCsvStr() {
        String worldName = location.getWorld().getName();
        String playerName = player.getDisplayName();
        String x = Double.toString(location.getX());
        String y = Double.toString(location.getY());
        String z = Double.toString(location.getZ());

        List<String> list = Arrays.asList(name, x, y, z, worldName, playerName);
        return String.join(",", list);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
