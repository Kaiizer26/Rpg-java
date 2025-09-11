package personnage;

import inventaire.Item;
import stats.Stat;
import stats.StatCombat;

import java.util.Map;

public class Ally extends Personnage {

    public Ally(String name, int hp, int defense, int atk, int speed, int luck, boolean start) {
        super(name, hp, defense, atk, speed, luck, start);
    }

    public Ally() {
        super();
    }

    public Ally(String name) {
        super(name);
    }

    public String toString() {
        return "Nom : " + this.getName() + "\nStats : " + this.getAllStats() + "\nStart" + this.isStart() + "\n";
    }

    @Override
    public void performAttack(Personnage defender) {
        if (defender.dodge()) {
            System.out.println(defender.getName() + " a esquivé !");
            defender.addStatsCombat(StatCombat.HITS_DODGED, 1);
            return;
        }
        int damage = this.getStat(Stat.ATTAQUE) - defender.getStat(Stat.DEFENSE);
        if (damage < 0){
            damage = 0;
            //Pour mettre la stat de coup réussi à 0 si pas de dégat infligé
            this.addStatsCombat(StatCombat.HITS_LANDED, -1);
        }
        this.addStatsCombat(StatCombat.DAMAGE_DEALT, damage);
        this.addStatsCombat(StatCombat.DAMAGE_TAKEN, damage);
        this.addStatsCombat(StatCombat.HITS_LANDED, 1);
        defender.setStat(Stat.HP,defender.getStat(Stat.HP) - damage);

        if (defender.getStat(Stat.HP) < 0){
            defender.setStat(Stat.HP, 0);
        }
        System.out.println(this.getName() + " attaque " + defender.getName() + " et inflige " + damage + " dégâts !");
    }
}
