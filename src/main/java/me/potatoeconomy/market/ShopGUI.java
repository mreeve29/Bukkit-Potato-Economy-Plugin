package me.potatoeconomy.market;

import me.potatoeconomy.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    public static void displayShopMenu(Player p, ArrayList<ItemStack> itemList, String title){
        Menu wMenu = ChestMenu.builder(itemList.size()/10 + 1)
                .title(title)
                .build();

        int i = 0;
        for(ItemStack item : itemList){
            Slot slot = wMenu.getSlot(i++);
            slot.setItem(item);

            slot.setClickOptions(ClickOptions.builder().allow(ClickType.LEFT).build());

            slot.setClickHandler((player, info) -> {
                if(info.getClickType() == ClickType.LEFT) {
                    PlayerInventory playerInv = player.getInventory();

                    ItemStack stack = info.getClickedSlot().getItem(player);


                    int itemCost = Integer.parseInt(stack.getItemMeta().getLore().get(0).split(" ")[1]);

                    int potatoes = getPotatoes(playerInv);
                    if (itemCost <= potatoes){


                        if(playerInv.firstEmpty() == -1){
                            player.sendMessage("You do not have enough space to purchase " + stack.getAmount() +  "X" + stack.getType());
                            return;
                        }

                        ItemStack playerStack = stack.clone();

                        ItemMeta playerStackMeta = stack.getItemMeta();
                        playerStackMeta.setLore(new ArrayList<>());
                        playerStack.setItemMeta(playerStackMeta);

                        playerInv.addItem(playerStack);
                        playerInv.removeItem(new ItemStack(Material.POISONOUS_POTATO,itemCost));

                        try {
                            Main.csvManager.addTransaction(stack,player, itemCost);
                        } catch (IOException e) {
                            Bukkit.getLogger().severe("Error exporting transaction (" + p.getDisplayName() + " purchased " + stack.getAmount() + " " + stack.getType() + " for " + itemCost + " potatoes");
                        }
                    }else{
                        player.sendMessage("You do not have enough potatoes to purchase " + stack.getAmount() +  "X" + stack.getType());
                    }

                }
            });
        }

        wMenu.open(p);
    }

    private static int getPotatoes(Inventory inv){
        if (inv.contains(Material.POISONOUS_POTATO)){
            int count = 0;
            for (ItemStack stack : inv.getContents()){
                if(stack != null && stack.getType().equals(Material.POISONOUS_POTATO)){
                    count += stack.getAmount();
                }
            }
            return count;
        }else{
            return 0;
        }
    }

}
