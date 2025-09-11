package personnage;

import inventaire.Item;
import stats.Stat;
import stats.StatCombat;

import java.util.Map;

public class Chevalier extends Ally{
    public Chevalier(String name, int hp, int defense, int atk, int speed, int luck, boolean start){
        super(name, hp, defense, atk, speed, luck, start);
    }
    public Chevalier(){
        super();
    }

    public Chevalier(String name){
        super(name);
    }
    public String toString(){
        return "Nom : " + this.getName() + "\nStat : "+ this.getAllStats() +"\nStart : " + this.isStart() + "\nInventaire : " + this.getInventory();
    }


}
