package com.md_5.autogroup;

import com.md_5.autogroup.events.PlayerListener;
import com.md_5.autogroup.events.WorldListener;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoGroup extends JavaPlugin {

    public static final Logger logger = Bukkit.getServer().getLogger();
    public static HashMap<String, Map> playerTimes = new HashMap<String, Map>();
    public static HashMap<String, Don> playerDonations = new HashMap<String, Don>();
    static FileConfiguration config;

    public void onEnable() {
//herp
        PluginManager pm = this.getServer().getPluginManager();

        WorldListener worldListener = new WorldListener(this);
        worldListener.registerEvents(pm);

        PlayerListener playerListener = new PlayerListener(this);
        playerListener.registerEvents(pm);

        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        Config.debug = config.getBoolean("debug", Config.debug);
        Config.interval = config.getInt("interval", Config.interval);
        Config.loyalty = config.getInt("loyaltyTime", Config.loyalty);
        Config.loyalGroup = config.getString("loyaltyGroup", Config.loyalGroup);
        Config.url = config.getString("mysqlURL", Config.url);
        Config.dbName = config.getString("dbName", Config.dbName);
        Config.userName = config.getString("username", Config.userName);
        Config.password = config.getString("password", Config.password);
        Config.table = config.getString("table", Config.table);
        Config.table2 = config.getString("table2", Config.table2);
        Config.don1Group = config.getString("don1Group", Config.don1Group);
        Config.don2Group = config.getString("don2Group", Config.don2Group);
        Config.don3Group = config.getString("don3Group", Config.don3Group);
        Config.don1Amount = config.getInt("don1Amount", Config.don1Amount);
        Config.don2Amount = config.getInt("don2Amount", Config.don2Amount);
        Config.don3Amount = config.getInt("don3Amount", Config.don3Amount);
        Config.command = config.getString("command", Config.command);
        Config.addictGroup1 = config.getString("addict1Group", Config.addictGroup1);
        Config.addictGroup2 = config.getString("addict2Group", Config.addictGroup2);
        Config.addictGroup3 = config.getString("addict3Group", Config.addictGroup3);
        Config.addict1Time = config.getInt("addict1Amount", Config.addict1Time);
        Config.addict2Time = config.getInt("addict2Amount", Config.addict2Time);
        Config.addict3Time = config.getInt("addict3Amount", Config.addict3Time);

        Database.init();

        if (Config.loyalty != 0 || Config.addict1Time != 0 || Config.addict2Time != 0 || Config.addict3Time != 0) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Ticker(), Config.interval * 20, Config.interval * 20);
        }
        logger.info(String.format("AutoGroup v%1$s by md_5 enabled", this.getDescription().getVersion()));
    }

    public void onDisable() {

        Database.save();

        getServer().getScheduler().cancelTasks(this);

        logger.info(String.format("Autogroup v%1$s by md_5 disabled", this.getDescription().getVersion()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return onPlayerCommand((Player) sender, command, label, args);
        } else {
            return onConsoleCommand(sender, command, label, args);
        }
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("playtime")) {
            switch (args.length) {
                case 0:
                    if (!playerTimes.containsKey(player.getName())) {
                        player.sendMessage("That player does not exist!");
                        return true;
                    }
                    player.sendMessage(ChatColor.GOLD + "You joined the server "
                            + (playerTimes.get(player.getName()).getLast() - playerTimes.get(player.getName()).getDate()) + " seconds ago");
                    player.sendMessage(ChatColor.GOLD + "You must reach " + Config.loyalty + " seconds before you are loyal");
                    player.sendMessage(ChatColor.GOLD + "You have played for "
                            + playerTimes.get(player.getName()).getTime() + " seconds in total");
                    break;
                case 1:
                    if (player.hasPermission("autogroup.playtime.others")) {
                        if (!playerTimes.containsKey(args[0])) {
                            player.sendMessage("That player does not exist!");
                            return true;
                        }
                        player.sendMessage(ChatColor.GOLD + args[0] + " joined the server "
                                + (playerTimes.get(args[0]).getLast() - playerTimes.get(args[0]).getDate()) + " seconds ago");
                        player.sendMessage(ChatColor.GOLD + args[0] + " must reach " + Config.loyalty + " seconds before they are loyal");
                        player.sendMessage(ChatColor.GOLD + args[0] + " has played for "
                                + playerTimes.get(args[0]).getTime() + " seconds in total");
                    } else {
                        player.sendMessage("You can only view your own time. Run this command without arguments");
                    }
                    break;
            }
            return true;
        }
        MYSQL.init();
        String name = player.getName();
        if (args.length == 0) {
            MYSQL.loadAmount(name);
            player.sendMessage(ChatColor.GREEN + "Your donations total $" + playerDonations.get(name).amount);
            playerDonations.remove(name);
            return true;
        }
        MYSQL.load(args[0], name);
        if (playerDonations.get(name).payment_amount == 0) {
            player.sendMessage(ChatColor.RED + "Are you trying to scam us? Invalid transaction id!");
            playerDonations.remove(name);
            return true;
        }
        player.sendMessage(ChatColor.RED + "The following information has been found about your donation:");
        player.sendMessage(ChatColor.GREEN + "First Name: " + playerDonations.get(name).first_name);
        player.sendMessage(ChatColor.GREEN + "Last Name: " + playerDonations.get(name).last_name);
        player.sendMessage(ChatColor.GREEN + "Email Address: " + playerDonations.get(name).payer_email);
        player.sendMessage(ChatColor.GREEN + "Amount: " + playerDonations.get(name).payment_amount);
        player.sendMessage(ChatColor.GREEN + "Claimed: " + playerDonations.get(name).claimed);
        player.sendMessage(ChatColor.GREEN + "Total: " + (playerDonations.get(name).amount + playerDonations.get(name).payment_amount));
        if (playerDonations.get(name).claimed) {
            player.sendMessage(ChatColor.RED + "That donation has been processed and your total will remain at $" + playerDonations.get(name).amount);
            playerDonations.remove(name);
            return true;
        }
        boolean add = false;
        if (playerDonations.get(name).name == null) {
            add = true;
        }
        playerDonations.get(name).name = name;
        playerDonations.get(name).amount += playerDonations.get(name).payment_amount;
        playerDonations.get(name).claimed = true;
        if (add) {
            MYSQL.add(name);
        }
        MYSQL.update(name, args[0]);
        if (playerDonations.get(name).amount <= Config.don1Amount) {
            player.sendMessage("That donation is big enough for " + Config.don1Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don1Group);
        } else if (playerDonations.get(name).amount <= Config.don2Amount) {
            player.sendMessage("That donation is big enough for " + Config.don2Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don2Group);
        } else if (playerDonations.get(name).amount >= Config.don3Amount) {
            player.sendMessage("That donation is big enough for " + Config.don3Group + " status. You have now been promoted to that rank!");
            Promote.promotePlayer(name, Config.don3Group);
        } else {
            player.sendMessage("That donation is not big enough to increase your rank :(");
        }
        playerDonations.remove(name);
        return true;
    }

    public boolean onConsoleCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(String.format("AutoGroup v%1$s by md_5", this.getDescription().getVersion()));
        sender.sendMessage("AutoGroup: No other console functionality is available at this time");
        return true;
    }
}
