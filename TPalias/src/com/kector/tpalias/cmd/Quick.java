package com.kector.tpalias.cmd;

import com.kector.tpalias.otr.SavesManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quick implements CommandExecutor {

    private final SavesManager saveman;
    private final Server server;

    public Quick(Server server, SavesManager saveman) {
        this.server = server;
        this.saveman = saveman;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        // /quick
        if (command.getName().equalsIgnoreCase("quick")) {
            if (args.length != 0) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /quick");
                return true;
            }

            saveman.saveQuick(player);
            player.sendMessage(ChatColor.AQUA + "Se guardaron tus coodenadas, usa /back para volver aca");
        }

        // /back
        if (command.getName().equalsIgnoreCase("back")) {
            if (args.length != 0) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /back");
                return true;
            }

            player.teleport(saveman.loadQuick(player));
            player.sendMessage(ChatColor.AQUA + "Tepeado");
        }

        return true;
    }
}
