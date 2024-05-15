package me.bysensl.bysslwhitelist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BysslWhiteList extends JavaPlugin implements Listener, TabCompleter {
    public final Logger logger = Logger.getLogger("Minecraft");
    public static BysslWhiteList plugin;
    public static Connection connection;

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Disabled!");

        try {
            if (connection == null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        this.openConnection();

        closeConnection();

    }

    public synchronized void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.getConfig().getString("host") + ":" + this.getConfig().getString("port") + "/" + this.getConfig().getString("database"), this.getConfig().getString("user"), this.getConfig().getString("password"));
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static synchronized void closeConnection() {
        try {
            connection.close();
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    public boolean isWhitelisted(Player player) {
        this.openConnection();

        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `" + this.getConfig().getString("table") + "` WHERE `user`=?;");
            sql.setString(1, player.getName());
            ResultSet rs = sql.executeQuery();
            logger.warning("hello");
            if (rs.next()) {
                PreparedStatement sql1 = connection.prepareStatement("SELECT * FROM `" + this.getConfig().getString("table") + "` WHERE `user`=? AND `included`=1;");
                sql1.setString(1, player.getName());
                ResultSet rs1 = sql.executeQuery();
                logger.warning("1");
                if (rs1.next()) {
                    logger.warning("11");
                    return true;
                } else {
                    logger.warning("10");
                    return false;
                }
            } else {
                PreparedStatement sql1 = connection.prepareStatement("INSERT INTO `" + this.getConfig().getString("table") + "` (`id`, `user`, `dateTime`, `included`) VALUES (NULL, ?, ?, 0);");
                sql1.setString(1, player.getName());
                sql1.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                sql1.execute();
                sql1.close();
                logger.warning("0");
                return false;
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public List<String> getActiveWhitelistedPlayers(CommandSender sender) {
        this.openConnection();

        try {
            PreparedStatement sql = connection.prepareStatement("SELECT `user` FROM `" + this.getConfig().getString("table") + "` WHERE `included`=1;");
            ResultSet rs = sql.executeQuery();

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("user"));
            }
            return list;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public List<String> getDisactiveWhitelistedPlayers(CommandSender sender) {
        this.openConnection();

        try {
            PreparedStatement sql = connection.prepareStatement("SELECT `user` FROM `" + this.getConfig().getString("table") + "` WHERE `included`=0;");
            ResultSet rs = sql.executeQuery();

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("user"));
            }
            return list;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public void addWhitelistOnline(String player, CommandSender sender) {
        this.openConnection();

        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `" + this.getConfig().getString("table") + "` WHERE `user`=?;");
            sql.setString(1, player);
            ResultSet rs = sql.executeQuery();
            if (!rs.next()) {
                PreparedStatement sql1 = connection.prepareStatement("INSERT INTO `" + this.getConfig().getString("table") + "` (`id`, `user`, `dateTime`, `included`) VALUES (NULL, ?, ?, 1);");
                sql1.setString(1, player);
                sql1.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                sql1.execute();
                sql1.close();
            } else {
                PreparedStatement sql1 = connection.prepareStatement("UPDATE `" + this.getConfig().getString("table") + "` SET `included`=1 WHERE `user`=?;");
                sql1.setString(1, player);
                sql1.execute();
                sql1.close();
            }

            rs.close();
            sql.close();
            sender.sendMessage(ChatColor.GREEN + player + " is now whitelisted!");
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            closeConnection();
        }

    }

    public void delWhitelist(String player, CommandSender sender) {
        this.openConnection();

        try {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM `" + this.getConfig().getString("table") + "` WHERE `user`=?;");
            sql.setString(1, player);
            sql.execute();
            sql.close();
            sender.sendMessage(ChatColor.RED + player + " is no longer whitelisted!");
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            closeConnection();
        }

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!commandLabel.equalsIgnoreCase("bwl") && !commandLabel.equalsIgnoreCase("bysslWhitelist")) {
            return false;
        } else {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("bysslWhitelist.add") && !sender.isOp() && !sender.hasPermission("bysslWhitelist.*")) {
                        sender.sendMessage(ChatColor.RED + "I'm sorry but you dont have the right permissions to do this");
                    } else if (args.length == 2) {
                        this.addWhitelistOnline(args[1], sender);
                        sender.sendMessage(sender.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage:" + ChatColor.WHITE + " /bwl add <player>");
                    }
                } else if (args[0].equalsIgnoreCase("del")) {
                    if (!sender.hasPermission("bysslWhitelist.del") && !sender.isOp() && !sender.hasPermission("bysslWhitelist.*")) {
                        sender.sendMessage(ChatColor.RED + "I'm sorry but you dont have the right permissions to do this");
                    } else if (args.length == 2) {
                        this.delWhitelist(args[1], sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage:" + ChatColor.WHITE + " /bwl del <player>");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (!sender.hasPermission("bysslWhitelist.help") && !sender.isOp() && !sender.hasPermission("bysslWhitelist.*")) {
                        sender.sendMessage(ChatColor.RED + "I'm sorry but you dont have the right permissions to do this");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "WhiteList enable: " + "/bwl on"); //help cmd
                        sender.sendMessage(ChatColor.GREEN + "WhiteList disable: " + "/bwl off"); //help cmd
                        sender.sendMessage(ChatColor.GREEN + "Add player to whiteList: " + "/bwl add <player>"); //help cmd
                        sender.sendMessage(ChatColor.GREEN + "Remove player from whiteList: " + "/bwl del <player>"); //help cmd
                        sender.sendMessage(ChatColor.GREEN + "Reload plugin: " + "/bwl reload"); //help cmd
                    }
                } else if (args[0].equalsIgnoreCase("on")) {
                    if (!sender.hasPermission("bysslWhitelist.enable") && !sender.isOp() && !sender.hasPermission("bysslWhitelist.*")) {
                        sender.sendMessage(ChatColor.RED + "I'm sorry but you dont have the right permissions to do this");
                    } else {
                        this.getConfig().set("enabled", true);
                        sender.sendMessage(ChatColor.GREEN + "WhiteList enabled");
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    if (!sender.hasPermission("bysslWhitelist.disable") && !sender.isOp() && !sender.hasPermission("bysslWhitelist.*")) {
                        sender.sendMessage(ChatColor.RED + "I'm sorry but you dont have the right permissions to do this");
                    } else {
                        this.getConfig().set("enabled", false);
                        sender.sendMessage(ChatColor.GREEN + "WhiteList disabled");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage:" + ChatColor.WHITE + " /bwl add/del/on/off [player]");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage:" + ChatColor.WHITE + " /bwl add/del/on/off [player]");
            }

            return true;
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!this.isWhitelisted(player) && this.getConfig().getBoolean("enabled")) {
            event.setKickMessage("You're not on our whitelist");
            event.setResult(Result.KICK_WHITELIST);
        }

    }
    @EventHandler
    public void onPlyerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            return Arrays.asList("add", "del", "on", "off", "reload", "help");
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("del")) {
                return getActiveWhitelistedPlayers(sender);
            } else if (args[0].equalsIgnoreCase("add")) {
                return getDisactiveWhitelistedPlayers(sender);
            } else if (args[0].equalsIgnoreCase("on")) {
                return Arrays.asList("");
            } else if (args[0].equalsIgnoreCase("off")) {
                return Arrays.asList("");
            } else {
                return Arrays.asList("");
            }
        }
        return Arrays.asList("");
    }
}