package me.potatoeconomy.events;

import me.potatoeconomy.Main;
import me.potatoeconomy.market.ShopGUI;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashSet;

public class VillagerListener implements Listener {

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent e){
        if (e.getRightClicked() instanceof Villager v){
            if(v.getMaximumAir() != 999999) {
                e.setCancelled(true);
                return;
            }

            HashSet<String> villagerList = Main.market.villagerNameSet;

            if(villagerList.contains(v.getCustomName())) {
                ShopGUI.displayShopMenu(e.getPlayer(), Main.market.get(v.getCustomName()), v.getCustomName() + "'s Shop");
            }

        }
    }


    @EventHandler
    public void onVillagerChangeProfession(VillagerCareerChangeEvent e){
        if(e.getEntity().canBreed()){
            e.setCancelled(true);
        }
    }
}
