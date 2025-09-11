package personnage;

import com.sun.source.tree.Tree;
import inventaire.Item;
import stats.Stat;
import stats.StatCombat;

import java.util.*;

public abstract class Personnage implements IPersonnage {
    Random random = new Random();
    private String name;
    private boolean start;
    private Map<Stat, Integer> stats;
    private Map<StatCombat, Integer> statsCombat;

    private int maxHP;

    // NOUVEAU : Système de niveau et expérience
    private int baseStatPoints;

    private int level;
    private int experience;
    private int experienceToNextLevel;

    // SUPPRIMÉ : inventory individuel (maintenant géré globalement)
    // private Map<String, Integer> inventory;

    public Personnage(){
        this.name = "Bot";
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        this.start = false;

        this.level = 1; // Commencer au niveau 1
        this.experience = 0;
        this.experienceToNextLevel = 100;

        // Initialiser avec des stats de base aléatoires
        int totalBaseStats = 0;
        for (Stat stat : Stat.values()) {
            int statValue = random.nextInt(20); // stats de base plus faibles
            stats.put(stat, statValue);
            totalBaseStats += statValue;
        }
        this.baseStatPoints = totalBaseStats;

        // CORRECTION: maxHP doit être défini APRÈS avoir mis les stats dans la map
        this.maxHP = this.stats.get(Stat.HP);
    }

    public Personnage(String name){
        this();
        this.name = name;
    }

    public Personnage(String name, int hp, int defense, int atk, int speed, int luck){
        this.name = name;
        this.stats = new EnumMap<>(Stat.class);
        this.statsCombat = new EnumMap<>(StatCombat.class);
        // Mettre les stats dans la map EN PREMIER
        stats.put(Stat.HP, hp);
        stats.put(Stat.ATTAQUE, atk);
        stats.put(Stat.DEFENSE, defense);
        stats.put(Stat.SPEED, speed);
        stats.put(Stat.LUCK, luck);

        // CORRECTION: maxHP défini APRÈS avoir mis les stats dans la map
        this.maxHP = hp;

        // Initialisation du système de niveau
        this.level = 1; // Commencer au niveau 1
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.baseStatPoints = hp + atk + defense + speed + luck;
    }

