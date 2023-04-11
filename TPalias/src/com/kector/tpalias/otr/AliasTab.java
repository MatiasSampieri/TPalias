package com.kector.tpalias.otr;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AliasTab implements TabCompleter {

    private SavesManager saveman;

    public AliasTab(SavesManager saveman) {
        this.saveman = saveman;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> aliasList = saveman.onlyNames(saveman.getList());
            List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

            for (Player player : playerList) {
                aliasList.add(player.getDisplayName());
            }

            return aliasList;
        }
        return null;
    }
}
