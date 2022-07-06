package com.kector.tpalias.cmd;

import com.kector.tpalias.otr.SaveData;
import com.kector.tpalias.otr.SavesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tpa implements CommandExecutor {

    private final SavesManager saveman;
    private final Server server;

    public Tpa(Server server, SavesManager saveman) {
        this.server = server;
        this.saveman = saveman;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        // /t
        if (command.getName().equalsIgnoreCase("t")) {

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /t <jugador o alias>");
                return true;
            }

            String argument = args[0];

            // /tpa help
            if (argument.equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.GOLD + "/t <jugador o alias>");
                return true;
            }

            // check if argument is player
            List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player onlinePlayer : playerList) {
                if (onlinePlayer.getDisplayName().equals(argument)) {
                    Player target = Bukkit.getPlayer(argument);
                    assert target != null;

                    saveman.saveQuick(player);

                    player.teleport(target.getLocation());
                    player.sendMessage(ChatColor.AQUA + "Tepeado a " + argument);
                    return true;
                }
            }

            // check if argument is an alias
            SaveData savedata = saveman.loadAlias(argument);
            if (savedata == null) {
                player.sendMessage(ChatColor.RED + "[ERROR: alias inexistente] \nusa /list para ver la lista de aliases");
                return true;
            }

            saveman.saveQuick(player);

            player.teleport(savedata.getLocation());
            player.sendMessage(ChatColor.AQUA + "Tepeado a " + savedata.getName());
        }

        return true;
    }
}
