package me.potatoeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CSVManager {

    private String path;

    public CSVManager(String path){
        this.path = path;

        try {
            File csvFile = new File(path);
            if(csvFile.createNewFile()){
                FileWriter csvWriter = new FileWriter(path);
                csvWriter.write("TIME,UUID,ITEM,ENCHANTMENTS,QUANTITY,COST\n");
                csvWriter.close();
                Bukkit.getLogger().info("Successfully Created transactions.csv");
            }else{
                Bukkit.getLogger().info("transactions.csv already exists!");
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error creating transactions.csv");
            e.printStackTrace();
        }
    }

    //TIME, UUID, ITEM, ENCHANTMENTS, QUANTITY, COST
    public void addTransaction(ItemStack stack, Player player, int cost) throws IOException {
        FileWriter csvFileWriter;
        csvFileWriter = new FileWriter(path, true);

        String enchantments = "";

        if(stack.getType() == Material.ENCHANTED_BOOK){
            Map<Enchantment, Integer> itemEnchantments = ((EnchantmentStorageMeta) stack.getItemMeta()).getStoredEnchants();
            for (Enchantment e : itemEnchantments.keySet()) {
                enchantments += e.toString() + "_" + itemEnchantments.get(e) + " ";
            }
            if(enchantments.length() > 0){
                enchantments = enchantments.substring(0, enchantments.length() - 1);
            }
        }else{
            Map<Enchantment, Integer> itemEnchantments = stack.getItemMeta().getEnchants();
            if(itemEnchantments != null){
                for(Enchantment e : itemEnchantments.keySet()) {
                    enchantments += e.getKey() + "_" + itemEnchantments.get(e) + " ";
                }
                if(enchantments.length() > 0){
                    enchantments = enchantments.substring(0, enchantments.length() - 1);
                }
            }
        }

        csvFileWriter.write(java.time.LocalDateTime.now() + ",");
        csvFileWriter.write(player.getUniqueId() + ",");
        csvFileWriter.write(stack.getType() + ",");
        csvFileWriter.write(enchantments + ",");
        csvFileWriter.write(stack.getAmount() + ",");
        csvFileWriter.write(cost + "\n");

        csvFileWriter.close();
    }

}
