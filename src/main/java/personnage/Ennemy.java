package personnage;
import stats.Stat;
import stats.StatCombat;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class Ennemy extends Personnage implements IEnnemy {

    Random random = new Random();
    private String name;
    private boolean start;
    private Map<Stat, Integer> stats;
    private Map<StatCombat, Integer> statsCombat;


    public Ennemy(String name, int hp, int defense, int atk, int speed, int luck ) {
        super(name, hp, defense, atk, speed, luck);
    }

    public Ennemy() {
        this.name = "Bot";
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        this.start = false;
        for (Stat stat : Stat.values()) {
            stats.put(stat, random.nextInt(100)); // pour chaque stat aléatoire de 0 à 99
        }
    }

    public Ennemy(String name) {
        this();
        this.name = name;
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
