package inventaire;

import java.util.*;

/**
 * Classe pour gérer un inventaire global pour le joueur
 * Remplace les inventaires individuels par personnage
 * Inclut maintenant la gestion de l'or
 */
public class GlobalInventory {
    private Map<String, Integer> items;
    private int gold; // Nouvelle propriété pour l'or

    public GlobalInventory() {
        this.items = new TreeMap<>(); // TreeMap pour garder les items triés
        this.gold = 0; // Commencer sans or
    }

    // ========== MÉTHODES POUR LES ITEMS ==========

    /**
     * Ajouter des items à l'inventaire
     */
    public void addItem(String itemName, int quantity) {
        if (quantity <= 0) return;
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    /**
     * Retirer des items de l'inventaire
     */
    public boolean removeItem(String itemName, int quantity) {
        if (quantity <= 0) return false;

        int currentQuantity = items.getOrDefault(itemName, 0);
        if (currentQuantity < quantity) {
            return false; // Pas assez d'items
        }

        int newQuantity = currentQuantity - quantity;
        if (newQuantity == 0) {
            items.remove(itemName);
        } else {
            items.put(itemName, newQuantity);
        }
        return true;
    }

    /**
     * Obtenir la quantité d'un item
     */
    public int getItemQuantity(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    /**
     * Vérifier si l'inventaire contient un item
     */
    public boolean hasItem(String itemName) {
        return items.containsKey(itemName) && items.get(itemName) > 0;
    }

    /**
     * Obtenir tous les items (copie pour éviter les modifications externes)
     */
    public Map<String, Integer> getAllItems() {
        return new TreeMap<>(items);
    }

    /**
     * Vider l'inventaire
     */
    public void clear() {
        items.clear();
        gold = 0; // Réinitialiser l'or aussi
    }

    /**
     * Obtenir le nombre total d'items différents
     */
    public int getUniqueItemCount() {
        return items.size();
    }

    /**
     * Obtenir le nombre total d'items
     */
    public int getTotalItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Vérifier si l'inventaire est vide
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // ========== MÉTHODES POUR L'OR ==========

    /**
     * Obtenir la quantité d'or actuelle
     */
    public int getGold() {
        return gold;
    }

    /**
     * Ajouter de l'or
     */
    public void addGold(int amount) {
        if (amount > 0) {
            gold += amount;
        }
    }

    /**
     * Dépenser de l'or
     * @param amount Montant à dépenser
     * @return true si la transaction a réussi, false si pas assez d'or
     */
    public boolean spendGold(int amount) {
        if (amount <= 0) return false;

        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false; // Pas assez d'or
    }

    /**
     * Vérifier si le joueur a assez d'or
     */
    public boolean hasEnoughGold(int amount) {
        return gold >= amount;
    }

    /**
     * Définir la quantité d'or (utile pour les tests ou la triche)
     */
    public void setGold(int amount) {
        gold = Math.max(0, amount); // Ne peut pas être négatif
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTAIRE GLOBAL ===\n");
        sb.append("Or: ").append(gold).append(" pièces\n");

        if (items.isEmpty()) {
            sb.append("Inventaire d'objets vide\n");
        } else {
            sb.append("Objets (").append(getUniqueItemCount()).append(" types, ")
                    .append(getTotalItemCount()).append(" au total):\n");

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return sb.toString();
    }
}