package personnage;

import inventaire.Item;
import stats.Stat;
import stats.StatCombat;

import java.util.Map;

public class Gobelin extends Ennemi{

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
