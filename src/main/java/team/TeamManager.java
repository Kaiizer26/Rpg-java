package team;

import personnage.Personnage;
import personnage.Ally;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.*;

/**
 * Classe responsable de la gestion des équipes
 * Utilise des types cohérents (Personnage/Ally)
 */
public class TeamManager {
    private static final int MAX_TEAM_SIZE = 3;

    private List<Ally> collection; // Tous les personnages disponibles
    private List<Ally> activeTeam; // Équipe active (max 3)
    private TerminalUI ui;

    public TeamManager(TerminalUI ui) {
        this.ui = ui;
        this.collection = new ArrayList<>();
        this.activeTeam = new ArrayList<>();
    }

    // Méthodes de base inchangées...
    public void addToCollection(Ally personnage) {
        if (!collection.contains(personnage)) {
            collection.add(personnage);
        }
    }

    public boolean addToTeam(Ally personnage) {
        if (activeTeam.size() >= MAX_TEAM_SIZE ||
                !collection.contains(personnage) ||
                activeTeam.contains(personnage)) {
            return false;
        }
        activeTeam.add(personnage);
        return true;
    }

    public void removeFromTeam(Ally personnage) {
        activeTeam.remove(personnage);
    }

    public boolean isTeamFull() {
        return activeTeam.size() >= MAX_TEAM_SIZE;
    }

    public int getTeamSize() {
        return activeTeam.size();
    }

    // CORRECTION : Retourner le bon type
    public List<Ally> getCollection() {
        return new ArrayList<>(collection);
    }

    public List<Ally> getActiveTeam() {
        return new ArrayList<>(activeTeam);
    }

    /**
     * NOUVELLE MÉTHODE : Obtenir le prochain membre d'équipe vivant
     * @param currentMember Le membre actuel (mort)
     * @return Le prochain membre vivant, ou null si aucun
     */
    public Ally getNextAliveMember(Ally currentMember) {
        // Retirer le membre mort de l'équipe active
        activeTeam.remove(currentMember);

        // Chercher le prochain membre vivant
        for (Ally member : activeTeam) {
            if (member.getStat(stats.Stat.HP) > 0) {
                return member;
            }
        }
        return null; // Plus de membres vivants
    }

    /**
     * NOUVELLE MÉTHODE : Vérifier si l'équipe a encore des membres vivants
     */
    public boolean hasAliveMember() {
        return activeTeam.stream().anyMatch(p -> p.getStat(stats.Stat.HP) > 0);
    }

    /**
     * NOUVELLE MÉTHODE : Obtenir le premier membre vivant de l'équipe
     */
    public Ally getFirstAliveMember() {
        return activeTeam.stream()
                .filter(p -> p.getStat(stats.Stat.HP) > 0)
                .findFirst()
                .orElse(null);
    }

    // Le reste des méthodes UI reste identique mais avec les bons types...
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
                    "🔙 Retour au menu principal"
            };

            int choice = ui.showMenu(options, "🛡️ GESTION D'ÉQUIPE");

            switch (choice) {
                case 1: showActiveTeam(); break;
                case 2: showCollection(); break;
                case 3: addPersonnageToTeam(); break;
                case 4: removePersonnageFromTeam(); break;
                case 5: continueManaging = false; break;
            }
        }
    }

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

                // Afficher le statut de vie
                if (p.getStat(stats.Stat.HP) > 0) {
                    ui.printColoredText("❤️ ", TextColor.ANSI.GREEN);
                } else {
                    ui.printColoredText("💀 ", TextColor.ANSI.RED);
                }

                ui.printLine(p.getName() + " (" + p.getClass().getSimpleName() +
                        ") - HP: " + p.getStat(stats.Stat.HP));
            }
        }

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

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

                if (activeTeam.contains(p)) {
                    ui.printColoredText("🟢 ", TextColor.ANSI.GREEN);
                } else {
                    ui.printColoredText("⚫ ", TextColor.ANSI.WHITE);
                }

                ui.printLine(p.getName() + " (" + p.getClass().getSimpleName() + ")");
            }

            ui.printLine("");
            ui.printColoredLine("🟢 = Dans l'équipe active", TextColor.ANSI.GREEN);
        }

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    private void addPersonnageToTeam() throws IOException {
        if (isTeamFull()) {
            ui.clearScreen();
            ui.printColoredLine("❌ L'équipe est déjà pleine ! (3/3)", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        List<Ally> available = new ArrayList<>();
        for (Ally p : collection) {
            if (!activeTeam.contains(p)) {
                available.add(p);
            }
        }

        if (available.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage disponible.", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("➕ AJOUTER À L'ÉQUIPE", TextColor.ANSI.GREEN);
        ui.printLine("");

        String[] options = new String[available.size() + 1];
        for (int i = 0; i < available.size(); i++) {
            Ally p = available.get(i);
            options[i] = p.getName() + " (" + p.getClass().getSimpleName() + ")";
        }
        options[available.size()] = "🔙 Annuler";

        int choice = ui.showMenu(options, "Choisissez un personnage:");

        if (choice <= available.size()) {
            Ally selected = available.get(choice - 1);
            addToTeam(selected);

            ui.clearScreen();
            ui.printColoredLine("✅ " + selected.getName() + " ajouté à l'équipe !", TextColor.ANSI.GREEN);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    private void removePersonnageFromTeam() throws IOException {
        if (activeTeam.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ L'équipe est vide.", TextColor.ANSI.RED);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        // NOUVELLE VÉRIFICATION : Empêcher de retirer le dernier membre
        if (activeTeam.size() == 1) {
            ui.clearScreen();
            ui.printColoredLine("❌ Impossible de retirer le dernier membre de l'équipe !", TextColor.ANSI.RED);
            ui.printColoredLine("💡 Il doit toujours rester au moins un personnage pour combattre.", TextColor.ANSI.YELLOW);
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
            ui.printColoredLine("✅ " + selected.getName() + " retiré de l'équipe.", TextColor.ANSI.YELLOW);
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }
}