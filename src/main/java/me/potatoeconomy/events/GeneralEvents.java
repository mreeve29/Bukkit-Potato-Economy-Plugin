package me.potatoeconomy.events;

import me.potatoeconomy.KingManager;
import me.potatoeconomy.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class GeneralEvents implements Listener {

    private Plugin plugin;

    public GeneralEvents(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Inventory playerInv = e.getEntity().getInventory();
        if (playerInv.contains(Material.POISONOUS_POTATO)){
            int count = 0;
            for (ItemStack stack : playerInv.getContents()){
                if(stack != null && stack.getType().equals(Material.POISONOUS_POTATO)){
                    count += stack.getAmount();
                }
            }

            String potatoMsg = count == 1 ? " poisonous potato" : " poisonous potatoes";

            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + e.getEntity().getDisplayName() + " died with " + count + potatoMsg);
        }

        if(e.getEntity().getKiller() != null && !e.getEntity().getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END)){
            //pvp death, not in end
            Player p = e.getEntity();
            if(p.getBedSpawnLocation() != null){
                Main.deathMap.put(p.getUniqueId(),p.getBedSpawnLocation());
            }
            //x:-111 y:-60 z:-93
            p.setBedSpawnLocation(new Location(Bukkit.getWorld("world"), 1180, 89, 198), true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        if(Main.deathMap.containsKey(e.getPlayer().getUniqueId())){
            e.getPlayer().setBedSpawnLocation(Main.deathMap.get(e.getPlayer().getUniqueId()), true);
            Main.deathMap.remove(e.getPlayer().getUniqueId());
        }
        if(Main.kingManager.isPlayerKing(e.getPlayer())){
            Main.kingManager.setBarProgress(1);
            Main.kingManager.giveSpeed();
        }
    }

    @EventHandler
    public void onPlayerItemPickup(EntityPickupItemEvent e){
        if(e.getItem().getItemStack().getType() == Material.DRAGON_EGG && e.getEntity() instanceof Player p){
            if(Main.kingManager.isPlayerKing(p)) return;

            Main.kingManager.setKingPlayer(p, false);
            Main.kingManager.setOnline();
            Main.kingManager.showBar();

            plugin.getConfig().set("king.UUID", p.getUniqueId().toString());
            plugin.getConfig().set("king.displayName", p.getDisplayName());
            plugin.saveConfig();

            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD +  p.getDisplayName() + " picked up the Ender Dragon Egg!");
        }

    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e){
        if(e.getBlock().getType() != Material.ANCIENT_DEBRIS) return;
        e.setDropItems(false);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e){
        if (e.getEntity() instanceof Player p){
            if(Main.kingManager.isPlayerKing(p)){
                Main.kingManager.setBarProgress((Math.floor(p.getHealth() + e.getAmount())) / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }

        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Villager v){
            if(v.getMaximumAir() == 999999) e.setCancelled(true);
        }
        if(!Main.kingManager.isOnline()) return;
        if (e.getEntity() instanceof Player p){
            if(Main.kingManager.isPlayerKing(p)){
                Main.kingManager.setBarProgress((Math.floor(p.getHealth() - e.getDamage())) / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            }
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Main.kingManager.addPlayerToBar(e.getPlayer());

        if(plugin.getConfig().getString("king.UUID").equals(e.getPlayer().getUniqueId().toString())){
            Main.kingManager.setKingPlayer(e.getPlayer(), true);
        }

        if(Main.kingManager.isPlayerKing(e.getPlayer())){
            Main.kingManager.setOnline();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Main.kingManager.removePlayerFromBar(e.getPlayer());
        if(Main.kingManager.isPlayerKing(e.getPlayer())){
            Main.kingManager.setOffline();
        }
    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent e){
        AnvilInventory inventory = e.getInventory();
        ItemStack first_item = inventory.getItem(0);
        ItemStack second_item = inventory.getItem(1);

        if ((first_item == null) || (second_item == null)) return;

        if (second_item.getType() == Material.ENCHANTED_BOOK) {
            ItemStack result = first_item.clone();
            ItemMeta bookMeta = second_item.getItemMeta();
            assert bookMeta != null;

            if(first_item.getType() == Material.ENCHANTED_BOOK &&
            bookHasMendingOrUnbreaking(first_item) && bookHasMendingOrUnbreaking(second_item)){
                e.setResult(null);
                inventory.setRepairCost(0);
                return;
            }

            if(first_item.getType() != Material.ENCHANTED_BOOK &&
                    bookHasMendingOrUnbreaking(second_item)){
                e.setResult(null);
                inventory.setRepairCost(0);
                return;
            }

            if (first_item.getType() != Material.ENCHANTED_BOOK) {
                for (Enchantment enchantment : ((EnchantmentStorageMeta) bookMeta).getStoredEnchants().keySet()) {
                    int bookLevel = ((EnchantmentStorageMeta) bookMeta).getStoredEnchantLevel(enchantment);
                    int itemLevel = first_item.getEnchantmentLevel(enchantment);
                    if (itemLevel < bookLevel) {
                        result.addUnsafeEnchantment(enchantment, bookLevel);
                    } else {
                        result = null;
                        inventory.setRepairCost(0);
                    }
                }
                e.setResult(result);
                if (inventory.getRepairCost() < 0) {
                    inventory.setRepairCost(5);
                }

            }
        }

    }

    @EventHandler
    public void onEnchantItemEvent( EnchantItemEvent e){
       Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();
       if(enchants.containsKey(Enchantment.MENDING) || enchants.containsKey(Enchantment.DURABILITY)){
           Bukkit.getLogger().info("removing mending and/or unbreaking");
           e.setCancelled(true);
       }
    }

    private boolean bookHasMendingOrUnbreaking(ItemStack s){
        if(s.getType() != Material.ENCHANTED_BOOK){
            return false;
        }

        Map enchants = ((EnchantmentStorageMeta)s.getItemMeta()).getStoredEnchants();
        Bukkit.getLogger().info(enchants.keySet().toString());
        if(enchants.containsKey(Enchantment.MENDING) || enchants.containsKey(Enchantment.DURABILITY)){
            return true;
        }else {
            return false;
        }

    }

//    @EventHandler
//    public void onPlayerEnderChestOpen(InventoryOpenEvent e){
//        if(e.getInventory().getType() == InventoryType.ENDER_CHEST){
//            e.getPlayer().sendMessage("Ender Chests are banned");
//            e.setCancelled(true);
//        }
//    }

}
