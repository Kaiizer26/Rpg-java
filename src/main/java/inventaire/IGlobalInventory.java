package inventaire;

import java.util.Map;

/**
 * Interface pour la gestion d'un inventaire global
 * Définit les opérations de base pour gérer items et or
 */
public interface IGlobalInventory {

    // ========== GESTION DES ITEMS ==========

    /**
     * Ajouter des items à l'inventaire
     */
    void addItem(String itemName, int quantity);

    /**
     * Retirer des items de l'inventaire
     * @return true si l'opération a réussi, false sinon
     */
    boolean removeItem(String itemName, int quantity);

    /**
     * Obtenir la quantité d'un item
     */
    int getItemQuantity(String itemName);

    /**
     * Vérifier si l'inventaire contient un item
     */
    boolean hasItem(String itemName);

    /**
     * Obtenir tous les items
     */
    Map<String, Integer> getAllItems();

    /**
     * Vider l'inventaire
     */
    void clear();

    /**
     * Obtenir le nombre total d'items différents
     */
    int getUniqueItemCount();

    /**
     * Obtenir le nombre total d'items
     */
    int getTotalItemCount();

    /**
     * Vérifier si l'inventaire est vide
     */
    boolean isEmpty();

    // ========== GESTION DE L'OR ==========

    /**
     * Obtenir la quantité d'or actuelle
     */
    int getGold();

    /**
     * Ajouter de l'or
     */
    void addGold(int amount);

    /**
     * Dépenser de l'or
     * @return true si la transaction a réussi, false sinon
     */
    boolean spendGold(int amount);

    /**
     * Vérifier si le joueur a assez d'or
     */
    boolean hasEnoughGold(int amount);

    /**
     * Définir la quantité d'or
     */
    void setGold(int amount);
}