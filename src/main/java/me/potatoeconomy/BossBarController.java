package me.potatoeconomy;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarController {

    private BossBar bb;
    private Player trackedPlayer;

    private boolean onlineStatus = false;

    public BossBarController(Player p){

        if(p != null){
            bb = Bukkit.createBossBar("King " + p.getDisplayName(), BarColor.BLUE, BarStyle.SEGMENTED_12);
            setBarProgress(p);
            setTrackedPlayer(p);
            show();
        }else{
            bb = Bukkit.createBossBar("No King", BarColor.BLUE, BarStyle.SEGMENTED_12);
            bb.setProgress(1);
            hide();
        }

    }

    public BossBarController(String displayName){
        bb = Bukkit.createBossBar("King: " + displayName + " (offline)", BarColor.BLUE, BarStyle.SEGMENTED_12);
        show();
    }

    public void setBarTitle(String title){
        bb.setTitle(title);
    }

    public void removeAllPlayers(){
        bb.removeAll();
    }

    public void addPlayer(Player p){
        bb.addPlayer(p);
    }

    public void removePlayer(Player p){
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
            bb.setProgress(p.getHealth() / 28.0);
        }

    }

    public void show(){
        bb.setVisible(true);
    }

    public void hide(){
        bb.setVisible(false);
    }

    public void setTrackedPlayer(Player p){
        trackedPlayer = p;
        setBarProgress(p);
    }

    public Player getTrackedPlayer(){
        return trackedPlayer;
    }

    public void setOffline(){
        bb.setTitle(bb.getTitle() + " (offline)");
        onlineStatus = false;
    }

    public void setOnline(){
        bb.setTitle("King " + trackedPlayer.getDisplayName());
        onlineStatus = true;
    }

    public boolean isOnline() {
        return onlineStatus;
    }


}
