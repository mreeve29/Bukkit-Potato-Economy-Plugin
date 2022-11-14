package me.potatoeconomy.commands;

import me.potatoeconomy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.HashMap;

public class VillagerSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            if(strings.length != 1){
                p.sendMessage("Need to provide a type of villager");
                return true;
            }
            String name = "";

            HashMap<String, String> villagerList = Main.market.villagerGroupToName;
            if(villagerList.containsKey(strings[0])){
                name = villagerList.get(strings[0]);
            }else{
                p.sendMessage("Need to provide a valid type of villager");
                return true;
            }

            Villager v = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
            v.setMaximumAir(999999);
            v.setCustomName(name);

            return true;
        }
        return true;
    }
}
