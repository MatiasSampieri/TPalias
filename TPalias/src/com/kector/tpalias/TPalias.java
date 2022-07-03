package com.kector.tpalias;

import com.kector.tpalias.cmd.Tpa;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TPalias extends JavaPlugin {

    @Override
    public void onEnable() {
        Tpa tpa = new Tpa();
        Objects.requireNonNull(getCommand("tpa")).setExecutor(tpa);

        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[TPalias] Enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[TPalias] Disabled!");
    }

}
