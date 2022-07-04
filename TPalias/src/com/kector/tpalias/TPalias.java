package com.kector.tpalias;

import com.kector.tpalias.cmd.Tpa;
import com.kector.tpalias.otr.SaveData;
import com.kector.tpalias.otr.SavesManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TPalias extends JavaPlugin {

    @Override
    public void onEnable() {
        Server server = getServer();
        SavesManager saveman = new SavesManager(server);

        Tpa tpa = new Tpa(server, saveman);
        Objects.requireNonNull(getCommand("tpa")).setExecutor(tpa);

        server.getConsoleSender().sendMessage(ChatColor.AQUA + "[TPalias] Enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[TPalias] Disabled!");
    }

}
