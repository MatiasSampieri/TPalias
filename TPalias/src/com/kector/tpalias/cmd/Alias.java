package com.kector.tpalias.cmd;

import com.kector.tpalias.otr.ListItem;
import com.kector.tpalias.otr.ListItemComparator;
import com.kector.tpalias.otr.SaveData;
import com.kector.tpalias.otr.SavesManager;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.netty.PipelineUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        // /fav
        if (command.getName().equalsIgnoreCase("fav")){
            if (args.length > 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /fav [alias]");
                return true;
            }

            if (args.length == 1) {
                int rows = saveman.setFav(args[0], true);

                if (rows == 0) {
                    player.sendMessage(ChatColor.RED + "[ERROR: Alias inexistente]");
                } else {
                    player.sendMessage(ChatColor.AQUA + "Alias " + args[0] + " agregado a favoritos!");
                }
                return true;
            }

            List<ListItem> favList = saveman.getFavs();
            sendInteractListColor(favList, player, "--- [LUGARES FAVORITOS] ---", false);

            return true;
        }

        // /defav
        if (command.getName().equalsIgnoreCase("defav")){
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /defav <alias>");
                return true;
            }

            int rows = saveman.setFav(args[0], false);

            if (rows == 0) {
                player.sendMessage(ChatColor.RED + "[ERROR: Alias inexistente]");
            } else {
                player.sendMessage(ChatColor.AQUA + "Alias " + args[0] + " eliminado de favoritos!");
            }

            return true;
        }

        // /top
        if (command.getName().equalsIgnoreCase("top")) {
            List<ListItem> aliasList = saveman.getTop();
            sendInteractListColor(aliasList, player, "--- [RANKING DE TPs MAS USADOS] ---", true);
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
                List<ListItem> aliasList = saveman.getList();
                //String listStr = makeListStr(aliasList);
                //player.sendMessage(ChatColor.YELLOW + listStr);
                sendInteractListColor(aliasList, player, "--- [TOCA EL NOMBRE PARA TEPEARTE] ---", false);

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
                argument = argument.replace("end", "world_the_end");
                List<ListItem> aliasList = saveman.getList(server.getWorld(argument));
                //String listStr = makeListStr(aliasList);
                //player.sendMessage(ChatColor.YELLOW + listStr);
                sendInteractListColor(aliasList, player, "--- [TOCA EL NOMBRE PARA TEPEARTE] ---", false);

                return true;
            }

            // with player filter
            List<ListItem> aliasList = saveman.getList(argument);
            //String listStr = makeListStr(aliasList);
            //player.sendMessage(ChatColor.YELLOW + listStr);
            sendInteractListColor(aliasList, player, "--- [TOCA EL NOMBRE PARA TEPEARTE] ---", false);

            return true;
        }

        // /del
        if (command.getName().equalsIgnoreCase("del")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[ERROR: cantidad de argumentos] /del <nombre>");
                return true;
            }

            // check if player is owner of alias
            String argument = args[0];
            SaveData savedata = saveman.loadAlias(argument);

            if (savedata == null) {
                player.sendMessage(ChatColor.RED + "[ERROR: alias inexistente]");
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

    private void sendInteractList(List<String> list, Player player, String header) {
        if (header != null) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + header);
        }

        for (String alias : list) {
            TextComponent msg = new TextComponent(alias);
            msg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            msg.setUnderlined(true);
            //msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Haz click para ir...").create()));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/t "+alias));
            player.spigot().sendMessage(msg);
        }
    }

    private void sendInteractListColor(List<ListItem> list, Player player, String header, boolean showCount) {
        if (header != null) {
            player.sendMessage(ChatColor.GOLD + header);

            BaseComponent[] legend = new ComponentBuilder("Colores: ").color(net.md_5.bungee.api.ChatColor.WHITE)
                    .bold(true).append("Overworld ").color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append("Nether ").color(net.md_5.bungee.api.ChatColor.DARK_RED)
                    .append("End").color(net.md_5.bungee.api.ChatColor.DARK_PURPLE)
                    .create();

            player.spigot().sendMessage(legend);
        }

        BaseComponent[] comp = new ComponentBuilder("").create();

        for (ListItem item : list) {
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.GREEN;
            if (item.dim.equals("world_nether")) {
                color = net.md_5.bungee.api.ChatColor.DARK_RED;
            } else if (item.dim.equals("world_the_end")) {
                color = net.md_5.bungee.api.ChatColor.DARK_PURPLE;
            }

            if (showCount) {
                comp = new ComponentBuilder("[" + item.count + "] - ").color(net.md_5.bungee.api.ChatColor.WHITE).bold(false)
                        .append(item.name).color(color).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/t " + item.name))
                        .create();
            } else {
                comp = new ComponentBuilder(item.name).color(color)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/t " + item.name))
                        .bold(true)
                        .create();
            }

            player.spigot().sendMessage(comp);
        }
    }
}
