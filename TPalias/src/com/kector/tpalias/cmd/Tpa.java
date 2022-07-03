package com.kector.tpalias.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tpa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        // /tpa
        if (command.getName().equalsIgnoreCase("tpa")) {

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /tpa <jugador o alias>");
                return true;
            }

            // /tpa help
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.GOLD + "/tpa <jugador o alias>");
                return true;
            }

            String argument = args[0];

            // check if argument is player
            List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player onlinePlayer : playerList) {
                if (onlinePlayer.getDisplayName().equals(argument)) {
                    Player target = Bukkit.getPlayer(argument);
                    assert target != null;
                    player.teleport(target.getLocation());
                    player.sendMessage(ChatColor.AQUA + "Tepeado a " + argument);
                    return true;
                }
            }

            // check if argument is an alias

            // TODO: hacer esto

            // if argument is not a player or alias
            player.sendMessage(ChatColor.RED + "[ERROR: argumento no es jugador o alias] /tpa <jugador o alias>");
        }




        return true;
    }
}
