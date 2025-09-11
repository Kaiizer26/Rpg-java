package team;

import personnage.Ally;
import personnage.Personnage;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.*;

/**
 * Classe responsable de la gestion des équipes
 * Gère la collection de personnages et l'équipe active
 */
public class TeamManager {
    private static final int MAX_TEAM_SIZE = 3;

    private List<Ally> collection; // Tous les personnages disponibles
    private List<Ally> activeTeam; // Équipe active (max 3)
    private TerminalUI ui;

    /**
     * Constructeur
     */
    public TeamManager(TerminalUI ui) {
        this.ui = ui;
        this.collection = new ArrayList<>();
        this.activeTeam = new ArrayList<>();
    }

    /**
     * Ajoute un personnage à la collection
     */
    public void addToCollection(Ally personnage) {
        if (!collection.contains(personnage)) {
            collection.add(personnage);
        }
    }

    /**
     * Retire un personnage de la collection
     */
    public void removeFromCollection(Ally personnage) {
        collection.remove(personnage);
        activeTeam.remove(personnage); // Le retirer aussi de l'équipe active
    }

    /**
     * Ajoute un personnage à l'équipe active
     */
    public boolean addToTeam(Ally personnage) {
        if (activeTeam.size() >= MAX_TEAM_SIZE) {
            return false; // Équipe pleine
        }
        if (!collection.contains(personnage)) {
            return false; // Personnage pas dans la collection
        }
        if (activeTeam.contains(personnage)) {
            return false; // Déjà dans l'équipe
        }

        activeTeam.add(personnage);
        return true;
    }

    /**
     * Retire un personnage de l'équipe active
     */
    public void removeFromTeam(Ally personnage) {
        activeTeam.remove(personnage);
    }

    /**
     * Vérifie si l'équipe est pleine
     */
    public boolean isTeamFull() {
        return activeTeam.size() >= MAX_TEAM_SIZE;
    }

    /**
     * Retourne la taille de l'équipe active
     */
    public int getTeamSize() {
        return activeTeam.size();
    }

    /**
     * Retourne une copie de la collection
     */
    public List<Ally> getCollection() {
        return new ArrayList<>(collection);
    }

    /**
     * Retourne une copie de l'équipe active
     */
    public List<Ally> getActiveTeam() {
        return new ArrayList<>(activeTeam);
    }

    /**
     * Retourne les personnages disponibles (dans la collection mais pas dans l'équipe)
     */
    public List<Ally> getAvailablePersonnages() {
        List<Ally> available = new ArrayList<>();
        for (Ally p : collection) {
            if (!activeTeam.contains(p)) {
                available.add(p);
            }
        }
        return available;
    }

    /**
     * Affiche et gère le menu principal de gestion d'équipe
     */
    public void showTeamManagementMenu() throws IOException, InterruptedException {
        boolean continueManaging = true;

        while (continueManaging) {
            ui.clearScreen();
            displayTeamOverview();

            String[] options = {
                    "👥 Voir l'équipe active",
                    "📦 Voir la collection",
                    "➕ Ajouter à l'équipe",
                    "➖ Retirer de l'équipe",
                    "🔄 Échanger des membres",
                    "🔙 Retour au menu principal"
            };

            int choice = ui.showMenu(options, "🛡️ GESTION D'ÉQUIPE");

            switch (choice) {
                case 1:
                    showActiveTeam();
                    break;
                case 2:
                    showCollection();
                    break;
                case 3:
                    addPersonnageToTeam();
                    break;
                case 4:
                    removePersonnageFromTeam();
                    break;
                case 5:
                    swapTeamMembers();
                    break;
                case 6:
                    continueManaging = false;
                    break;
            }
        }
    }

    /**
     * Affiche un aperçu rapide de l'équipe
     */
    private void displayTeamOverview() throws IOException {
        ui.printColoredLine("╔══════════════════════════════════════╗", TextColor.ANSI.CYAN);
        ui.printColoredLine("║            APERÇU ÉQUIPE             ║", TextColor.ANSI.CYAN);
        ui.printColoredLine("╚══════════════════════════════════════╝", TextColor.ANSI.CYAN);
        ui.printLine("");

        ui.printColoredText("👥 Équipe active: ", TextColor.ANSI.YELLOW);
        ui.printLine(activeTeam.size() + "/" + MAX_TEAM_SIZE);

        ui.printColoredText("📦 Collection totale: ", TextColor.ANSI.YELLOW);
        ui.printLine(collection.size() + " personnages");

        ui.printLine("");
    }

