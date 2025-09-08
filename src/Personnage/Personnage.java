package Personnage;

import java.util.Random;

public abstract class Personnage {
    Random random = new Random();
    private String name;
    private int hp;
    private int defense;
    private int atk;
    private int speed;
    private int luck;
    private boolean start;
    public Personnage(){
        this.name = "Bot";
        this.hp = 50;
        this.defense = 15;
        this.atk = 35;
        this.speed = 75;
        this.luck = 5;
        this.start=true;
    }
    public Personnage(String name){
        this.name = name;
        this.hp = 50;
        this.defense = 15;
        this.atk = 35;
        this.speed = 75;
        this.luck = 5;
        this.start=true;

    }
    public Personnage(String name, int hp, int defense, int atk, int speed, int luck, boolean start){
        this.name = name;
        this.hp = hp;
        this.defense = defense;
        this.atk = atk;
        this.speed = speed;
        this.luck = luck;
        this.start=true;
    }



    public String toString(){
        return "Nom : " + name + "\nHp : " + hp + "\nDefense : " + defense + "\nAttaque : " + atk + "\nAgilité : " + speed + "\nChance : " + luck + "\nStart" + start + "\n";
    }

    public abstract void performAttack(Personnage defender);

    public boolean dodge(){
        boolean dodgerand = random.nextBoolean();
        return dodgerand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

}
