package team;

import personnage.Ally;
import personnage.Personnage;
import stats.Stat;
import ui.TerminalUI;
import inventaire.GlobalInventory;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.*;

/**
 * Gestionnaire pour la personnalisation des personnages et de l'inventaire
 */
public class CustomizationManager implements ICustomizationManager {
    private TerminalUI ui;
    private TeamManager teamManager;
    private GlobalInventory globalInventory;

    public CustomizationManager(TerminalUI ui, TeamManager teamManager, GlobalInventory globalInventory) {
        this.ui = ui;
        this.teamManager = teamManager;
        this.globalInventory = globalInventory;
    }

    /**
     * Menu principal de personnalisation
     */
    public void showCustomizationMenu() throws IOException, InterruptedException {
        boolean continueCustomizing = true;

        while (continueCustomizing) {
            ui.clearScreen();
            displayCustomizationOverview();

            String[] options = {
                    "✏️ Renommer le personnage principal",
                    "⚡ Personnaliser les stats",
                    "📦 Voir l'inventaire global",
                    "📊 Voir détails des personnages",
                    "🔙 Retour au menu principal"
            };

            int choice = ui.showMenu(options, "🎨 PERSONNALISATION");

            switch (choice) {
                case 1: renameMainCharacter(); break;
                case 2: customizeStats(); break;
                case 3: showGlobalInventory(); break;
                case 4: showCharacterDetails(); break;
                case 5: continueCustomizing = false; break;
            }
        }
    }

