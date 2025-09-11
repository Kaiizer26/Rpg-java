package inventaire;

import java.util.*;

/**
 * Classe pour gérer un inventaire global pour le joueur
 * Remplace les inventaires individuels par personnage
 */
public class GlobalInventory {
    private Map<String, Integer> items;

    public GlobalInventory() {
        this.items = new TreeMap<>(); // TreeMap pour garder les items triés
    }

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

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return "Inventaire vide";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Inventaire (").append(getUniqueItemCount()).append(" types d'items, ")
                .append(getTotalItemCount()).append(" au total):\n");

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}