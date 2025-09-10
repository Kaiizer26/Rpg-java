import personnage.Chevalier;
import personnage.Ennemi;
import personnage.Gobelin;
import personnage.Personnage;
import stats.Stat;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class rpg_main {
    private static Terminal terminal;

    public static void firstToAttack(Chevalier knight1, Chevalier knight2) {
        if (knight1.getStat(Stat.SPEED) >= knight2.getStat(Stat.SPEED)) {
            knight1.setStart(true);
            knight2.setStart(false);
        } else {
            knight2.setStart(true);
            knight1.setStart(false);
        }
    }

    public static boolean hasAliveEnemies(Chevalier[] enemies) {
        for (Chevalier enemy : enemies) {
            if (enemy.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    // Méthode pour initialiser le terminal
    private static void initializeTerminal() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminal = terminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorVisible(false);
    }

    // Méthode pour nettoyer et fermer le terminal
    private static void cleanupTerminal() throws IOException {
        terminal.exitPrivateMode();
        terminal.close();
    }

    // Méthode pour effacer l'écran et repositionner le curseur
    private static void clearScreen() throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(0, 0);
    }

    // Méthode pour afficher du texte avec couleur
    private static void printColoredText(String text, TextColor color) throws IOException {
        terminal.setForegroundColor(color);
        for (char c : text.toCharArray()) {
            terminal.putCharacter(c);
        }
        terminal.resetColorAndSGR();
        terminal.flush();
    }

    // Méthode pour afficher du texte et aller à la ligne
    private static void printLine(String text) throws IOException {
        printColoredText(text, TextColor.ANSI.WHITE);
        terminal.putCharacter('\n');
        terminal.flush();
    }

    // Méthode pour afficher du texte coloré et aller à la ligne
    private static void printColoredLine(String text, TextColor color) throws IOException {
        printColoredText(text, color);
        terminal.putCharacter('\n');
        terminal.flush();
    }

    // Méthode pour attendre une entrée utilisateur (touche)
    private static KeyStroke waitForKeyPress() throws IOException {
        terminal.flush();
        return terminal.readInput();
    }

    // Méthode pour afficher un menu et récupérer le choix
    private static int showMenu(String[] options, String prompt) throws IOException {
        printColoredLine(prompt, TextColor.ANSI.CYAN);

        for (int i = 0; i < options.length; i++) {
            printLine((i + 1) + " - " + options[i]);
        }

        printColoredText("Votre choix : ", TextColor.ANSI.YELLOW);

        while (true) {
            KeyStroke key = waitForKeyPress();
            if (key.getKeyType() == KeyType.Character) {
                char c = key.getCharacter();
                if (c >= '1' && c <= '0' + options.length) {
                    terminal.putCharacter(c);
                    terminal.putCharacter('\n');
                    terminal.flush();
                    return c - '0';
                }
            }
        }
    }

    // Méthode pour afficher les stats d'un personnage avec style
    private static void displayCharacterStats(Chevalier character, TextColor nameColor) throws IOException {
        printColoredLine("═══════════════════════════════════════", TextColor.ANSI.BLUE);
        printColoredText("⚔ ", TextColor.ANSI.YELLOW);
        printColoredLine(character.getName().toUpperCase(), nameColor);
        printColoredLine("═══════════════════════════════════════", TextColor.ANSI.BLUE);

        printColoredText("❤ HP: ", TextColor.ANSI.RED);
        printLine(character.getStat(Stat.HP) + "");

        printColoredText("⚔ ATK: ", TextColor.ANSI.RED);
        printLine(character.getStat(Stat.ATTAQUE) + "");

        printColoredText("🛡 DEF: ", TextColor.ANSI.BLUE);
        printLine(character.getStat(Stat.DEFENSE) + "");

        printColoredText("💨 SPD: ", TextColor.ANSI.GREEN);
        printLine(character.getStat(Stat.SPEED) + "");

        printColoredText("🍀 LUCK: ", TextColor.ANSI.MAGENTA);
        printLine(character.getStat(Stat.LUCK) + "");

        printLine("");
    }

    // Animation de texte avec délai
    private static void animatedText(String text, TextColor color, int delayMs) throws IOException, InterruptedException {
        terminal.setForegroundColor(color);
        for (char c : text.toCharArray()) {
            terminal.putCharacter(c);
            terminal.flush();
            Thread.sleep(delayMs);
        }
        terminal.resetColorAndSGR();
        terminal.putCharacter('\n');
        terminal.flush();
    }

    // Méthode pour afficher l'état du combat
    private static void displayCombatStatus(Chevalier player, Chevalier enemy) throws IOException {
        clearScreen();

        printColoredLine("⚔═══════════ COMBAT ═══════════⚔", TextColor.ANSI.RED);
        printLine("");

        // Affichage du joueur (à gauche)
        printColoredText("🛡 ", TextColor.ANSI.BLUE);
        printColoredText(player.getName() + " ", TextColor.ANSI.CYAN);
        printColoredText("❤" + player.getStat(Stat.HP), TextColor.ANSI.RED);

        printColoredText("        VS        ", TextColor.ANSI.YELLOW);

        // Affichage de l'ennemi (à droite)
        printColoredText("💀 ", TextColor.ANSI.RED);
        printColoredText(enemy.getName() + " ", TextColor.ANSI.RED);
        printColoredText("❤" + enemy.getStat(Stat.HP), TextColor.ANSI.RED);

        printLine("");
        printLine("");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            // Initialisation de Lanterna
            initializeTerminal();

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
            Personnage[] persos = {new Ennemi("Joepoknoob77"), new Gobelin("Jordy"), new Chevalier("Kaiizerrr")};

            // Inventaire, collection
            List<Personnage> equipe = new ArrayList<>();
            equipe.add(new Chevalier("Maelle"));
            equipe.add(new Gobelin("Frantz"));

            knight1.addItem("Pistolet", 1);
            knight1.addItem("Arbre", 1);
            knight1.addItem("Zebre", 1);

            // Écran de titre
            clearScreen();
            animatedText("╔══════════════════════════════════════╗", TextColor.ANSI.MAGENTA, 20);
            animatedText("║          RPG TEXTUEL v1.0            ║", TextColor.ANSI.MAGENTA, 20);
            animatedText("║     Inspiré de Clair Obscur 33      ║", TextColor.ANSI.MAGENTA, 20);
            animatedText("╚══════════════════════════════════════╝", TextColor.ANSI.MAGENTA, 20);

            printLine("");
            printColoredLine("Appuyez sur une touche pour commencer...", TextColor.ANSI.CYAN);
            waitForKeyPress();

            clearScreen();

            // Affichage de l'équipe du joueur
//            printColoredLine("🏆 VOTRE HÉROS 🏆", TextColor.ANSI.GOLD);
            displayCharacterStats(knight1, TextColor.ANSI.CYAN);

            printColoredLine("Appuyez sur une touche pour voir vos adversaires...", TextColor.ANSI.CYAN);
            waitForKeyPress();

            clearScreen();

            // Affichage des ennemis
            printColoredLine("💀 VOS ADVERSAIRES 💀", TextColor.ANSI.RED);
            printLine("");

            for (Chevalier ennemi : knightTab) {
                displayCharacterStats(ennemi, TextColor.ANSI.RED);
            }

            printColoredLine("Appuyez sur une touche pour commencer le combat...", TextColor.ANSI.CYAN);
            waitForKeyPress();

            // Début du combat
            animatedText("⚔ DÉBUT DU COMBAT ! ⚔", TextColor.ANSI.RED, 50);
            Thread.sleep(1000);

            for (int i = 0; i < knightTab.length && knight1.getStat(Stat.HP) > 0; i++) {
                // Déterminer qui commence
                firstToAttack(knight1, knightTab[i]);

                printLine("");
                animatedText("🎯 Adversaire: " + knightTab[i].getName(), TextColor.ANSI.RED, 30);
                Thread.sleep(1500);

                while (knight1.getStat(Stat.HP) > 0 && knightTab[i].getStat(Stat.HP) > 0) {
                    displayCombatStatus(knight1, knightTab[i]);

                    if (knight1.isStart()) {
                        printColoredLine("🗡 Tour de " + knight1.getName(), TextColor.ANSI.CYAN);

                        String[] options = {"Attaquer", "Fuir"};
                        int choice = showMenu(options, "Que voulez-vous faire ?");

                        if (choice == 1) {
                            animatedText("⚔ " + knight1.getName() + " attaque !", TextColor.ANSI.YELLOW, 30);
                            knight1.performAttack(knightTab[i]);

                            if (knightTab[i].getStat(Stat.HP) > 0) {
                                printColoredText("💔 ", TextColor.ANSI.RED);
                                printLine(knightTab[i].getName() + " a maintenant " + knightTab[i].getStat(Stat.HP) + " HP");
                            } else {
                                animatedText("💀 " + knightTab[i].getName() + " est vaincu !", TextColor.ANSI.GREEN, 50);
                            }

                        } else if (choice == 2) {
                            animatedText("🏃 " + knight1.getName() + " prend la fuite !", TextColor.ANSI.YELLOW, 50);
                            break;
                        }

                        Thread.sleep(2000);
                    }

                    if (knightTab[i].getStat(Stat.HP) > 0) {
                        displayCombatStatus(knight1, knightTab[i]);

                        printColoredLine("💀 Tour de " + knightTab[i].getName(), TextColor.ANSI.RED);
                        printColoredLine("L'ennemi réfléchit...", TextColor.ANSI.RED);
                        Thread.sleep(1500);

                        animatedText("💥 " + knightTab[i].getName() + " attaque !", TextColor.ANSI.RED, 30);
                        knightTab[i].performAttack(knight1);

                        if (knight1.getStat(Stat.HP) > 0) {
                            printColoredText("💔 ", TextColor.ANSI.RED);
                            printLine(knight1.getName() + " a maintenant " + knight1.getStat(Stat.HP) + " HP");
                        } else {
                            animatedText("💀 " + knight1.getName() + " est vaincu !", TextColor.ANSI.RED, 50);
                        }

                        Thread.sleep(2000);
                    }

                    // Alternance des tours
                    boolean temp = knight1.isStart();
                    knight1.setStart(knightTab[i].isStart());
                    knightTab[i].setStart(temp);
                }

                if (knight1.getStat(Stat.HP) <= 0) {
                    break;
                }
            }

            // Fin du combat
            clearScreen();
            animatedText("⚔ FIN DU COMBAT ! ⚔", TextColor.ANSI.MAGENTA, 50);
            printLine("");

            if (knight1.getStat(Stat.HP) > 0) {
                printColoredLine("🏆 VICTOIRE ! 🏆", TextColor.ANSI.GREEN);
            } else {
                printColoredLine("💀 DÉFAITE... 💀", TextColor.ANSI.RED);
            }

            printLine("");
            printLine(knight1.EndCombatMessage());

            printLine("");
            printColoredLine("Appuyez sur une touche pour quitter...", TextColor.ANSI.CYAN);
            waitForKeyPress();

        } finally {
            // Nettoyage du terminal
            cleanupTerminal();
        }
    }
}