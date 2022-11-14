package me.potatoeconomy.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.potatoeconomy.market.MarketGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JSONManager {

    private JsonObject json_contents;
    private HashMap<String, MarketGroup> allGroups;

    private HashMap<String, String> villagerGroupToName;
    private HashSet<String> villagerNameSet;

    public JSONManager(){
        allGroups = new HashMap<>();
        villagerGroupToName = new HashMap<>();
        villagerNameSet = new HashSet<>();
    }

    public boolean getFromFile(Path p){
        try{
            Object o = JsonParser.parseString(new String(Files.readAllBytes(p)));
            json_contents = (JsonObject) o;

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void setAllItems (){
        JsonArray groups = (JsonArray) json_contents.get("all_groups");
        groups.forEach(jsonElement -> {
            JsonObject j = (JsonObject) jsonElement;

            String groupName = j.get("groupName").getAsString();
            String villagerName = j.get("villagerName").getAsString();

            allGroups.put(groupName,
                    new MarketGroup(
                            villagerName,
                            JSONArrayToList(j.getAsJsonArray("items")),
                            j.get("amountShown").getAsInt()
                    ));

            villagerGroupToName.put(groupName, villagerName);
            villagerNameSet.add(villagerName);

        });

    }

    private ArrayList<ItemStack> JSONArrayToList(JsonArray items){
        ArrayList<ItemStack> list = new ArrayList<>();
        try{
            Bukkit.getLogger().info(items.size() + " items");

            items.forEach(i -> {
                JsonObject item = (JsonObject) i;
                Bukkit.getLogger().info("material: " + item.get("material").getAsString());
                Material mat = Material.matchMaterial(item.get("material").getAsString());

                if(mat != null){
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

                        if(stack.getType() == Material.ENCHANTED_BOOK){
                            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)stack.getItemMeta();
                            esm.addStoredEnchant(minecraftEnchantment, enchantmentObj.get("level").getAsInt(), true);
                            stack.setItemMeta(esm);
                        }else{
                            stack.addEnchantment(minecraftEnchantment,enchantmentObj.get("level").getAsInt());
                        }
                    });


                    list.add(stack);
                }else{
                    Bukkit.getLogger().severe(item.get("material").getAsString() + " IS NOT A MATERIAL");
                }


            });
        }catch (Exception e){
            Bukkit.getLogger().info(e.toString());
            return list;
        }

        return list;
    }

    public HashMap<String, MarketGroup> getAllGroups() {
        return allGroups;
    }

    public HashMap<String, String> getVillagerGroupToName() {
        return villagerGroupToName;
    }

    public HashSet<String> getVillagerNameSet() {
        return villagerNameSet;
    }

}
