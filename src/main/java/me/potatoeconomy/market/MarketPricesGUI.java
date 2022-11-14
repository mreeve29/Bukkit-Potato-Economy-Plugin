package me.potatoeconomy.market;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask2D;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;

public class MarketPricesGUI {
    public static void showMarketPricesGUI(Player p){
        Menu.Builder pageTemplate = ChestMenu.builder(3).title("Items").redraw(true);
        List<Menu> pages = PaginatedMenuBuilder.builder(pageTemplate)
                .nextButton(new ItemStack(Material.ARROW))
                .nextButtonEmpty(new ItemStack(Material.ARROW)) // Icon when no next page available
                .nextButtonSlot(23)
                .previousButton(new ItemStack(Material.ARROW))
                .previousButtonEmpty(new ItemStack(Material.ARROW)) // Icon when no previous page available
                .previousButtonSlot(21)
                .addItem(new ItemStack(Material.DIRT))
                .build();
        pages.get(0).open(p);
    }
}
