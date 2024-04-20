package com.thefishnextdoor.custommobs.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thefishnextdoor.custommobs.Debug;
import com.thefishnextdoor.custommobs.FishsCustomMobs;
import com.thefishnextdoor.custommobs.configuration.ItemConfiguration;
import com.thefishnextdoor.custommobs.configuration.MobConfiguration;
import com.thefishnextdoor.custommobs.util.CommandTools;

import net.md_5.bungee.api.ChatColor;

public class FCM implements CommandExecutor, TabCompleter {

    private static final String RELOAD_PERMISSION = "fcm.reload";
    private static final String DEBUG_PERMISSION = "fcm.debug";
    private static final String SUMMON_PERMISSION = "fcm.summon";
    private static final String GIVE_PERMISSION = "fcm.give";

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> subcommands = new ArrayList<>();
            subcommands.add("help");
            if (sender.hasPermission(RELOAD_PERMISSION)) {
                subcommands.add("reload");
            }
            if (sender.hasPermission(DEBUG_PERMISSION)) {
                subcommands.add("debug");
            }
            if (sender.hasPermission(SUMMON_PERMISSION)) {
                subcommands.add("summon");
            }
            if (sender.hasPermission(GIVE_PERMISSION)) {
                subcommands.add("give");
            }
            return subcommands;
        }

        // Summon //
        if (args[0].equalsIgnoreCase("summon") && sender.hasPermission(SUMMON_PERMISSION)) {
            if (args.length == 2) {
                return MobConfiguration.getIds();
            }
            return null;
        }

        // Give //
        if (args[0].equalsIgnoreCase("give") && sender.hasPermission(GIVE_PERMISSION)) {
            if (args.length == 2) {
                return CommandTools.getPlayerNames();
            }
            else if (args.length == 3) {
                return ItemConfiguration.getIds();
            }
            return null;
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Help //
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Fish's Custom Mobs");
            if (sender.hasPermission(RELOAD_PERMISSION)) {
                sender.sendMessage(ChatColor.GREEN + "/fcm reload " + ChatColor.WHITE + "Reload the plugin");
            }
            if (sender.hasPermission(SUMMON_PERMISSION)) {
                sender.sendMessage(ChatColor.GREEN + "/fcm summon <mob> " + ChatColor.WHITE + "Summon a custom mob");
            }
            if (sender.hasPermission(GIVE_PERMISSION)) {
                sender.sendMessage(ChatColor.GREEN + "/fcm give <player> <item> [amount] " + ChatColor.WHITE + "Give a custom item to a player");
            }
            return true;
        }

        // Reload //
        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission(RELOAD_PERMISSION)) {
            FishsCustomMobs.loadConfigs();
            sender.sendMessage(ChatColor.GREEN + "Plugin reloaded");
            return true;
        }

        // Debug //
        if (args[0].equalsIgnoreCase("debug") && sender.hasPermission(DEBUG_PERMISSION)) {
            Debug.debug = !Debug.debug;
            sender.sendMessage(ChatColor.DARK_GREEN + "Console debugging " + (Debug.debug ? "enabled" : "disabled"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This subcommand can only be run by a player");
            return true;
        }
        Player player = (Player) sender;

        // Summon //
        if (args[0].equalsIgnoreCase("summon") && player.hasPermission(SUMMON_PERMISSION)) {
            if (args.length == 1) {
                sender.sendMessage("/fcm summon <mob>");
                return true;
            }

            MobConfiguration mobConfiguration = MobConfiguration.get(args[1]);
            if (mobConfiguration == null) {
                sender.sendMessage(ChatColor.RED + "Invalid mob");
                return true;
            }

            mobConfiguration.spawn(player.getLocation());
            player.sendMessage("Summoned " + mobConfiguration.getId());
            return true;
        }

        // Give //
        if (args[0].equalsIgnoreCase("give") && player.hasPermission(GIVE_PERMISSION)) {
            if (args.length == 1) {
                sender.sendMessage("/fcm give <player> <item> [amount]");
                return true;
            }

            Player target = player.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player");
                return true;
            }

            if (args.length == 2) {
                sender.sendMessage("/fcm give " + args[1] + " <item> [amount]");
                return true;
            }

            ItemConfiguration itemConfiguration = ItemConfiguration.get(args[2]);
            if (itemConfiguration == null) {
                sender.sendMessage(ChatColor.RED + "Invalid item");
                return true;
            }

            int amount = 1;
            if (args.length >= 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                    amount = Math.max(amount, 1);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount");
                    return true;
                }
            }

            target.getInventory().addItem(itemConfiguration.create(amount));
            player.sendMessage("Gave " + target.getName() + " " + amount + " " + itemConfiguration.getId());
            return true;
        }

        return false;
    }   
}
