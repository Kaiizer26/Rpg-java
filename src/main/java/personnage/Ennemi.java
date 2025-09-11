package personnage;
import stats.Stat;
import stats.StatCombat;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class Ennemi extends Personnage {

    Random random = new Random();
    private String name;
    private boolean start;
    private Map<Stat, Integer> stats;
    private Map<StatCombat, Integer> statsCombat;


    public Ennemi(String name, int hp, int defense, int atk, int speed, int luck, boolean start) {
        super(name, hp, defense, atk, speed, luck, start);
    }

    public Ennemi() {
        this.name = "Bot";
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        this.start = false;
        for (Stat stat : Stat.values()) {
            stats.put(stat, random.nextInt(100)); // pour chaque stat aléatoire de 0 à 99
        }
    }

    public Ennemi(String name) {
        this();
        this.name = name;
    }

    public String toString() {
        return "Nom : " + this.getName() + "\nStats : " + this.getAllStats() + "\nStart" + this.isStart() + "\n";
    }

    @Override
    public void performAttack(Personnage defender) {
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