    /**
     * Afficher un aperçu de la personnalisation
     */
    public void displayCustomizationOverview() throws IOException {
        ui.printColoredLine("╔══════════════════════════════════════╗", TextColor.ANSI.CYAN);
        ui.printColoredLine("║           PERSONNALISATION           ║", TextColor.ANSI.CYAN);
        ui.printColoredLine("╚══════════════════════════════════════╝", TextColor.ANSI.CYAN);
        ui.printLine("");

        // Trouver le personnage principal
        Ally mainCharacter = findMainCharacter();
        if (mainCharacter != null) {
            ui.printColoredText("👑 Personnage principal: ", TextColor.ANSI.YELLOW);
            ui.printLine(mainCharacter.getName() + " (Niveau " + mainCharacter.getLevel() + ")");
        }

        ui.printColoredText("👥 Personnages total: ", TextColor.ANSI.YELLOW);
        ui.printLine(teamManager.getCollection().size() + "");

        ui.printColoredText("📦 Items dans l'inventaire: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getUniqueItemCount() + " types différents");
        ui.printLine("");
    }

    /**
     * Renommer le personnage principal (knight1 uniquement)
     */
    public void renameMainCharacter() throws IOException {
        Ally mainCharacter = findMainCharacter();

        if (mainCharacter == null) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage principal trouvé!", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("✏️ RENOMMER LE PERSONNAGE PRINCIPAL", TextColor.ANSI.GREEN);
        ui.printLine("");
        ui.printColoredText("Nom actuel: ", TextColor.ANSI.YELLOW);
        ui.printLine(mainCharacter.getName());
        ui.printLine("");

        ui.printColoredText("Nouveau nom (ou ENTER pour annuler): ", TextColor.ANSI.CYAN);
        String newName = ui.readLine().trim();

        if (!newName.isEmpty()) {
            String oldName = mainCharacter.getName();
            mainCharacter.setName(newName);

            ui.clearScreen();
            ui.printColoredLine("✅ Personnage renommé avec succès!", TextColor.ANSI.GREEN);
            ui.printColoredLine("Ancien nom: " + oldName, TextColor.ANSI.YELLOW);
            ui.printColoredLine("Nouveau nom: " + newName, TextColor.ANSI.GREEN);
        } else {
            ui.clearScreen();
            ui.printColoredLine("❌ Renommage annulé.", TextColor.ANSI.YELLOW);
        }

        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Menu de personnalisation des stats
     */
    public void customizeStats() throws IOException {
        List<Ally> collection = teamManager.getCollection();

        if (collection.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage disponible.", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("⚡ PERSONNALISER LES STATS", TextColor.ANSI.CYAN);
        ui.printLine("");

        // Sélection du personnage
        String[] options = new String[collection.size() + 1];
        for (int i = 0; i < collection.size(); i++) {
            Ally p = collection.get(i);
            options[i] = p.getName() + " (" + p.getClass().getSimpleName() + ") - Niveau " + p.getLevel();
        }
        options[collection.size()] = "🔙 Retour";

        int choice = ui.showMenu(options, "Choisissez un personnage:");

        if (choice <= collection.size()) {
            Ally selected = collection.get(choice - 1);
            customizeCharacterStats(selected);
        }
    }

    /**
     * Personnaliser les stats d'un personnage spécifique
     */
    public void customizeCharacterStats(Ally character) throws IOException {
        boolean continueCustomizing = true;
        int availablePoints;

        while (continueCustomizing) {
            availablePoints = character.getAvailablePoints();
            ui.clearScreen();
            ui.printColoredLine("⚡ STATS DE " + character.getName().toUpperCase(), TextColor.ANSI.CYAN);
            ui.printLine("");

            // Afficher les stats actuelles
            ui.printColoredText("Niveau actuel: ", TextColor.ANSI.YELLOW);
            ui.printLine(character.getLevel() + " (Points disponibles: " + availablePoints + ")");
            ui.printLine("");

            ui.printColoredLine("Stats actuelles:", TextColor.ANSI.GREEN);
            for (Stat stat : Stat.values()) {
                ui.printColoredText("  " + stat.name() + ": ", TextColor.ANSI.WHITE);
                ui.printLine(character.getStat(stat) + "");
            }
            ui.printLine("");

            // Menu des stats
            String[] statOptions = new String[Stat.values().length + 1];
            for (int i = 0; i < Stat.values().length; i++) {
                Stat stat = Stat.values()[i];
                statOptions[i] = stat.name() + " (" + character.getStat(stat) + ") - Modifier";
            }
            statOptions[Stat.values().length] = "🔙 Retour";

            int choice = ui.showMenu(statOptions, "Quelle stat modifier?");

            if (choice <= Stat.values().length) {
                Stat selectedStat = Stat.values()[choice - 1];
                modifyStat(character, selectedStat);
            } else {
                continueCustomizing = false;
            }
        }
    }

    /**
     * Modifier une stat spécifique
     */
    /**
     * Modifier une stat spécifique en respectant les points disponibles
     */
    public void modifyStat(Ally character, Stat stat) throws IOException {
        int currentValue = character.getStat(stat);
        int availablePoints = character.getAvailablePoints();

        ui.clearScreen();
        ui.printColoredLine("⚡ MODIFIER " + stat.name(), TextColor.ANSI.CYAN);
        ui.printLine("");
        ui.printColoredText("Valeur actuelle: ", TextColor.ANSI.YELLOW);
        ui.printLine(currentValue + "");
        ui.printColoredText("Points disponibles: ", TextColor.ANSI.GREEN);
        ui.printLine(availablePoints + "");
        ui.printColoredLine("Plage autorisée: 0-99", TextColor.ANSI.WHITE);

        // Calculer la valeur maximale possible
        int maxPossibleValue = Math.min(99, currentValue + availablePoints);
        ui.printColoredText("Valeur max possible: ", TextColor.ANSI.CYAN);
        ui.printLine(maxPossibleValue + "");
        ui.printLine("");

        try {
            ui.printColoredText("Nouvelle valeur (0-" + maxPossibleValue + "): ", TextColor.ANSI.CYAN);
            String input = ui.readLine().trim();

            if (input.isEmpty()) {
                return; // Annuler
            }

            int newValue = Integer.parseInt(input);

            if (newValue < 0 || newValue > 99) {
                ui.clearScreen();
                ui.printColoredLine("❌ Valeur invalide! Doit être entre 0 et 99.", TextColor.ANSI.RED);
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
                return;
            }

            // Vérifier si on a assez de points
            int pointsNeeded = newValue - currentValue;
            if (pointsNeeded > availablePoints) {
                ui.clearScreen();
                ui.printColoredLine("❌ Pas assez de points disponibles!", TextColor.ANSI.RED);
                ui.printColoredLine("Points nécessaires: " + pointsNeeded, TextColor.ANSI.YELLOW);
                ui.printColoredLine("Points disponibles: " + availablePoints, TextColor.ANSI.YELLOW);
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
                return;
            }

            character.setStat(stat, newValue);

            ui.clearScreen();
            ui.printColoredLine("✅ Stat modifiée avec succès!", TextColor.ANSI.GREEN);
            ui.printColoredLine(stat.name() + ": " + currentValue + " → " + newValue, TextColor.ANSI.YELLOW);
            ui.printColoredLine("Points utilisés: " + pointsNeeded, TextColor.ANSI.CYAN);
            ui.printColoredLine("Points restants: " + character.getAvailablePoints(), TextColor.ANSI.GREEN);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();

        } catch (NumberFormatException e) {
            ui.clearScreen();
            ui.printColoredLine("❌ Veuillez entrer un nombre valide.", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Afficher l'inventaire global
     */
    public void showGlobalInventory() throws IOException {
        ui.clearScreen();
        ui.printColoredLine("📦 INVENTAIRE GLOBAL", TextColor.ANSI.CYAN);
        ui.printLine("");

        if (globalInventory.isEmpty()) {
            ui.printColoredLine("Inventaire vide.", TextColor.ANSI.YELLOW);
        } else {
            ui.printColoredText("Types d'items: ", TextColor.ANSI.YELLOW);
            ui.printLine(globalInventory.getUniqueItemCount() + "");
            ui.printColoredText("Total d'items: ", TextColor.ANSI.YELLOW);
            ui.printLine(globalInventory.getTotalItemCount() + "");
            ui.printLine("");

            Map<String, Integer> items = globalInventory.getAllItems();
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                ui.printColoredText("📦 ", TextColor.ANSI.GREEN);
                ui.printColoredText(entry.getKey() + ": ", TextColor.ANSI.WHITE);
                ui.printLine(entry.getValue() + "");
            }
        }

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Afficher les détails des personnages
     */
    public void showCharacterDetails() throws IOException {
        List<Ally> collection = teamManager.getCollection();

        ui.clearScreen();
        ui.printColoredLine("📊 DÉTAILS DES PERSONNAGES", TextColor.ANSI.CYAN);
        ui.printLine("");

        if (collection.isEmpty()) {
            ui.printColoredLine("Aucun personnage dans la collection.", TextColor.ANSI.YELLOW);
        } else {
            for (Ally character : collection) {
                ui.printColoredLine("═══ " + character.getName() + " ═══", TextColor.ANSI.GREEN);
                ui.printColoredText("Classe: ", TextColor.ANSI.YELLOW);
                ui.printLine(character.getClass().getSimpleName());
                ui.printColoredText("Niveau: ", TextColor.ANSI.YELLOW);
                ui.printLine(character.getLevel() + "");
                ui.printColoredText("Expérience: ", TextColor.ANSI.YELLOW);
                ui.printLine(character.getExperience() + "/" + character.getExperienceToNextLevel());

                ui.printColoredLine("Stats:", TextColor.ANSI.CYAN);
                for (Stat stat : Stat.values()) {
                    ui.printColoredText("  " + stat.name() + ": ", TextColor.ANSI.WHITE);
                    ui.printLine(character.getStat(stat) + "");
                }
                ui.printLine("");
            }
        }

        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Trouver le personnage principal (celui avec isStart() == true)
     */
    public Ally findMainCharacter() {
        for (Ally character : teamManager.getCollection()) {
            if (character.isStart()) {
                return character;
            }
        }
        return null;
    }
}