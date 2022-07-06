package com.kector.tpalias;

import com.kector.tpalias.cmd.Alias;
import com.kector.tpalias.cmd.Quick;
import com.kector.tpalias.cmd.Tpa;
import com.kector.tpalias.events.BackFromTheGrave;
import com.kector.tpalias.otr.AliasTab;
import com.kector.tpalias.otr.SaveData;
import com.kector.tpalias.otr.SavesManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TPalias extends JavaPlugin {

    private SavesManager saveman;

    @Override
    public void onEnable() {
        Server server = getServer();
        this.saveman = new SavesManager(server);


        Tpa tpa = new Tpa(server, saveman);
        AliasTab aliasTab = new AliasTab(saveman);
        Alias alias = new Alias(server, saveman);
        Quick quick = new Quick(server, saveman);

        Objects.requireNonNull(getCommand("t")).setExecutor(tpa);
        Objects.requireNonNull(getCommand("t")).setTabCompleter(aliasTab);

        Objects.requireNonNull(getCommand("alias")).setExecutor(alias);
        Objects.requireNonNull(getCommand("lista")).setExecutor(alias);
        Objects.requireNonNull(getCommand("del")).setExecutor(alias);

        Objects.requireNonNull(getCommand("quick")).setExecutor(quick);
        Objects.requireNonNull(getCommand("back")).setExecutor(quick);

        server.getPluginManager().registerEvents(new BackFromTheGrave(saveman), this);

        server.getConsoleSender().sendMessage(ChatColor.AQUA + "[TPalias] Enabled!");
    }

    @Override
    public void onDisable() {
        saveman.closeConnection();
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[TPalias] Disabled!");
    }

}
