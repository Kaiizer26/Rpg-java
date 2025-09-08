package Personnage;

import java.util.Random;
public class Chevalier extends Personnage{
    Random random = new Random();
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
        return "Nom : " + this.getName() + "\nHp : " + this.getHp() + "\nDefense : " + this.getDefense() + "\nAttaque : " + this.getAtk() + "\nAgilité : " + this.getSpeed() + "\nChance : " + this.getLuck() + "\nStart" + this.isStart() + "\n";
    }

    @Override
    public void performAttack(Personnage defender) {
        if (defender.dodge()) {
            System.out.println(defender.getName() + " a esquivé !");
        } else {
            int damage = this.getAtk() - defender.getDefense();
            if (damage < 0) damage = 0;
            defender.setHp(defender.getHp() - damage);
            if (defender.getHp() < 0) defender.setHp(0);
            System.out.println(this.getName() + " attaque " + defender.getName() + " et inflige " + damage + " dégâts !");
        }


    }}
