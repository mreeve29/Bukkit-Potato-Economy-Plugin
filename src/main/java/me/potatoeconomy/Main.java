package me.potatoeconomy;

import me.potatoeconomy.commands.MarketPricesCommand;
import me.potatoeconomy.commands.RemoveKingCommand;
import me.potatoeconomy.commands.VillagerSpawnCommand;
import me.potatoeconomy.events.GeneralEvents;
import me.potatoeconomy.events.VillagerListener;

import me.potatoeconomy.market.MarketManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static MarketManager market;

    public static HashMap<UUID, Location> deathMap;

    public static KingManager kingManager;

    public FileConfiguration config;

    public static CSVManager csvManager;

    @Override
    public void onEnable() {
        getLogger().info("Potato Economy starting");

        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        config = getConfig();

        kingManager = new KingManager(config, this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            kingManager.addPlayerToBar(player);
        });

        Path allItemsPath = Path.of(getDataFolder().getPath() + "/all_items.json");

        market = new MarketManager(allItemsPath);

        deathMap = new HashMap<>();

        csvManager = new CSVManager(getDataFolder().getPath() + "/transactions.csv");

        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
        this.getCommand("spawnVillager").setExecutor(new VillagerSpawnCommand());
        this.getCommand("removeKing").setExecutor(new RemoveKingCommand());
//        this.getCommand("marketPrices").setExecutor(new MarketPricesCommand());
        getServer().getPluginManager().registerEvents(new VillagerListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralEvents(this), this);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            market.refreshMarket();
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "---MARKET REFRESH---");
        }, 0, 24000);

        getLogger().info("Potato Economy started successfully!");

    }

    @Override
    public void onDisable() {
        kingManager.removeAllPlayersFromBar();
        kingManager = null;
    }

}