    public String toString(){
        return "Nom : " + this.getName() +
                "\nNiveau : " + this.getLevel() +
                " (EXP: " + this.getExperience() + "/" + this.getExperienceToNextLevel() + ")" +
                "\nPoints disponibles : " + this.getAvailablePoints() +
                "\nStats : " + this.getAllStats() +
                "\nStart : " + this.isStart();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStat(Stat stat){
        return stats.getOrDefault(stat, 0);
    }

    public void setStat(Stat stat, int value){
        // Valider que la valeur est entre 0 et 99
        value = Math.max(0, Math.min(99, value));

        // Calculer les points utilisés si on appliquait ce changement
        int currentValue = stats.getOrDefault(stat, 0);
        int pointsDifference = value - currentValue;
        int availablePoints = getAvailablePoints();

        // Vérifier si on a assez de points disponibles
        if (pointsDifference <= availablePoints) {
            stats.put(stat, value);

            // NOUVEAU: Si on modifie les HP, mettre à jour maxHP
            if (stat == Stat.HP) {
                this.maxHP = value;
                // S'assurer que les HP actuels ne dépassent pas les nouveaux maxHP
                // (au cas où on diminuerait les HP max)
                if (getStat(Stat.HP) > maxHP) {
                    stats.put(Stat.HP, maxHP);
                }
            }
        } else {
            // Optionnel : lancer une exception ou afficher un message
            System.out.println("Pas assez de points disponibles! Points disponibles: " + availablePoints);
        }
    }

    public Map<Stat, Integer> getAllStats(){
        return new EnumMap<>(this.stats); // Retourner une copie pour éviter les modifications externes
    }

    int getStatCombat(StatCombat stat){
        return this.statsCombat.getOrDefault(stat, 0);
    }

    void setStatsCombat(StatCombat stat, int value){
        this.statsCombat.put(stat, value);
    }

    public void addStatsCombat(StatCombat stat, int value){
        this.statsCombat.put(stat, this.getStatCombat(stat) + value);
    }

    Map<StatCombat, Integer> getAllStatsCombat(){
        return this.statsCombat;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    // NOUVEAU : Méthodes pour le système de niveau/expérience
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    // Vérifier si le personnage monte de niveau
    private void checkLevelUp() {
        while (experience >= experienceToNextLevel) {
            experience -= experienceToNextLevel;
            level++;
            // Augmenter légèrement l'exp nécessaire pour le prochain niveau
            experienceToNextLevel = (int)(experienceToNextLevel * 1.1);
        }
    }

    // Calculer le coût total des stats actuelles
    public int getTotalStatCost() {
        int total = 0;
        for (Integer statValue : stats.values()) {
            total += statValue;
        }
        return total;
    }

    /**
     * Calcule le nombre de points disponibles pour améliorer les stats
     * Basé sur le niveau actuel du personnage moins les points déjà dépensés
     * @return le nombre de points disponibles (peut être négatif si les stats dépassent le niveau)
     */
    // Méthode pour calculer les points disponibles
    public int getAvailablePoints() {
        // Points totaux = points de base + points bonus par niveau
        int totalAllowedPoints = baseStatPoints + ((level - 1) * 5); // 5 points par niveau après le 1er

        // Points actuellement dépensés
        int spentPoints = getTotalStatCost();

        // Points disponibles
        return totalAllowedPoints - spentPoints;
    }

    // Méthode pour monter de niveau (appelée après victoire en combat)
    public void levelUp() {
        this.level++;
        // Chaque niveau donne 5 points de stats supplémentaires à dépenser
        System.out.println(name + " monte au niveau " + level + "! +5 points de stats disponibles.");
    }

    public int getMaxAvailablePoints() {
        return baseStatPoints + ((level - 1) * 5);
    }

    public String EndCombatMessage(){
        return "Vous avez gagné ! \nNom : " + this.getName() +
                "\nNiveau : " + this.getLevel() +
                "\nStats de Combat : " + this.getAllStatsCombat();
    }

    public int getMaxHP() {
        return maxHP;
    }

    // ===== NOUVELLES MÉTHODES POUR GÉRER LES HP =====

    /**
     * Restaure les HP au maximum
     */
    public void restoreFullHP() {
        this.stats.put(Stat.HP, this.maxHP);
    }

    /**
     * Vérifie si le personnage est mort
     */
    public boolean isDead() {
        return getStat(Stat.HP) <= 0;
    }

    /**
     * Vérifie si le personnage a tous ses HP
     */
    public boolean isFullHealth() {
        return getStat(Stat.HP) >= getMaxHP();
    }

    /**
     * Retourne le nombre de HP manquants
     */
    public int getMissingHP() {
        return getMaxHP() - getStat(Stat.HP);
    }

    /**
     * Soigne le personnage d'un certain montant (sans dépasser maxHP)
     */
    public void healBy(int amount) {
        int currentHP = getStat(Stat.HP);
        int newHP = Math.min(currentHP + amount, getMaxHP());
        this.stats.put(Stat.HP, newHP); // Utilisation directe pour éviter la validation de points
    }

    /**
     * Met à jour maxHP manuellement si besoin (méthode utilitaire)
     */
    public void updateMaxHP() {
        this.maxHP = getStat(Stat.HP);
    }

    /**
     * Inflige des dégâts au personnage (sans descendre en dessous de 0)
     */
    public void takeDamage(int damage) {
        int currentHP = getStat(Stat.HP);
        int newHP = Math.max(currentHP - damage, 0);
        this.stats.put(Stat.HP, newHP); // Utilisation directe pour éviter la validation de points
    }
}