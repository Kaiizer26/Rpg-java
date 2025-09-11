package shop;

import personnage.*;
import inventaire.GlobalInventory;
import team.TeamManager;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;
import stats.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de la boutique du jeu
 * Permet d'acheter des alliés et des objets avec de l'or
 */
public class ShopManager {
    private TerminalUI ui;
    private TeamManager teamManager;
    private GlobalInventory globalInventory;

    // Prix et objets disponibles
    private static final int ALLY_BASE_PRICE = 100;
    private static final String[][] SHOP_ITEMS = {
            {"Potion de soin", "50", "Restaure 30 HP"},
            {"Potion de mana", "40", "Restaure 20 MP"},
            {"Épée en fer", "150", "Augmente l'attaque de 10"},
            {"Bouclier en fer", "120", "Augmente la défense de 8"},
            {"Amulette de chance", "200", "Augmente la chance de 15"}
    };

    public ShopManager(TerminalUI ui, TeamManager teamManager, GlobalInventory globalInventory) {
        this.ui = ui;
        this.teamManager = teamManager;
        this.globalInventory = globalInventory;
    }

    /**
     * Affiche le menu principal de la boutique
     */
    public void showShopMenu() throws IOException, InterruptedException {
        boolean continueShop = true;

        while (continueShop) {
            ui.clearScreen();
            ui.printColoredLine("🏪 BOUTIQUE", TextColor.ANSI.YELLOW);
            ui.printLine("");

            // Afficher l'or du joueur
            ui.printColoredText("💰 Or disponible: ", TextColor.ANSI.YELLOW);
            ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);
            ui.printLine("");

            String[] shopOptions = {
                    "⚔️ Recruter des alliés",
                    "🎒 Acheter des objets",
                    "📊 Voir mes statistiques",
                    "🔙 Retour"
            };

            int choice = ui.showMenu(shopOptions, "Que souhaitez-vous faire ?");

            switch (choice) {
                case 1:
                    showAllyShop();
                    break;
                case 2:
                    showItemShop();
                    break;
                case 3:
                    showPlayerStats();
                    break;
                case 4:
                    continueShop = false;
                    break;
            }
        }
    }

    /**
     * Affiche la boutique d'alliés
     */
    private void showAllyShop() throws IOException, InterruptedException {
        ui.clearScreen();
        ui.printColoredLine("⚔️ RECRUTEMENT D'ALLIÉS", TextColor.ANSI.CYAN);
        ui.printLine("");

        ui.printColoredText("💰 Or disponible: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);
        ui.printLine("");

        // Créer des alliés disponibles avec des stats variables
        Ally[] availableAllies = createAvailableAllies();

        // Afficher les alliés disponibles
        ui.printColoredLine("Alliés disponibles:", TextColor.ANSI.GREEN);
        ui.printLine("");

        for (int i = 0; i < availableAllies.length; i++) {
            Ally ally = availableAllies[i];
            int price = calculateAllyPrice(ally);

            ui.printColoredText((i + 1) + ". ", TextColor.ANSI.WHITE);
            ui.printColoredText(ally.getName(), TextColor.ANSI.YELLOW);
            ui.printColoredText(" (Niveau " + ally.getLevel() + ")", TextColor.ANSI.CYAN);
            ui.printColoredText(" - " + price + " or", TextColor.ANSI.GREEN);
            ui.printLine("");

            // Afficher les statistiques
            ui.printColoredText("   HP: " + ally.getStat(Stat.HP), TextColor.ANSI.RED);
            ui.printColoredText(" | ATT: " + ally.getStat(Stat.ATTAQUE), TextColor.ANSI.RED);
            ui.printColoredText(" | DEF: " + ally.getStat(Stat.DEFENSE), TextColor.ANSI.BLUE);
            ui.printColoredText(" | SPD: " + ally.getStat(Stat.SPEED), TextColor.ANSI.GREEN);
            ui.printColoredText(" | LCK: " + ally.getStat(Stat.LUCK), TextColor.ANSI.MAGENTA);
            ui.printLine("");
            ui.printLine("");
        }

        ui.printColoredLine((availableAllies.length + 1) + ". Retour", TextColor.ANSI.WHITE);
        ui.printLine("");

        ui.printColoredText("Choisissez un allié à recruter (1-" + (availableAllies.length + 1) + "): ", TextColor.ANSI.YELLOW);
        String input = ui.readLine();

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= availableAllies.length) {
                buyAlly(availableAllies[choice - 1]);
            } else if (choice == availableAllies.length + 1) {
                return; // Retour
            } else {
                ui.printColoredLine("Choix invalide !", TextColor.ANSI.RED);
                ui.waitForKeyPress();
            }
        } catch (NumberFormatException e) {
            ui.printColoredLine("Veuillez entrer un nombre valide !", TextColor.ANSI.RED);
            ui.waitForKeyPress();
        }
    }

    /**
     * Affiche la boutique d'objets
     */
    private void showItemShop() throws IOException, InterruptedException {
        ui.clearScreen();
        ui.printColoredLine("🎒 BOUTIQUE D'OBJETS", TextColor.ANSI.CYAN);
        ui.printLine("");

        ui.printColoredText("💰 Or disponible: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);
        ui.printLine("");

        ui.printColoredLine("Objets disponibles:", TextColor.ANSI.GREEN);
        ui.printLine("");

        for (int i = 0; i < SHOP_ITEMS.length; i++) {
            String[] item = SHOP_ITEMS[i];
            ui.printColoredText((i + 1) + ". ", TextColor.ANSI.WHITE);
            ui.printColoredText(item[0], TextColor.ANSI.YELLOW);
            ui.printColoredText(" - " + item[1] + " or", TextColor.ANSI.GREEN);
            ui.printLine("");
            ui.printColoredText("   " + item[2], TextColor.ANSI.CYAN);
            ui.printLine("");
            ui.printLine("");
        }

        ui.printColoredLine((SHOP_ITEMS.length + 1) + ". Retour", TextColor.ANSI.WHITE);
        ui.printLine("");

        ui.printColoredText("Choisissez un objet à acheter (1-" + (SHOP_ITEMS.length + 1) + "): ", TextColor.ANSI.YELLOW);
        String input = ui.readLine();

        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= SHOP_ITEMS.length) {
                buyItem(SHOP_ITEMS[choice - 1]);
            } else if (choice == SHOP_ITEMS.length + 1) {
                return; // Retour
            } else {
                ui.printColoredLine("Choix invalide !", TextColor.ANSI.RED);
                ui.waitForKeyPress();
            }
        } catch (NumberFormatException e) {
            ui.printColoredLine("Veuillez entrer un nombre valide !", TextColor.ANSI.RED);
            ui.waitForKeyPress();
        }
    }

    /**
     * Crée des alliés disponibles à l'achat avec des stats variées
     */
    private Ally[] createAvailableAllies() {
        return new Ally[] {
                new Chevalier("Recrue Élite", 70, 20, 30, 25, 18, true),
                new Chevalier("Vétéran", 90, 25, 40, 20, 22, true),
                new Chevalier("Champion", 110, 30, 50, 30, 25, true),
                new Chevalier("Légende", 140, 35, 65, 35, 30, true)
        };
    }

    /**
     * Calcule le prix d'un allié basé sur ses statistiques
     */
    private int calculateAllyPrice(Ally ally) {
        int basePrice = ALLY_BASE_PRICE;
        int statBonus = (ally.getStat(Stat.HP) + ally.getStat(Stat.ATTAQUE) +
                ally.getStat(Stat.DEFENSE) + ally.getStat(Stat.SPEED) +
                ally.getStat(Stat.LUCK)) / 5;
        return basePrice + (ally.getLevel() * 10) + (statBonus * 2);
    }

    /**
     * Achète un allié
     */
    private void buyAlly(Ally ally) throws IOException, InterruptedException {
        int price = calculateAllyPrice(ally);

        if (globalInventory.getGold() >= price) {
            ui.clearScreen();
            ui.printColoredText("Confirmer l'achat de ", TextColor.ANSI.YELLOW);
            ui.printColoredText(ally.getName(), TextColor.ANSI.CYAN);
            ui.printColoredText(" pour " + price + " or ? (o/n): ", TextColor.ANSI.YELLOW);

            String confirm = ui.readLine().toLowerCase();
            if (confirm.equals("o") || confirm.equals("oui")) {
                globalInventory.spendGold(price);
                teamManager.addToCollection(ally);

                ui.clearScreen();
                ui.printColoredLine("✅ " + ally.getName() + " a rejoint votre collection !", TextColor.ANSI.GREEN);
                ui.printColoredText("💰 Or restant: ", TextColor.ANSI.YELLOW);
                ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);
                ui.printLine("");
                ui.printColoredLine("Allez dans 'Équipe' pour l'ajouter à votre équipe active !", TextColor.ANSI.CYAN);
                ui.printLine("");
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
            }
        } else {
            ui.clearScreen();
            ui.printColoredLine("❌ Or insuffisant !", TextColor.ANSI.RED);
            ui.printColoredText("Vous avez: " + globalInventory.getGold() + " or", TextColor.ANSI.YELLOW);
            ui.printColoredText(" | Requis: " + price + " or", TextColor.ANSI.RED);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Achète un objet
     */
    private void buyItem(String[] itemData) throws IOException, InterruptedException {
        String itemName = itemData[0];
        int price = Integer.parseInt(itemData[1]);
        String description = itemData[2];

        if (globalInventory.getGold() >= price) {
            ui.clearScreen();
            ui.printColoredText("Confirmer l'achat de ", TextColor.ANSI.YELLOW);
            ui.printColoredText(itemName, TextColor.ANSI.CYAN);
            ui.printColoredText(" pour " + price + " or ? (o/n): ", TextColor.ANSI.YELLOW);

            String confirm = ui.readLine().toLowerCase();
            if (confirm.equals("o") || confirm.equals("oui")) {
                globalInventory.spendGold(price);
                globalInventory.addItem(itemName, 1);

                ui.clearScreen();
                ui.printColoredLine("✅ " + itemName + " ajouté à votre inventaire !", TextColor.ANSI.GREEN);
                ui.printColoredText("💰 Or restant: ", TextColor.ANSI.YELLOW);
                ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);
                ui.printLine("");
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
            }
        } else {
            ui.clearScreen();
            ui.printColoredLine("❌ Or insuffisant !", TextColor.ANSI.RED);
            ui.printColoredText("Vous avez: " + globalInventory.getGold() + " or", TextColor.ANSI.YELLOW);
            ui.printColoredText(" | Requis: " + price + " or", TextColor.ANSI.RED);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Affiche les statistiques du joueur
     */
    private void showPlayerStats() throws IOException {
        ui.clearScreen();
        ui.printColoredLine("📊 VOS STATISTIQUES", TextColor.ANSI.CYAN);
        ui.printLine("");

        ui.printColoredText("💰 Or total: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(globalInventory.getGold() + " pièces", TextColor.ANSI.GREEN);

        ui.printColoredText("⚔️ Alliés dans la collection: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(teamManager.getCollectionSize() + "", TextColor.ANSI.GREEN);

        ui.printColoredText("🎒 Objets uniques: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(globalInventory.getUniqueItemCount() + "", TextColor.ANSI.GREEN);

        ui.printColoredText("📦 Total d'objets: ", TextColor.ANSI.YELLOW);
        ui.printColoredLine(globalInventory.getTotalItemCount() + "", TextColor.ANSI.GREEN);

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }
}