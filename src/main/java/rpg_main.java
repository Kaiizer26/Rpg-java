import personnage.Chevalier;
import personnage.Ennemi;
import personnage.Gobelin;
import personnage.Personnage;
import stats.Stat;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class rpg_main {
    // Instance de l'interface utilisateur
    private static TerminalUI ui = new TerminalUI();

    /**
     * Détermine qui attaque en premier selon la vitesse
     */
    public static void firstToAttack(Chevalier knight1, Chevalier knight2) {
        if (knight1.getStat(Stat.SPEED) >= knight2.getStat(Stat.SPEED)) {
            knight1.setStart(true);
            knight2.setStart(false);
        } else {
            knight2.setStart(true);
            knight1.setStart(false);
        }
    }

    /**
     * Vérifie s'il reste des ennemis vivants
     */
    public static boolean hasAliveEnemies(Chevalier[] enemies) {
        for (Chevalier enemy : enemies) {
            if (enemy.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    /**
     * Gère le tour de combat d'un joueur
     */
    private static boolean handlePlayerTurn(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        ui.printColoredLine("🗡 Tour de " + player.getName(), TextColor.ANSI.CYAN);

        String[] options = {"Attaquer", "Fuir"};
        int choice = ui.showMenu(options, "Que voulez-vous faire ?");

        if (choice == 1) {
            ui.animatedText("⚔ " + player.getName() + " attaque !", TextColor.ANSI.YELLOW, 30);
            player.performAttack(enemy);

            if (enemy.getStat(Stat.HP) > 0) {
                ui.printColoredText("💔 ", TextColor.ANSI.RED);
                ui.printLine(enemy.getName() + " a maintenant " + enemy.getStat(Stat.HP) + " HP");
            } else {
                ui.animatedText("💀 " + enemy.getName() + " est vaincu !", TextColor.ANSI.GREEN, 50);
            }
            return true; // Continue le combat
        } else if (choice == 2) {
            ui.animatedText("🏃 " + player.getName() + " prend la fuite !", TextColor.ANSI.YELLOW, 50);
            return false; // Fuit le combat
        }
        return true;
    }

    /**
     * Gère le tour de combat d'un ennemi
     */
    private static void handleEnemyTurn(Chevalier enemy, Chevalier player) throws IOException, InterruptedException {
        ui.displayCombatStatus(player, enemy);

        ui.printColoredLine("💀 Tour de " + enemy.getName(), TextColor.ANSI.RED);
        ui.printColoredLine("L'ennemi réfléchit...", TextColor.ANSI.RED);
        Thread.sleep(1500);

        ui.animatedText("💥 " + enemy.getName() + " attaque !", TextColor.ANSI.RED, 30);
        enemy.performAttack(player);

        if (player.getStat(Stat.HP) > 0) {
            ui.printColoredText("💔 ", TextColor.ANSI.RED);
            ui.printLine(player.getName() + " a maintenant " + player.getStat(Stat.HP) + " HP");
        } else {
            ui.animatedText("💀 " + player.getName() + " est vaincu !", TextColor.ANSI.RED, 50);
        }
    }

    /**
     * Gère un combat complet entre le joueur et un ennemi
     */
    private static boolean handleSingleCombat(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        // Déterminer qui commence
        firstToAttack(player, enemy);

        ui.printLine("");
        ui.animatedText("🎯 Adversaire: " + enemy.getName(), TextColor.ANSI.RED, 30);
        Thread.sleep(1500);

        while (player.getStat(Stat.HP) > 0 && enemy.getStat(Stat.HP) > 0) {
            ui.displayCombatStatus(player, enemy);

            if (player.isStart()) {
                boolean continuesCombat = handlePlayerTurn(player, enemy);
                if (!continuesCombat) {
                    return false; // Joueur a fui
                }
                Thread.sleep(2000);
            }

            if (enemy.getStat(Stat.HP) > 0) {
                handleEnemyTurn(enemy, player);
                Thread.sleep(2000);
            }

            // Alternance des tours
            boolean temp = player.isStart();
            player.setStart(enemy.isStart());
            enemy.setStart(temp);
        }

        return player.getStat(Stat.HP) > 0; // true si le joueur a gagné
    }

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

            // === ÉCRANS DU JEU ===

            // Écran de titre
            ui.showTitleScreen();

            // Affichage du héros du joueur
            ui.showPlayerHeroSection(knight1);

            // Affichage des adversaires
            ui.showAdversariesSection(knightTab);

            // Début du combat
            ui.showCombatStart();

            // === BOUCLE DE COMBAT PRINCIPALE ===
            boolean playerAlive = true;

            for (int i = 0; i < knightTab.length && playerAlive; i++) {
                boolean wonFight = handleSingleCombat(knight1, knightTab[i]);

                if (!wonFight) {
                    playerAlive = false;
                    break;
                }

                if (knight1.getStat(Stat.HP) <= 0) {
                    playerAlive = false;
                    break;
                }
            }

            // === FIN DU JEU ===
            boolean playerWon = knight1.getStat(Stat.HP) > 0;
            ui.showCombatEnd(playerWon, knight1.EndCombatMessage());

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