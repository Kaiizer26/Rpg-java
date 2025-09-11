package personnage;

public class Knight extends Ally{
    public Knight(String name, int hp, int defense, int atk, int speed, int luck ){
        super(name, hp, defense, atk, speed, luck);
    }
    public Knight(){
        super();
    }

    public Knight(String name){
        super(name);
    }
    public String toString(){
        return "Nom : " + this.getName() + "\nStat : "+ this.getAllStats() +"\nStart : " + this.isStart();
    }


}
