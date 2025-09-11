package personnage;

import com.googlecode.lanterna.TextColor;
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
        int attack = this.getStat(Stat.ATTAQUE);
        int defense = defender.getStat(Stat.DEFENSE);

        // Calcul des dégâts en pourcentage (remplace l'ancien système attaque - def)
        int damage = (int) Math.round(attack * 100.0 / (100 + defense));

        // Mise à jour des statistiques de combat
        this.addStatsCombat(StatCombat.DAMAGE_DEALT, damage);
        defender.addStatsCombat(StatCombat.DAMAGE_TAKEN, damage);
        this.addStatsCombat(StatCombat.HITS_LANDED, 1);

        // Appliquer les dégâts
        int newHP = defender.getStat(Stat.HP) - damage;
        defender.setStat(Stat.HP, Math.max(newHP, 0));
    }

}
