package me.potatoeconomy.commands;

import me.potatoeconomy.market.MarketPricesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketPricesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            MarketPricesGUI.showMarketPricesGUI(p);
        }

        return true;
    }


}
