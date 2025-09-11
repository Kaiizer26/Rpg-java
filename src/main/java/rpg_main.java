import personnage.*;
import stats.Stat;
import ui.TerminalUI;
import combat.Combat;
import team.TeamManager;
import team.CustomizationManager;
import inventaire.GlobalInventory;
import shop.ShopManager;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;

public class rpg_main {
    // Instance de l'interface utilisateur
    private static TerminalUI ui = new TerminalUI();

    // Instance du gestionnaire d'équipe
    private static TeamManager teamManager = new TeamManager(ui);
    // Instance de l'inventaire global
    private static GlobalInventory globalInventory = new GlobalInventory();

    // Instance du gestionnaire de combat
    private static Combat combatManager = new Combat(ui, globalInventory);
    // Instance du gestionnaire de personnalisation
    private static CustomizationManager customizationManager = new CustomizationManager(ui, teamManager, globalInventory);
    // Instance du gestionnaire de boutique
    private static ShopManager shopManager = new ShopManager(ui, teamManager, globalInventory);

    /**
     * Initialise les personnages et la collection
     */
    private static void initializeCharacters()  {
        // Création des personnages principaux
        Knight knight1 = new Knight("Gustave");
        Knight knight2 = new Knight("Simon");

        globalInventory.addGold(500); // Or de départ

        // Ajout d'objets à l'inventaire global
        globalInventory.addItem("Table Ikea", 1);
        globalInventory.addItem("Potion de soin", 5);

        // Création d'autres personnages pour la collection
        Knight jordy = new Knight("Jordy");
        Knight johan = new Knight("Johan");
        Knight maelle = new Knight("Maelle");
        Knight kaiizer = new Knight("Kaiizerrr");

        // Personnages de départ dans la collection
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
     * Vérifie si l'équipe a des membres vivants
     */
    private static boolean hasAliveTeamMembers(List<Ally> team) {
        for (Ally ally : team) {
            if (ally.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    /**
     * Démarre un tournoi avec l'équipe complète (NOUVEAU SYSTÈME)
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

        // Vérifier qu'il y a au moins un personnage vivant
        if (!hasAliveTeamMembers(activeTeam)) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage vivant dans l'équipe !", TextColor.ANSI.RED);
            ui.printColoredLine("Tous vos personnages sont morts. Allez dans 'Personnalisation' pour les soigner.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        // Affichage des informations de l'équipe avant le combat
        ui.clearScreen();
        ui.printColoredLine("🏆 TOURNOI D'ÉQUIPE", TextColor.ANSI.CYAN);
        ui.printLine("");
        ui.printColoredLine("Votre équipe:", TextColor.ANSI.GREEN);

        int aliveCount = 0;
        for (Ally ally : activeTeam) {
            String statusIcon = ally.getStat(Stat.HP) > 0 ? "⚔️" : "💀";
            TextColor statusColor = ally.getStat(Stat.HP) > 0 ? TextColor.ANSI.GREEN : TextColor.ANSI.RED;

            ui.printColoredText(statusIcon + " ", statusColor);
            ui.printColoredText(ally.getName(), TextColor.ANSI.YELLOW);
            ui.printColoredText(" (Niv." + ally.getLevel() + ")", TextColor.ANSI.CYAN);
            ui.printColoredText(" HP: " + ally.getStat(Stat.HP), statusColor);
            ui.printLine("");

            if (ally.getStat(Stat.HP) > 0) aliveCount++;
        }

        ui.printLine("");
        ui.printColoredText("Combattants disponibles: ", TextColor.ANSI.YELLOW);
        ui.printLine(aliveCount + "");
        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour commencer le tournoi...", TextColor.ANSI.YELLOW);
        ui.waitForKeyPress();

        // Créer des adversaires pour le tournoi
        Personnage[] enemies = {
                new Knight("Garde du Roi", 80, 25, 35, 20, 15),
                new Knight("Champion de l'Arène", 120, 30, 45, 25, 20)
        };

        // Utiliser le système de combat en équipe
        boolean tournamentWon = combatManager.startTeamTournament(activeTeam, enemies);

        // Après le tournoi, donner de l'expérience aux survivants ET de l'or si victoire
        int survivors = 0;
        for (Ally ally : activeTeam) {
            if (ally.getStat(Stat.HP) > 0) {
                survivors++;
                ally.addExperience(75); // Bonus d'expérience pour les survivants
            }
        }

        if (survivors > 0) {
            ui.clearScreen();
            ui.printColoredLine("🎉 Tournoi terminé !", TextColor.ANSI.GREEN);
            ui.printColoredLine("Survivants: " + survivors + "/" + activeTeam.size(), TextColor.ANSI.YELLOW);
            ui.printColoredLine("+" + 75 + " EXP bonus pour chaque survivant !", TextColor.ANSI.MAGENTA);

            // Récompense en or si victoire
            if (tournamentWon) {
                int goldReward = 150 + (survivors * 25); // Bonus basé sur les survivants
                globalInventory.addGold(goldReward);
                ui.printColoredLine("💰 +" + goldReward + " or pour la victoire !", TextColor.ANSI.YELLOW);
            }

            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
        }
    }

    /**
     * Combat d'entraînement contre un seul ennemi
     */
    private static void startTrainingCombat() throws IOException, InterruptedException {
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

        if (!hasAliveTeamMembers(activeTeam)) {
            ui.clearScreen();
            ui.printColoredLine("❌ Aucun personnage vivant dans l'équipe !", TextColor.ANSI.RED);
            ui.printColoredLine("Tous vos personnages sont morts.", TextColor.ANSI.YELLOW);
            ui.printLine("");
            ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
            ui.waitForKeyPress();
            return;
        }

        ui.clearScreen();
        ui.printColoredLine("⚔️ COMBAT D'ENTRAÎNEMENT", TextColor.ANSI.CYAN);
        ui.printLine("");
        ui.printColoredLine("Choisissez votre adversaire:", TextColor.ANSI.YELLOW);

        String[] enemyOptions = {
                "🛡️ Garde débutant (Facile)",
                "⚔️ Soldat expérimenté (Moyen)",
                "👑 Chevalier royal (Difficile)",
                "🔙 Retour"
        };

        int choice = ui.showMenu(enemyOptions, "Adversaire:");

        Personnage enemy = null;
        int goldReward = 0;

        switch (choice) {
            case 1:
                enemy = new Knight("Garde débutant", 60, 15, 25, 15, 10);
                goldReward = 30;
                break;
            case 2:
                enemy = new Knight("Soldat expérimenté", 85, 20, 35, 20, 15);
                goldReward = 50;
                break;
            case 3:
                enemy = new Knight("Chevalier royal", 110, 25, 45, 25, 20);
                goldReward = 75;
                break;
            case 4:
                return; // Retour
        }

        if (enemy != null) {
            boolean won = combatManager.startTeamVsEnemyCombat(activeTeam, enemy);

            // Donner de l'expérience et de l'or après le combat d'entraînement
            int expGain = 25;
            for (Ally ally : activeTeam) {
                if (ally.getStat(Stat.HP) > 0) {
                    ally.addExperience(expGain);
                }
            }

            if (won) {
                globalInventory.addGold(goldReward);
                ui.clearScreen();
                ui.printColoredLine("✅ Combat d'entraînement terminé !", TextColor.ANSI.GREEN);
                ui.printColoredLine("+" + expGain + " EXP pour les survivants !", TextColor.ANSI.YELLOW);
                ui.printColoredLine("💰 +" + goldReward + " or pour la victoire !", TextColor.ANSI.YELLOW);
                ui.printLine("");
                ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
                ui.waitForKeyPress();
            }
        }
    }

    /**
     * Afficher des informations de debug/développement
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
        ui.printColoredText("Or: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getGold() + " pièces");
        ui.printColoredText("Items uniques: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getUniqueItemCount() + "");
        ui.printColoredText("Total items: ", TextColor.ANSI.YELLOW);
        ui.printLine(globalInventory.getTotalItemCount() + "");

        ui.printLine("");
        ui.printColoredLine("Appuyez sur une touche pour continuer...", TextColor.ANSI.CYAN);
        ui.waitForKeyPress();
    }

    /**
     * Menu de combat avec plusieurs options
     */
    private static void showCombatMenu() throws IOException, InterruptedException {
        boolean continueCombat = true;

        while (continueCombat) {
            ui.clearScreen();
            ui.printColoredLine("⚔️ MENU DE COMBAT", TextColor.ANSI.CYAN);
            ui.printLine("");

            String[] combatOptions = {
                    "🏆 Tournoi (Combat contre plusieurs ennemis)",
                    "⚔️ Combat d'entraînement (Combat contre un ennemi)",
                    "🔧 Informations de debug",
                    "🔙 Retour au menu principal"
            };

            int choice = ui.showMenu(combatOptions, "Que voulez-vous faire ?");

            switch (choice) {
                case 1:
                    startTournamentWithTeam();
                    break;
                case 2:
                    startTrainingCombat();
                    break;
                case 3:
                    showDebugInfo();
                    break;
                case 4:
                    continueCombat = false;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Initialisation de l'interface utilisateur
            ui.initialize();

            // Initialisation des personnages et de l'équipe
            initializeCharacters();

            // Écran de titre
            ui.showTitleScreen();

            // Boucle principale du jeu
            boolean continueGame = true;

            while (continueGame) {
                int menuChoice = ui.showMainMenu();

                switch (menuChoice) {
                    case 1:
                        showCombatMenu();
                        break;

                    case 2: // Équipe
                        teamManager.showTeamManagementMenu();
                        break;

                    case 3: // Personnalisation
                        customizationManager.showCustomizationMenu();
                        break;

                    case 4: // Boutique
                        shopManager.showShopMenu();
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
            e.printStackTrace();
        } finally {
            // Nettoyage de l'interface utilisateur
            try {
                ui.cleanup();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }}