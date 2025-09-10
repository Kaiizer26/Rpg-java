package personnage;

import com.sun.source.tree.Tree;
import inventaire.Item;
import stats.Stat;
import stats.StatCombat;

import java.util.*;

public abstract class Personnage implements IPersonnage {
    Random random = new Random();
    private String name;
    private boolean start;
    private Map<Stat, Integer> stats;
    private Map<StatCombat, Integer> statsCombat;
    private Map<String, Integer> inventory;
    public Personnage(){
        this.name = "Bot";
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        stats.put(Stat.HP, 50);
        stats.put(Stat.ATTAQUE, 50);
        stats.put(Stat.DEFENSE, 15);
        stats.put(Stat.SPEED, 75);
        stats.put(Stat.LUCK, 5);
        this.start=true;
        this.inventory = new TreeMap<>();
    }
    public Personnage(String name){
        this();
        this.name = name;

    }
    public Personnage(String name, int hp, int defense, int atk, int speed, int luck, boolean start){
        this.name = name;
        this.start=start;
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        stats.put(Stat.HP, hp);
        stats.put(Stat.ATTAQUE, atk);
        stats.put(Stat.DEFENSE, defense);
        stats.put(Stat.SPEED, speed);
        stats.put(Stat.LUCK, luck);
        this.inventory = new TreeMap<>();
    }

    public String toString(){
        return "Nom : " + this.getName() +  "\nInventaire : " + this.getInventory() + "\nStat : " + this.getAllStats() + "\nStart : " + this.isStart();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStat(Stat stat){
        return stats.getOrDefault(stat, 0);
    }

    public void setStat(Stat stat, int value){
        stats.put(stat, value);
    }
     Map<Stat, Integer> getAllStats(){
        return this.stats;
    }

    int getStatCombat(StatCombat stat){
        return this.statsCombat.getOrDefault(stat, 0);
    }

    void setStatsCombat(StatCombat stat, int value){
        this.statsCombat.put(stat, value);
    }

    void addStatsCombat(StatCombat stat, int value){
        this.statsCombat.put(stat, this.getStatCombat(stat)+ value);
    }

    Map<StatCombat, Integer> getAllStatsCombat(){
        return this.statsCombat;
    }
    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Map<String, Integer> getInventory() {
        return this.inventory;
    }

    public void addItem(String name, Integer nb){
        this.inventory.put(name, nb);
    }

    public void removeItem(String name){
        this.inventory.remove(name);
    }
    public boolean dodge(){
        boolean dodgerand = random.nextBoolean();
        return dodgerand;
    }

    public String EndCombatMessage(){
        return "Vous avez gagné ! \nNom : " + this.getName() +  "\nInventaire : " + this.getInventory() + "\nStats de Combat : " + this.getAllStatsCombat();
    }

}
