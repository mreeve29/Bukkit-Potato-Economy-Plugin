package me.potatoeconomy;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class KingManager {

    public static final double MAX_KING_HEALTH = 28.0D;

    private Player king;

    private BossBar bb;

    private Plugin plugin;

    private boolean onlineStatus = false;

    public KingManager(FileConfiguration config, Plugin plugin){
        this.plugin = plugin;
        String kingUUID = config.getString("king.UUID");
        String kingDisplayName = config.getString("king.displayName");

        if(!kingUUID.isEmpty() && !kingDisplayName.isEmpty()){
            Player kingPlayer = Bukkit.getPlayer(UUID.fromString(kingUUID));
            if(kingPlayer == null){
                bb = Bukkit.createBossBar("King: " + kingDisplayName + " (offline)", BarColor.BLUE, BarStyle.SEGMENTED_12);
                showBar();
            }else{
                bb = Bukkit.createBossBar("King " + kingPlayer.getDisplayName(), BarColor.BLUE, BarStyle.SEGMENTED_12);
                setKingPlayer(kingPlayer, false);
                setBarProgress(kingPlayer);
                showBar();
            }
        }else{
            bb = Bukkit.createBossBar("No King", BarColor.BLUE, BarStyle.SEGMENTED_12);
            bb.setProgress(1);
            hideBar();
        }

    }

    public void removeAllPlayersFromBar(){
        bb.removeAll();
    }

    public void addPlayerToBar(Player p){
        bb.addPlayer(p);
    }

    public void removePlayerFromBar(Player p){
        bb.removePlayer(p);
    }

    public void setBarProgress(double d){
        if(d <= 0){
            bb.setProgress(0);
        }else{
            bb.setProgress(d);
        }
    }

    public void setBarProgress(Player p){
        if(p.getHealth() <= 0) {
            bb.setProgress(0);
        }else{
            bb.setProgress(p.getHealth() / p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }

    }

    public void showBar(){
        bb.setVisible(true);
    }

    public void hideBar(){
        bb.setVisible(false);
    }

    public void setKingPlayer(Player p, boolean fromJoin){
        setKingHealth(king, p);
        king = p;

        if(!fromJoin) {
            king.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, false, false));
        }
        giveSpeed();

        setBarProgress(p);
    }

    public Player getKingPlayer(){
        return king;
    }

    public void setOffline(){
        bb.setTitle(bb.getTitle() + " (offline)");
        onlineStatus = false;
    }

    public void setOnline(){
        bb.setTitle("King " + king.getDisplayName());
        onlineStatus = true;
    }

    public boolean isOnline() {
        return onlineStatus;
    }


    private void setKingHealth(Player oldPlayer, Player newPlayer){
        if(oldPlayer != null){
            oldPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20D);
            oldPlayer.removePotionEffect(PotionEffectType.SPEED);
            if(oldPlayer.getHealth() > 20.0){
                oldPlayer.setHealth(20.0);
            }
        }
        newPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(MAX_KING_HEALTH);
    }

    public void removeKing(){
        plugin.getConfig().set("king.UUID", "");
        plugin.getConfig().set("king.displayName", "");
        plugin.saveConfig();

        bb = Bukkit.createBossBar("No King", BarColor.BLUE, BarStyle.SEGMENTED_12);
        bb.setProgress(1);
        removeAllPlayersFromBar();
        hideBar();

        if(king != null){
            king.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20D);
            king.removePotionEffect(PotionEffectType.SPEED);

            if(king.getHealth() > 20.0D)king.setHealth(20.0D);
        }

        king = null;
    }

    public boolean isPlayerKing(Player p){
        if(king == null) return false;
        return king.equals(p);
    }

    public void giveSpeed() {
        if(king != null){
            king.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        }
    }
}
