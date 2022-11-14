package me.potatoeconomy.commands;

import me.potatoeconomy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RefreshMarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Main.market.refreshMarket();
        return true;
    }
}
