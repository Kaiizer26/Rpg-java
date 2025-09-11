import personnage.*;
import stats.Stat;
import ui.TerminalUI;
import combat.Combat;
import team.TeamManager;
import team.CustomizationManager;
import inventaire.GlobalInventory;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class rpg_main {
    // Instance de l'interface utilisateur
    private static TerminalUI ui = new TerminalUI();
    // Instance du gestionnaire de combat
    private static Combat combatManager = new Combat(ui);
    // Instance du gestionnaire d'équipe
    private static TeamManager teamManager = new TeamManager(ui);
    // NOUVEAU : Instance de l'inventaire global
    private static GlobalInventory globalInventory = new GlobalInventory();
    // NOUVEAU : Instance du gestionnaire de personnalisation
    private static CustomizationManager customizationManager = new CustomizationManager(ui, teamManager, globalInventory);

    /**
     * Initialise les personnages et la collection
     */
    private static void initializeCharacters() {
        // Création des personnages principaux
        Chevalier knight1 = new Chevalier("Gustave", 99, 32, 45, 45, 57, true);
        Chevalier knight2 = new Chevalier();
        knight2.setName("Simon");
        knight2.setStat(Stat.HP, 99);
        knight2.setStat(Stat.DEFENSE, 32);
        knight2.setStat(Stat.ATTAQUE, 85);
        knight2.setStat(Stat.SPEED, 60);
        knight2.setStat(Stat.LUCK, 57);

        // NOUVEAU : Ajout d'objets à l'inventaire GLOBAL au lieu de l'inventaire du personnage
        globalInventory.addItem("Pistolet", 1);
        globalInventory.addItem("Arbre", 1);
        globalInventory.addItem("Zebre", 1);
        globalInventory.addItem("Potion de soin", 5);
        globalInventory.addItem("Épée rouillée", 2);
        globalInventory.addItem("Bouclier en bois", 1);

        // Création d'autres personnages pour la collection
        Chevalier jordy = new Chevalier("Jordy");
        Chevalier johan = new Chevalier("Johan");
        Chevalier maelle = new Chevalier("Maelle");
        Chevalier kaiizer = new Chevalier("Kaiizerrr");

        // Ajout de tous les personnages à la collection
        teamManager.addToCollection(knight1);
        teamManager.addToCollection(knight2);
        teamManager.addToCollection(jordy);
        teamManager.addToCollection(johan);
        teamManager.addToCollection(maelle);
        teamManager.addToCollection(kaiizer);

        // Formation d'une équipe initiale avec les personnages principaux
        teamManager.addToTeam(knight1);
        teamManager.addToTeam(knight2);
    }

    /**
     * Démarre un tournoi avec l'équipe active
     */
    private static void startTournamentWithTeam() throws IOException, InterruptedException {
        List<Ally> activeTeam = teamManager.getActiveTeam();

        if (activeTeam.isEmpty()) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage dans l'équipe active !", TextColor.ANSI.RED);
            ui.printColoredLine("Allez dans 'Équipe' pour composer votre équipe.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        // AMÉLIORATION : Vérifier qu'il y a au moins un personnage vivant
        Ally firstAliveMember = teamManager.getFirstAliveMember();
        if (firstAliveMember == null) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage vivant dans l'équipe !", TextColor.ANSI.RED);
            ui.printColoredLine("Tous vos personnages sont morts. Allez dans 'Personnalisation' pour les soigner.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        // Utiliser le premier membre vivant de l'équipe
        Personnage selectedHero = firstAliveMember;

        if (selectedHero instanceof Chevalier) {
            // Créer des adversaires
            Chevalier[] enemies = {new Chevalier("Jordy Enemy"), new Chevalier("Johan Enemy")};

            ui.clearScreen();
            ui.printColoredLine("🏆 Tournoi avec " + selectedHero.getName(), TextColor.ANSI.CYAN);
            ui.printColoredText("Niveau: ", TextColor.ANSI.YELLOW);
            ui.printLine(selectedHero.getLevel() + "");
            ui.printColoredText("HP: ", TextColor.ANSI.GREEN);
            ui.printLine(selectedHero.getStat(Stat.HP) + "");
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour commencer...", TextColor.ANSI.YELLOW);
            ui.waitForKeyPress();

            combatManager.startTournament((Chevalier) selectedHero, enemies);

            // NOUVEAU : Après le combat, donner de l'expérience
            if (selectedHero.getStat(Stat.HP) > 0) { // Si le héros a survécu
                selectedHero.addExperience(50); // 50 exp pour avoir participé au tournoi

                ui.clearScreen();
                ui.printColoredLine("🎉 Tournoi terminé !", TextColor.ANSI.GREEN);
                ui.printColoredLine("+" + 50 + " EXP gagné !", TextColor.ANSI.YELLOW);
                ui.printColoredText("Niveau actuel: ", TextColor.ANSI.CYAN);
                ui.printLine(selectedHero.getLevel() + "");
                ui.printColoredText("EXP: ", TextColor.ANSI.CYAN);
                ui.printLine(selectedHero.getExperience() + "/" + selectedHero.getExperienceToNextLevel());
                ui.printLine("");
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
            }

        } else {
            ui.clearScreen();
            ui.printColoredLine("❌ Le personnage sélectionné ne peut pas participer au tournoi.", TextColor.ANSI.RED);
            ui.printColoredLine("Seuls les Chevaliers peuvent participer pour l'instant.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * NOUVELLE MÉTHODE : Afficher des informations de debug/développement
     */
    private static void showDebugInfo() throws IOException {
        ui.clearScreen();
        ui.printColoredLine("🔧 INFORMATIONS DE DEBUG", TextColor.ANSI.MAGENTA);
        ui.printLine("");

        ui.printColoredLine("=== ÉQUIPE ACTIVE ===", TextColor.ANSI.CYAN);
        List<Ally> activeTeam = teamManager.getActiveTeam();
        for (Ally member : activeTeam) {
            ui.printColoredText("- ", TextColor.ANSI.WHITE);
            ui.printColoredText(member.getName(), TextColor.ANSI.YELLOW);
            ui.printColoredText(" (Niv." + member.getLevel() + ")", TextColor.ANSI.GREEN);
            ui.printColoredText(" HP: " + member.getStat(Stat.HP), member.getStat(Stat.HP) > 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.RED);
            ui.printLine("");
        }

        ui.printLine("");
        ui.printColoredLine("=== INVENTAIRE GLOBAL ===", TextColor.ANSI.CYAN);
        ui.printColoredText("Items uniques: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getUniqueItemCount() + "");
        ui.printColoredText("Total items: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getTotalItemCount() + "");

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    public static void main(String[] args) {
        try {
            // Initialisation de l'interface utilisateur
            ui.initialize();

            // Initialisation des personnages et de l'équipe
            initializeCharacters();

            // === ÉCRAN DE TITRE ===
            ui.showTitleScreen();

            // === BOUCLE PRINCIPALE DU JEU ===
            boolean continueGame = true;

            while (continueGame) {
                int menuChoice = ui.showMainMenu();

                switch (menuChoice) {
                    case 1: // Rejoindre le tournoi
                        startTournamentWithTeam();
                        break;

                    case 2: // Équipe
                        teamManager.showTeamManagementMenu();
                        break;

                    case 3: // Personnalisation - NOUVEAU : Implémenté !
                        customizationManager.showCustomizationMenu();
                        break;

                    case 4: // Boutique
                        ui.showNotImplemented("Boutique");
                        break;

                    case 5: // Quitter le jeu
                        ui.clearScreen();
                        ui.animatedText("🌙 Merci d'avoir joué ! À bientôt ! 🌙", TextColor.ANSI.MAGENTA, 50);
                        Thread.sleep(1500);
                        continueGame = false;
                        break;

                    default:
                        ui.printColoredLine("Choix invalide, veuillez réessayer.", TextColor.ANSI.RED);
                        break;
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur durant l'exécution du jeu : " + e.getMessage());
            e.printStackTrace(); // AMÉLIORATION : Afficher la stack trace pour debug
        } finally {
            // Nettoyage de l'interface utilisateur
            try {
                ui.cleanup();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}