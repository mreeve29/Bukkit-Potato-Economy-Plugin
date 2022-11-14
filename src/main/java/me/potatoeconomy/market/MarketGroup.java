package me.potatoeconomy.market;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MarketGroup {

    private String villagerName;
    private ArrayList<ItemStack> fullStack;
    private int amountShown;

    public MarketGroup(String villagerName, ArrayList<ItemStack> stack, int amountShown){
        this.villagerName = villagerName;
        this.fullStack = stack;
        this.amountShown = amountShown > stack.size() ? stack.size() : amountShown;
    }

    public ArrayList<ItemStack> getFullStack(){
        return fullStack;
    }

    public ArrayList<ItemStack> getReducedStack(){

        if(amountShown == -1){
            return  fullStack;
        }

        Random r = new Random();
        ArrayList<ItemStack> reducedStack = new ArrayList<>();
        HashSet<Integer> alreadySeen = new HashSet<>();
        int count = 0;
        while (count < amountShown){
            int num = r.nextInt(fullStack.size());
            if(!alreadySeen.contains(num)){
                alreadySeen.add(num);

                reducedStack.add(fullStack.get(num));

                count++;
            }
        }
        return reducedStack;
    }

    public String getVillagerName(){
        return villagerName;
    }
}
