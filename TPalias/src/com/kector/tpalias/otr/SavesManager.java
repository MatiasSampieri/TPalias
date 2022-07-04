package com.kector.tpalias.otr;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavesManager {

    private final Server server;
    private final Connection connection;

    public SavesManager(Server server) {
        this.server = server;
        this.connection = initDB("aliases.db");

    }

    public void saveAlias(SaveData data) {
        String name = data.getName();
        String player = data.getPlayer().getDisplayName();
        double x = data.getLocation().getX();
        double y = data.getLocation().getY();
        double z = data.getLocation().getZ();
        float yaw = data.getLocation().getYaw();
        float pitch = data.getLocation().getPitch();
        String dim = data.getLocation().getWorld().getName();

        try {
            Statement statement = connection.createStatement();
            String sql = String.format("INSERT INTO Aliases (Name, X, Y, Z, yaw, pitch, Dim, Player) " +
                                       "VALUES (\"%s\", %f, %f, %f, %f, %f, \"%s\", \"%s\");", name, x, y, z, yaw, pitch, dim, player);
            statement.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    public SaveData loadAlias(String name) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT Name FROM Aliases WHERE Name = \"%s\"", name);
            ResultSet res = statement.executeQuery(sql);

            if (!res.next()) {
                return null;
            }

            String dim = res.getString("Dim");
            double x = res.getDouble("X");
            double y = res.getDouble("Y");
            double z = res.getDouble("Z");
            float yaw = res.getFloat("yaw");
            float pitch = res.getFloat("pitch");
            String player = res.getString("Player");

            Location location = new Location(server.getWorld(dim), x, y, z, yaw, pitch);

            return new SaveData(name, location, server.getPlayer(player));

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getList() {
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT Name FROM Aliases";
            ResultSet res = statement.executeQuery(sql);

            List<String> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(res.getString("Name"));
            }

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getList(Player player) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT Name FROM Aliases WHERE Player = \"%s\"", player.getDisplayName());
            ResultSet res = statement.executeQuery(sql);

            List<String> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(res.getString("Name"));
            }

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getList(World dim) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT Name FROM Aliases WHERE Dim = \"%s\"", dim.getName());
            ResultSet res = statement.executeQuery(sql);

            List<String> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(res.getString("Name"));
            }

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }


    private Connection initDB(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + name);
            server.getConsoleSender().sendMessage(ChatColor.GREEN + "[TPalias] DB abierta!");

            // Create tables
            Statement statement = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS \"Aliases\" (" +
                    "\"ID\" INTEGER UNIQUE," +
                    "\"Name\" TEXT NOT NULL UNIQUE," +
                    "\"X\" REAL," +
                    "\"Y\" REAL," +
                    "\"Z\" REAL," +
                    "\"yaw\" REAL," +
                    "\"pitch\" REAL," +
                    "\"Dim\" TEXT," +
                    "\"Player\" TEXT," +
                    "PRIMARY KEY(\"ID\" AUTOINCREMENT));";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS \"QuickSaves\" (" +
                    "\"Player\"TEXT NOT NULL UNIQUE," +
                    "\"X\"REAL," +
                    "\"Y\"REAL," +
                    "\"Z\"REAL," +
                    "\"yaw\" REAL," +
                    "\"pitch\" REAL," +
                    "\"Dim\"TEXT," +
                    "PRIMARY KEY(\"Player\"));";

            statement.executeUpdate(sql);
            statement.close();
            return con;

        } catch ( Exception e ) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al inicar DB");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }
}
