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

    public Location loadQuick(Player player) {
        String name = player.getDisplayName();
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT * FROM QuickSaves WHERE Player = \"%s\"", name);
            ResultSet res = statement.executeQuery(sql);

            if (!res.next()) {
                server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al cargar quick save");
                return null;
            }

            double x = res.getDouble("X");
            double y = res.getDouble("Y");
            double z = res.getDouble("Z");
            float yaw = res.getFloat("yaw");
            float pitch = res.getFloat("pitch");
            String dim = res.getString("Dim");

            res.close();
            statement.close();

            return new Location(server.getWorld(dim), x, y, z, yaw, pitch);

        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al cargar quick save");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    public void saveQuick(Player player) {
        String name = player.getDisplayName();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        String dim = player.getLocation().getWorld().getName();

        try {
            Statement statement = connection.createStatement();
            String sql = String.format("INSERT OR REPLACE INTO QuickSaves (Player, X, Y, Z, yaw, pitch, Dim) " +
                    "VALUES (\"%s\", %f, %f, %f, %f, %f, \"%s\");", name, x, y, z, yaw, pitch, dim);
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    public void deleteSave(String name) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("DELETE FROM Aliases WHERE Name = \"%s\";", name);
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
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
            statement.close();
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    public SaveData loadAlias(String name) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT * FROM Aliases WHERE Name = \"%s\"", name);
            ResultSet res = statement.executeQuery(sql);

            if (!res.next()) {
                server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al encontrar alias");
                return null;
            }

            String dim = res.getString("Dim");
            double x = res.getDouble("X");
            double y = res.getDouble("Y");
            double z = res.getDouble("Z");
            float yaw = res.getFloat("yaw");
            float pitch = res.getFloat("pitch");
            String player = res.getString("Player");

            res.close();
            statement.close();

            Location location = new Location(server.getWorld(dim), x, y, z, yaw, pitch);

            return new SaveData(name, location, server.getPlayer(player));

        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al encontrar alias");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
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

            res.close();
            statement.close();

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getList(String player) {
        try {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT Name FROM Aliases WHERE Player = \"%s\"", player);
            ResultSet res = statement.executeQuery(sql);

            List<String> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(res.getString("Name"));
            }

            res.close();
            statement.close();

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

            res.close();
            statement.close();

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public void closeConnection() {
        try {
            connection.commit();
            connection.close();
        } catch (Exception e) {

        }
    }

    private Connection initDB(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + name);
            con.setAutoCommit(false);
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
            con.commit();
            return con;

        } catch ( Exception e ) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al inicar DB");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }
}
