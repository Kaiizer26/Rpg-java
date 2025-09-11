import personnage.*;
import stats.Stat;
import ui.TerminalUI;
import combat.Combat;
import team.TeamManager;
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

        // Ajout d'objets à l'inventaire de Gustave
        knight1.addItem("Pistolet", 1);
        knight1.addItem("Arbre", 1);
        knight1.addItem("Zebre", 1);

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

        // Pour l'instant, utiliser le premier membre de l'équipe
        Personnage selectedHero = activeTeam.get(0);

        if (selectedHero instanceof Chevalier) {
            // Créer des adversaires
            Chevalier[] enemies = {new Chevalier("Jordy Enemy"), new Chevalier("Johan Enemy")};

            ui.clearScreen();
            ui.printColoredLine("🏆 Tournoi avec " + selectedHero.getName(), TextColor.ANSI.CYAN);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour commencer...", TextColor.ANSI.YELLOW);
            ui.waitForKeyPress();

            combatManager.startTournament((Chevalier) selectedHero, enemies);
        } else {
            ui.clearScreen();
            ui.printColoredLine("❌ Le personnage sélectionné ne peut pas participer au tournoi.", TextColor.ANSI.RED);
            ui.printColoredLine("Seuls les Chevaliers peuvent participer pour l'instant.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
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

                    case 3: // Personnalisation
                        ui.showNotImplemented("Personnalisation");
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