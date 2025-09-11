import personnage.Chevalier;
import personnage.Ennemi;
import personnage.Gobelin;
import personnage.Personnage;
import stats.Stat;
import ui.TerminalUI;
import combat.Combat;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class rpg_main {
    // Instance de l'interface utilisateur
    private static TerminalUI ui = new TerminalUI();
    // Instance du gestionnaire de combat
    private static Combat combatManager = new Combat(ui);

    public static void main(String[] args) {
        try {
            // Initialisation de l'interface utilisateur
            ui.initialize();

            // Création des personnages
            Chevalier knight1 = new Chevalier("Gustave", 999, 32, 45, 45, 57, true);

            Chevalier knight2 = new Chevalier();
            knight2.setName("Simon");
            knight2.setStat(Stat.HP, 99);
            knight2.setStat(Stat.DEFENSE, 32);
            knight2.setStat(Stat.ATTAQUE, 85);
            knight2.setStat(Stat.SPEED, 60);
            knight2.setStat(Stat.LUCK, 57);

            Chevalier[] knightTab = {new Chevalier("Jordy"), new Chevalier("Johan")};

            // Code pour d'autres personnages (optionnel)
            Personnage[] persos = {new Ennemi("Joepoknoob77"), new Gobelin("Jordy"), new Chevalier("Kaiizerrr")};
            List<Personnage> equipe = new ArrayList<>();
            equipe.add(new Chevalier("Maelle"));
            equipe.add(new Gobelin("Frantz"));

            // Ajout d'objets à l'inventaire
            knight1.addItem("Pistolet", 1);
            knight1.addItem("Arbre", 1);
            knight1.addItem("Zebre", 1);

            // === ÉCRAN DE TITRE ===
            ui.showTitleScreen();

            // === BOUCLE PRINCIPALE DU JEU ===
            boolean continueGame = true;

            while (continueGame) {
                int menuChoice = ui.showMainMenu();

                switch (menuChoice) {
                    case 1: // Rejoindre le tournoi
                        combatManager.startTournament(knight1, knightTab);
                        break;

                    case 2: // Equipe
                        ui.showNotImplemented("Equipe");
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