package personnage;

public class Gobelin extends Ennemy {

    public Gobelin(){
        super();
    }

    public Gobelin(String name){
        super(name);
    }

    public Gobelin(String name, int hp, int defense, int atk, int speed, int luck ) {
        super(name, hp, defense, atk, speed, luck);
    }


}
