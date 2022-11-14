package me.potatoeconomy.market;

import me.potatoeconomy.Main;
import me.potatoeconomy.json.JSONManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MarketManager {
    public static HashMap<String, MarketGroup> allMarketGroups;
    public static HashMap<String, ArrayList<ItemStack>> currentItems;

    public static HashMap<String, String> villagerGroupToName;

    public static HashSet<String> villagerNameSet;

    private ItemStack book;

    public MarketManager(Path allItemsPath) {
        allMarketGroups = new HashMap<>();
        currentItems = new HashMap<>();

        JSONManager jsonManager = new JSONManager();
        if(jsonManager.getFromFile(allItemsPath)){
            //success
            jsonManager.setAllItems();
            allMarketGroups = jsonManager.getAllGroups();
            villagerGroupToName = jsonManager.getVillagerGroupToName();
            villagerNameSet = jsonManager.getVillagerNameSet();
        }else{
            Bukkit.getLogger().info("error loading items");
            allMarketGroups = new HashMap<>();
            villagerGroupToName = new HashMap<>();
            villagerNameSet = new HashSet<>();
        }

        refreshMarket();

        book = generateBook();
    }

    public void refreshMarket() {
        allMarketGroups.forEach((k , v) -> {
            MarketGroup mg = allMarketGroups.get(k);
            currentItems.put(mg.getVillagerName(), mg.getReducedStack());
        });
    }

    public ArrayList<ItemStack> get(String villagerName) {
        return currentItems.get(villagerName);
    }


    private ItemStack generateBook(){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bmeta = (BookMeta) book.getItemMeta();

        bmeta.setTitle(ChatColor.GREEN + "POTATO MARKET");
        ArrayList<String> pages = getPages();
        bmeta.setPages(pages);

        book.setItemMeta(bmeta);
        return book;
    }

    private ArrayList<String> getPages() {
        ArrayList<String> pages = new ArrayList<>();

        allMarketGroups.values().forEach(group -> {
            StringBuilder currPageBuilder = new StringBuilder();
            currPageBuilder.append(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + group.getVillagerName() + "\n");
            for(ItemStack i : group.getFullStack()){
                int itemCost = Integer.parseInt(i.getItemMeta().getLore().get(0).split(" ")[1]);
                currPageBuilder.append(ChatColor.GREEN + "$" + itemCost + " ");
                if(i.getType() == Material.ENCHANTED_BOOK){
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta)i.getItemMeta();
                    StringBuilder enchList = new StringBuilder();
                    esm.getStoredEnchants().forEach((ench, val) -> {
                        enchList.append(enchKeyToString(ench) + (val == 1 ? "": " " + val));
                    });
                    currPageBuilder.append(ChatColor.BLUE + enchList.toString() + "\n");
                }else{
                    currPageBuilder.append(ChatColor.BLUE + "" + i.getAmount() + " " + fixString(i.getType().toString()) + "\n");
                }


            }
//            System.out.println(currPageBuilder.toString());
            pages.add(currPageBuilder.toString());
        });
        return pages;
    }

    private String fixString(String str){
        String base = str.replace("_", " ").toLowerCase();
        StringBuilder sb = new StringBuilder();
        for(String s : base.split(" ")){
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            sb.append(chars);
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private String enchKeyToString(Enchantment e){
        String base = e.getKey().toString().replace("minecraft:", "");
        return fixString(base);
    }

    public ItemStack getBook(){
        return book;
    }
}