    /**
     * Affiche l'équipe active
     */
    private void showActiveTeam() throws IOException {
        ui.clearScreen();
        ui.printColoredLine("👥 ÉQUIPE ACTIVE", TextColor.ANSI.CYAN);
        ui.printLine("");

        if (activeTeam.isEmpty()) {
            ui.printColoredLine("Aucun personnage dans l'équipe.", TextColor.ANSI.YELLOW);
        } else {
            for (int i = 0; i < activeTeam.size(); i++) {
                Ally p = activeTeam.get(i);
                ui.printColoredText((i + 1) + ". ", TextColor.ANSI.YELLOW);
                ui.printColoredText("⚔️ ", TextColor.ANSI.RED);
                ui.printLine(p.getName() + " (" + p.getClass().getSimpleName() + ")");
            }
        }

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Affiche toute la collection
     */
    private void showCollection() throws IOException {
        ui.clearScreen();
        ui.printColoredLine("📦 COLLECTION COMPLÈTE", TextColor.ANSI.CYAN);
        ui.printLine("");

        if (collection.isEmpty()) {
            ui.printColoredLine("Aucun personnage dans la collection.", TextColor.ANSI.YELLOW);
        } else {
            for (int i = 0; i < collection.size(); i++) {
                Ally p = collection.get(i);
                ui.printColoredText((i + 1) + ". ", TextColor.ANSI.YELLOW);

                // Indiquer si le personnage est dans l'équipe active
                if (activeTeam.contains(p)) {
                    ui.printColoredText("🟢 ", TextColor.ANSI.GREEN);
                } else {
                    ui.printColoredText("⚫ ", TextColor.ANSI.WHITE);
                }

                ui.printLine(p.getName() + " (" + p.getClass().getSimpleName() + ")");
            }

            ui.printLine("");
            ui.printColoredLine("🟢 = Dans l'équipe active", TextColor.ANSI.GREEN);
            ui.printColoredLine("⚫ = Disponible", TextColor.ANSI.WHITE);
        }

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Ajouter un personnage à l'équipe
     */
    private void addPersonnageToTeam() throws IOException {
        if (isTeamFull()) {
            ui.clearScreen();
            ui.printColoredLine("❌ L'équipe est déjà pleine ! (3/3)", TextColor.ANSI.RED);
            ui.printColoredLine("Retirez d'abord un membre.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        List<Ally> available = getAvailablePersonnages();
        if (available.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage disponible à ajouter.", TextColor.ANSI.RED);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("➕ AJOUTER À L'ÉQUIPE", TextColor.ANSI.GREEN);
        ui.printLine("");

        // Créer les options pour le menu
        String[] options = new String[available.size() + 1];
        for (int i = 0; i < available.size(); i++) {
            Ally p = available.get(i);
            options[i] = p.getName() + " (" + p.getClass().getSimpleName() + ")";
        }
        options[available.size()] = "🔙 Annuler";

        int choice = ui.showMenu(options, "Choisissez un personnage à ajouter:");

        if (choice <= available.size()) {
            Ally selected = available.get(choice - 1);
            addToTeam(selected);

            ui.clearScreen();
            ui.printColoredLine("✅ " + selected.getName() + " a été ajouté à l'équipe !", TextColor.ANSI.GREEN);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Retirer un personnage de l'équipe
     */
    private void removePersonnageFromTeam() throws IOException {
        if (activeTeam.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ L'équipe est vide.", TextColor.ANSI.RED);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("➖ RETIRER DE L'ÉQUIPE", TextColor.ANSI.RED);
        ui.printLine("");

        String[] options = new String[activeTeam.size() + 1];
        for (int i = 0; i < activeTeam.size(); i++) {
            Ally p = activeTeam.get(i);
            options[i] = p.getName() + " (" + p.getClass().getSimpleName() + ")";
        }
        options[activeTeam.size()] = "🔙 Annuler";

        int choice = ui.showMenu(options, "Choisissez un personnage à retirer:");

        if (choice <= activeTeam.size()) {
            Ally selected = activeTeam.get(choice - 1);
            removeFromTeam(selected);

            ui.clearScreen();
            ui.printColoredLine("✅ " + selected.getName() + " a été retiré de l'équipe.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Échange direct entre collection et équipe
     */
    private void swapTeamMembers() throws IOException {
        if (activeTeam.isEmpty() || getAvailablePersonnages().isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ Échange impossible.", TextColor.ANSI.RED);
            ui.printColoredLine("Il faut au moins un membre dans l'équipe et un disponible.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.showNotImplemented("Échange de membres");
    }
}