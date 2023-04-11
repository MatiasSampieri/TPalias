package com.kector.tpalias.otr;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;


public class SavesManager {

    private final Server server;
    private final Connection connection;
    private static final String DB_NAME = "saves.db";

    public SavesManager(Server server) {
        this.server = server;
        this.connection = initDB();
    }

    public Location loadQuick(Player player) {
        String name = player.getDisplayName();
        try {
            //Statement statement = connection.createStatement();
            //String sql = String.format("SELECT * FROM \"QuickSaves\" WHERE Player = \"%s\"", name);
            //ResultSet res = statement.executeQuery(sql);

            String sqlstr = "SELECT * FROM \"QuickSaves\" WHERE Player = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();

            if (!res.next()) {
                server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al cargar quick save");
                return null;
            }

            double x = res.getDouble("X");
            double y = res.getDouble("Y");
            double z = res.getDouble("Z");
            float yaw = res.getFloat("Yaw");
            float pitch = res.getFloat("Pitch");
            String dim = res.getString("Dim");

            res.close();
            stmt.close();

            return new Location(server.getWorld(dim), x, y, z, yaw, pitch);

        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al cargar quick save");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    public List<ListItem> getTop(){
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT Name, Count, Dim FROM Aliases ORDER BY Count DESC";
            ResultSet res = statement.executeQuery(sql);

            List<ListItem> aliasList = new ArrayList<>();

            while (res.next()) {
                ListItem item = new ListItem(
                        res.getString("Name"),
                        res.getString("Dim"),
                        res.getInt("Count")
                    );
                aliasList.add(item);
            }

            res.close();
            statement.close();

            return aliasList;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveQuick(Player player) {

        String ply = player.getDisplayName();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        String dim = player.getLocation().getWorld().getName();

        try {
            //Statement statement = connection.createStatement();
            //String sql = String.format("INSERT OR REPLACE INTO \"QuickSaves\" (Player, X, Y, Z, Yaw, Pitch, Dim) " +
            //        "VALUES (\"%s\", %f, %f, %f, %f, %f, \"%s\");", name, x, y, z, yaw, pitch, dim);

            String sqlstr = "INSERT OR REPLACE INTO \"QuickSaves\" (Player, X, Y, Z, Yaw, Pitch, Dim) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, ply);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, z);
            stmt.setFloat(5, yaw);
            stmt.setFloat(6, pitch);
            stmt.setString(7, dim);

            stmt.executeUpdate();
            stmt.close();
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }
    }

    public void deleteSave(String name) {
        try {
            //Statement statement = connection.createStatement();

            String sqlstr = "DELETE FROM \"Aliases\" WHERE Name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);

            stmt.setString(1, name);
            stmt.executeUpdate();
            stmt.close();
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
            //Statement statement = connection.createStatement();
            //String sql = String.format("INSERT INTO \"Aliases\" (Name, X, Y, Z, Yaw, Pitch, Dim, Player) " +
            //                           "VALUES (\"%s\", %f, %f, %f, %f, %f, \"%s\", \"%s\");", name, x, y, z, yaw, pitch, dim, player);

            String sqlstr = "INSERT INTO \"Aliases\" (Name, X, Y, Z, Yaw, Pitch, Dim, Player, Count, Fav, Priv) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0)";

            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, name);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, z);
            stmt.setFloat(5, yaw);
            stmt.setFloat(6, pitch);
            stmt.setString(7, dim);
            stmt.setString(8, player);

            stmt.executeUpdate();
            stmt.close();
            connection.commit();
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    public int setFav(String name, boolean fav) {
        try {
            String sqlstr = "UPDATE \"Aliases\" SET Fav = ? WHERE Name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);

            stmt.setString(2, name);
            stmt.setInt(1, (fav ? 1 : 0));
            int rows = stmt.executeUpdate();
            stmt.close();
            connection.commit();

            return rows;
        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return 0;
        }
    }

    public List<ListItem> getFavs() {
        try {
            String sqlstr = "SELECT Name, Dim, Count FROM \"Aliases\" WHERE Fav = 1";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            ResultSet res = stmt.executeQuery();

            List<ListItem> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(new ListItem(
                        res.getString("Name"),
                        res.getString("Dim"),
                        res.getInt("Count")
                ));
            }

            res.close();
            stmt.close();

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public SaveData loadAlias(String name) {
        try {
            //Statement statement = connection.createStatement();
            //String sql = String.format("SELECT * FROM \"Aliases\" WHERE Name = \"%s\"", name);
            //ResultSet res = statement.executeQuery(sql);

            String sqlstr = "SELECT * FROM \"Aliases\" WHERE Name = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();

            if (!res.next()) {
                server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al encontrar alias");
                return null;
            }

            String dim = res.getString("Dim");
            double x = res.getDouble("X");
            double y = res.getDouble("Y");
            double z = res.getDouble("Z");
            float yaw = res.getFloat("Yaw");
            float pitch = res.getFloat("Pitch");
            String player = res.getString("Player");
            int count = res.getInt("Count");

            res.close();
            stmt.close();

            String updtstr = "UPDATE \"Aliases\" SET Count = ? WHERE Name = ?";
            PreparedStatement updatestmt = connection.prepareStatement(updtstr);
            updatestmt.setString(2, name);
            updatestmt.setInt(1, count + 1);
            updatestmt.executeUpdate();
            updatestmt.close();

            Location location = new Location(server.getWorld(dim), x, y, z, yaw, pitch);

            return new SaveData(name, location, server.getPlayer(player));

        } catch (Exception e) {
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR al encontrar alias");
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    public List<ListItem> getList() {
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT Name, Dim, Count FROM Aliases";
            ResultSet res = statement.executeQuery(sql);

            List<ListItem> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(new ListItem(
                    res.getString("Name"),
                    res.getString("Dim"),
                    res.getInt("Count")
                ));
            }

            res.close();
            statement.close();

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<ListItem> getList(String player) {
        try {
            //Statement statement = connection.createStatement();
            //String sql = String.format("SELECT Name FROM \"Aliases\" WHERE Player = \"%s\"", player);
            //ResultSet res = statement.executeQuery(sql);
            String sqlstr = "SELECT Name, Dim, Count FROM \"Aliases\" WHERE Player = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, player);
            ResultSet res = stmt.executeQuery();

            List<ListItem> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(new ListItem(
                    res.getString("Name"),
                    res.getString("Dim"),
                    res.getInt("Count")
                ));
            }

            res.close();
            stmt.close();

            return aliasList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<ListItem> getList(World dim) {
        try {
            //Statement statement = connection.createStatement();
            //String sql = String.format("SELECT Name FROM \"Aliases\" WHERE Dim = \"%s\"", dim.getName());
            //ResultSet res = statement.executeQuery(sql);

            String sqlstr = "SELECT Name, Dim, Count FROM \"Aliases\" WHERE Dim = ?";
            PreparedStatement stmt = connection.prepareStatement(sqlstr);
            stmt.setString(1, dim.getName());
            ResultSet res = stmt.executeQuery();

            List<ListItem> aliasList = new ArrayList<>();

            while (res.next()) {
                aliasList.add(new ListItem(
                    res.getString("Name"),
                    res.getString("Dim"),
                    res.getInt("Count")
                ));
            }

            res.close();
            stmt.close();

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
            server.getConsoleSender().sendMessage(ChatColor.RED + "[TPalias] ERROR: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    private Connection initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);

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
                    "\"Yaw\" REAL," +
                    "\"Pitch\" REAL," +
                    "\"Dim\" TEXT," +
                    "\"Player\" TEXT," +
                    "\"Count\" INTEGER," +
                    "\"Fav\" INTEGER," +
                    "\"Priv\" INTEGER," +
                    "PRIMARY KEY(\"ID\" AUTOINCREMENT));";

            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS \"QuickSaves\" (" +
                    "\"Player\" TEXT NOT NULL UNIQUE," +
                    "\"X\" REAL," +
                    "\"Y\" REAL," +
                    "\"Z\" REAL," +
                    "\"Yaw\" REAL," +
                    "\"Pitch\" REAL," +
                    "\"Dim\" TEXT," +
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

    public List<String> onlyNames(List<ListItem> list) {
        List<String> stringList = new ArrayList<>();
        for (ListItem item : list) {
            stringList.add(item.name);
        }
        return stringList;
    }
}
