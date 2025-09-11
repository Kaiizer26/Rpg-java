package ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import personnage.Chevalier;
import stats.Stat;

import java.io.IOException;

/**
 * Classe responsable de l'interface utilisateur avec Lanterna
 * Centralise toutes les méthodes d'affichage et d'interaction
 */
public class TerminalUI {
    private Terminal terminal;

    /**
     * Initialise le terminal avec une police personnalisée
     */
    public void initialize() throws IOException {
        // Pour augmenter la font size (taille 24)
        java.awt.Font font = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 24);

        // Configuration de police pour SwingTerminal
        com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration fontConfig =
                com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration.newInstance(font);

        // Factory avec police custom
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                .setTerminalEmulatorFontConfiguration(fontConfig);

        terminal = terminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorVisible(false);
    }

    /**
     * Nettoie et ferme le terminal proprement
     */
    public void cleanup() throws IOException {
        if (terminal != null) {
            terminal.exitPrivateMode();
            terminal.close();
        }
    }

    /**
     * Efface l'écran et repositionne le curseur
     */
    public void clearScreen() throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(0, 0);
    }

    /**
     * Affiche du texte avec une couleur spécifiée
     */
    public void printColoredText(String text, TextColor color) throws IOException {
        terminal.setForegroundColor(color);
        for (char c : text.toCharArray()) {
            terminal.putCharacter(c);
        }
        terminal.resetColorAndSGR();
        terminal.flush();
    }

    /**
     * Affiche du texte blanc et va à la ligne
     */
    public void printLine(String text) throws IOException {
        printColoredText(text, TextColor.ANSI.WHITE);
        terminal.putCharacter('\n');
        terminal.flush();
    }

    /**
     * Affiche du texte coloré et va à la ligne
     */
    public void printColoredLine(String text, TextColor color) throws IOException {
        printColoredText(text, color);
        terminal.putCharacter('\n');
        terminal.flush();
    }

    /**
     * Attend une pression de touche de l'utilisateur
     */
    public KeyStroke waitForKeyPress() throws IOException {
        terminal.flush();
        return terminal.readInput();
    }

    /**
     * Affiche un menu et retourne le choix de l'utilisateur
     */
    public int showMenu(String[] options, String prompt) throws IOException {
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

    /**
     * Affiche les statistiques d'un personnage de manière stylisée
     */
    public void displayCharacterStats(Chevalier character, TextColor nameColor) throws IOException {
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

    /**
     * Affiche du texte avec animation caractère par caractère
     */
    public void animatedText(String text, TextColor color, int delayMs) throws IOException, InterruptedException {
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

    /**
     * Affiche l'état actuel du combat entre deux personnages
     */
    public void displayCombatStatus(Chevalier player, Chevalier enemy) throws IOException {
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

    /**
     * Affiche l'écran de titre du jeu
     */
    public void showTitleScreen() throws IOException, InterruptedException {
        clearScreen();
        animatedText("╔══════════════════════════════════════╗", TextColor.ANSI.MAGENTA, 20);
        animatedText("║              Tower 33                ║", TextColor.ANSI.MAGENTA, 20);
        animatedText("║      Inspiré de Clair Obscur 33      ║", TextColor.ANSI.MAGENTA, 20);
        animatedText("╚══════════════════════════════════════╝", TextColor.ANSI.MAGENTA, 20);

        printLine("");
        printColoredLine("Appuyez sur une touche pour commencer...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }

    /**
     * Affiche l'écran de début de combat
     */
    public void showCombatStart() throws IOException, InterruptedException {
        clearScreen();
        printLine("");
        printLine("");
        animatedText("    ╔════════════════════════════════════════════════════════════════╗", TextColor.ANSI.RED, 30);
        animatedText("    ║                  ⚔️ DÉBUT DU COMBAT ! ⚔️                       ║", TextColor.ANSI.RED, 30);
        animatedText("    ╚════════════════════════════════════════════════════════════════╝", TextColor.ANSI.RED, 30);
        Thread.sleep(1500);
    }

    /**
     * Affiche l'écran de fin de combat
     */
    public void showCombatEnd(boolean playerWon, String endMessage) throws IOException, InterruptedException {
        clearScreen();
        animatedText("⚔ FIN DU COMBAT ! ⚔", TextColor.ANSI.MAGENTA, 50);
        printLine("");

        if (playerWon) {
            printColoredLine("🏆 VICTOIRE ! 🏆", TextColor.ANSI.GREEN);
        } else {
            printColoredLine("💀 DÉFAITE... 💀", TextColor.ANSI.RED);
        }

        printLine("");
        printLine(endMessage);

        printLine("");
        printColoredLine("Appuyez sur une touche pour quitter...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }

    /**
     * Affiche la section des adversaires
     */
    public void showAdversariesSection(Chevalier[] enemies) throws IOException {
        clearScreen();
        printColoredLine("💀 VOS ADVERSAIRES 💀", TextColor.ANSI.RED);
        printLine("");

        for (Chevalier ennemi : enemies) {
            displayCharacterStats(ennemi, TextColor.ANSI.RED);
        }

        printColoredLine("Appuyez sur une touche pour commencer le combat...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }

    /**
     * Affiche la section du héros du joueur
     */
    public void showPlayerHeroSection(Chevalier player) throws IOException {
        clearScreen();
        displayCharacterStats(player, TextColor.ANSI.CYAN);
        printColoredLine("Appuyez sur une touche pour voir vos adversaires...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }
}