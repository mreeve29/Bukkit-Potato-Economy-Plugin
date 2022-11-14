package me.potatoeconomy.json;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JSONReader {

    public static List<ItemStack> getListFromFile(String path){
        ArrayList<ItemStack> list = new ArrayList<>();
        try{
            Bukkit.getLogger().info("try");
            Object o = JsonParser.parseString(new String(Files.readAllBytes(Path.of(path))));
            JsonObject json = (JsonObject) o;
            JsonArray weapons = (JsonArray) json.get("weapons");

            Bukkit.getLogger().info(weapons.toString());
            weapons.forEach(i -> {
                JsonObject item = (JsonObject) i;
                Bukkit.getLogger().info("material: " + item.get("material").getAsString());
                ItemStack stack = new ItemStack(
                        Material.matchMaterial(item.get("material").getAsString()),
                        item.get("quantity").getAsInt());


                ItemMeta meta = stack.getItemMeta();
                String name = item.get("name").getAsString();
                if(!name.equals("N/A")) {
                    meta.setDisplayName(name);
                }

                ArrayList<String> lore = new ArrayList<>();
                lore.add("Price: " + item.get("price").getAsString() + " poisonous potatoes");

                String desc = item.get("description").getAsString();
                if(!desc.equals("")) lore.add(desc);

                meta.setLore(lore);

                stack.setItemMeta(meta);

                item.getAsJsonArray("enchantments").forEach(e -> {
                    JsonObject enchantmentObj = (JsonObject) e;
                    Enchantment minecraftEnchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentObj.get("enchantment").getAsString()));
                    stack.addEnchantment(minecraftEnchantment,enchantmentObj.get("level").getAsInt());
                });


                list.add(stack);

            });

        }catch (Exception e){
            Bukkit.getLogger().info(e.toString());
            return list;
        }

        return list;
    }


}
