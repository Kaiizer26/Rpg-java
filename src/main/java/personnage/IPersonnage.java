package personnage;

import stats.Stat;
import stats.StatCombat;
import java.util.Map;

public interface IPersonnage {

    // === MÉTHODES DE BASE ===
    void performAttack(Personnage defender);

    String getName();
    void setName(String name);
    boolean isStart();
    void setStart(boolean start);

    // === GESTION DES STATS ===
    int getStat(Stat stat);
    void setStat(Stat stat, int value);
    Map<Stat, Integer> getAllStats();

    // === GESTION DES STATS DE COMBAT ===
    void addStatsCombat(StatCombat stat, int value);

    // === SYSTÈME DE NIVEAU ET EXPÉRIENCE ===
    int getLevel();
    void setLevel(int level);
    int getExperience();
    void addExperience(int exp);
    int getExperienceToNextLevel();

    // === SYSTÈME DE POINTS ===
    int getTotalStatCost();
    int getAvailablePoints();
    int getMaxAvailablePoints();

    // === GESTION DES HP ===
    int getMaxHP();
    void restoreFullHP();
    boolean isDead();
    boolean isFullHealth();
    int getMissingHP();
    void healBy(int amount);
    void takeDamage(int damage);
    void updateMaxHP();

    // === MÉTHODES UTILITAIRES ===
    String EndCombatMessage();
}