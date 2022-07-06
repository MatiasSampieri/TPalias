package com.kector.tpalias.cmd;

import com.kector.tpalias.otr.SaveData;
import com.kector.tpalias.otr.SavesManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Alias implements CommandExecutor {
    private final SavesManager saveman;
    private final Server server;

    public Alias(Server server, SavesManager saveman) {
        this.server = server;
        this.saveman = saveman;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        // /alias
        if (command.getName().equalsIgnoreCase("alias")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /alias <nombre>");
                return true;
            }

            String argument = args[0];
            // /alias help
            if (argument.equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.GOLD + "/alias <nombre>");
                return true;
            }

            if (saveman.getList().contains(argument)) {
                player.sendMessage(ChatColor.RED + "[ERROR: alias ya esxiste!] \nusa /list para ver la lista de aliases");
                return true;
            }

            saveman.saveAlias(new SaveData(argument, player.getLocation(), player));
            player.sendMessage(ChatColor.AQUA + "Alias " + argument + " creado!");
            return true;
        }


        // /list
        if (command.getName().equalsIgnoreCase("lista")) {
            if (args.length > 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /list [<jugador o dimension>] ");
                return true;
            }

            // no arguments
            if (args.length == 0) {
                List<String> aliasList = saveman.getList();
                String listStr = makeListStr(aliasList);
                player.sendMessage(ChatColor.YELLOW + listStr);
                return true;
            }

            String argument = args[0];

            // /list help
            if (argument.equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.GOLD + "/list [<jugador o dimension>]\nDimensiones: world, nether, end");
                return true;
            }

            if (Arrays.asList("world", "nether", "end").contains(argument)) {
                argument = argument.replace("nether", "world_nether");
                argument = argument.replace("end", "world_end");
                List<String> aliasList = saveman.getList(server.getWorld(argument));
                String listStr = makeListStr(aliasList);
                player.sendMessage(ChatColor.YELLOW + listStr);
                return true;
            }

            // with player filter
            List<String> aliasList = saveman.getList(argument);
            String listStr = makeListStr(aliasList);
            player.sendMessage(ChatColor.YELLOW + listStr);
            return true;
        }

        if (command.getName().equalsIgnoreCase("del")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /del <nombre>");
                return true;
            }

            // check if player is owner of alias
            String argument = args[0];
            SaveData savedata = saveman.loadAlias(argument);

            if (savedata == null) {
                player.sendMessage(ChatColor.RED + "[ERROR: alias o jugador inexistente]");
                return true;
            }

            if (savedata.getPlayer() != player) {
                player.sendMessage(ChatColor.RED + "Solo el jugador que creo el alias puede eliminarlo!");
                return true;
            }

            saveman.deleteSave(argument);
            player.sendMessage(ChatColor.GOLD + "Se borro el alias " + argument);
            return true;
        }
        return true;
    }

    private String makeListStr(List<String> list) {
        StringBuilder aliasListStr = new StringBuilder();

        for (String alias : list) {
            aliasListStr.append("[").append(alias).append("] ");
        }

        return aliasListStr.toString();
    }
}
