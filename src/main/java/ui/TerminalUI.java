package ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import personnage.Chevalier;
import personnage.Personnage;
import stats.Stat;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
     * Affiche un message pour les fonctionnalités non implémentées
     */
    public void showNotImplemented(String feature) throws IOException {
        clearScreen();
        printColoredLine("╔════════════════════════════════════════════════╗", TextColor.ANSI.YELLOW);
        printColoredLine("║              🚧 EN CONSTRUCTION 🚧             ║", TextColor.ANSI.YELLOW);
        printColoredLine("╚════════════════════════════════════════════════╝", TextColor.ANSI.YELLOW);
        printLine("");
        printColoredLine("La fonctionnalité \"" + feature + "\" n'est pas encore implémentée.", TextColor.ANSI.YELLOW);
        printLine("");
        printColoredLine("Appuyez sur une touche pour revenir au menu principal...", TextColor.ANSI.CYAN);
        try {
            waitForKeyPress();
        } catch (IOException e) {
            // Ignorer l'erreur
        }
    }

    /**
     * Affiche le menu principal du jeu et retourne le choix de l'utilisateur
     */
    /**
     * Affiche le menu principal du jeu et retourne le choix de l'utilisateur
     */
    public int showMainMenu() throws IOException {
        clearScreen();

        // Titre du menu principal
        printLine("");
        printColoredLine("╔══════════════════════════════════════╗", TextColor.ANSI.CYAN);
        printColoredLine("║           MENU PRINCIPAL             ║", TextColor.ANSI.CYAN);
        printColoredLine("╚══════════════════════════════════════╝", TextColor.ANSI.CYAN);
        printLine("");

        // Options du menu
        String[] menuOptions = {
                "🏆 Rejoindre le tournoi",
                "👥 Équipe",
                "🎨 Personnalisation",
                "🛒 Boutique",
                "🚪 Quitter le jeu"
        };

        // Affichage des options
        for (int i = 0; i < menuOptions.length; i++) {
            printColoredText((i + 1) + " - ", TextColor.ANSI.YELLOW);
            printLine(menuOptions[i]);
        }

        printLine("");
        printColoredText("Votre choix (1-5) : ", TextColor.ANSI.GREEN);

        // Vider le buffer des touches en attente avant de lire le choix
        clearInputBuffer();

        // Lecture du choix utilisateur
        while (true) {
            KeyStroke key = waitForKeyPress();
            if (key.getKeyType() == KeyType.Character) {
                char c = key.getCharacter();
                if (c >= '1' && c <= '5') {
                    terminal.putCharacter(c);
                    terminal.putCharacter('\n');
                    terminal.flush();
                    return c - '0'; // Convertit le caractère en nombre
                }
            }
            // Si choix invalide, on continue la boucle
            printColoredText("\nChoix invalide. Choisissez entre 1 et 5 : ", TextColor.ANSI.RED);
        }
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
    /**
     * Affiche un menu et retourne le choix de l'utilisateur
     */
    public int showMenu(String[] options, String prompt) throws IOException {
        printColoredLine(prompt, TextColor.ANSI.CYAN);

        for (int i = 0; i < options.length; i++) {
            printLine((i + 1) + " - " + options[i]);
        }

        printColoredText("Votre choix : ", TextColor.ANSI.YELLOW);

        // Vider le buffer avant de lire le choix
        clearInputBuffer();

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
            // Si choix invalide, on continue la boucle sans message d'erreur
            // car cela pourrait créer de la confusion dans l'interface
        }
    }

    /**
     * Vide le buffer des touches en attente pour éviter les saisies indésirables
     */
    private void clearInputBuffer() throws IOException {
        // Continue à lire tant qu'il y a des touches en attente
        while (terminal.pollInput() != null) {
            // Vide le buffer en lisant toutes les touches en attente
        }
    }
    /**
     * Affiche les statistiques d'un personnage de manière stylisée
     */
    public void displayCharacterStats(Personnage character, TextColor nameColor) throws IOException {
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
    public void displayCombatStatus(Personnage player, Personnage enemy) throws IOException {
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
    public void showAdversariesSection(Personnage[] enemies) throws IOException {
        clearScreen();
        printColoredLine("💀 VOS ADVERSAIRES 💀", TextColor.ANSI.RED);
        printLine("");

        for (Personnage ennemi : enemies) {
            displayCharacterStats(ennemi, TextColor.ANSI.RED);
        }

        printColoredLine("Appuyez sur une touche pour commencer le combat...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }

    /**
     * Affiche la section du héros du joueur
     */
    public void showPlayerHeroSection(Personnage player) throws IOException {
        clearScreen();
        displayCharacterStats(player, TextColor.ANSI.CYAN);
        printColoredLine("Appuyez sur une touche pour voir vos adversaires...", TextColor.ANSI.CYAN);
        waitForKeyPress();
    }

    // Ajoutez ces méthodes à votre classe TerminalUI


    /**
     * Affiche un QTE (Quick Time Event) de dodge et retourne si le joueur a réussi
     * @return true si le joueur a réussi à esquiver, false sinon
     */
    public boolean showDodgeQTE() throws IOException, InterruptedException {
        // Délai aléatoire avant l'apparition du bouton (1-3 secondes)
        int randomDelay = ThreadLocalRandom.current().nextInt(1000, 3001);

        // Afficher le message d'attaque en cours
        printColoredLine("⚔️ L'ennemi lance son attaque...", TextColor.ANSI.RED);
        printColoredLine("🛡️ Préparez-vous à esquiver !", TextColor.ANSI.YELLOW);

        // Attendre le délai aléatoire
        Thread.sleep(randomDelay);

        // Vider le buffer avant le QTE
        clearInputBuffer();

        // Afficher le symbole de dodge
        clearScreen();
        printLine("");
        printLine("");
        printColoredLine("           ╔═══════════════════════════╗", TextColor.ANSI.GREEN);
        printColoredLine("           ║                           ║", TextColor.ANSI.GREEN);
        printColoredLine("           ║      ❌ APPUYEZ SUR X ❌    ║", TextColor.ANSI.GREEN);
        printColoredLine("           ║                           ║", TextColor.ANSI.GREEN);
        printColoredLine("           ╚═══════════════════════════╝", TextColor.ANSI.GREEN);
        terminal.flush();

        // Chronométrer la réaction (200ms = 0.2sec)
        long startTime = System.currentTimeMillis();
        long timeLimit = 300; // 300 millisecondes

        boolean dodgeSuccess = false;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            KeyStroke key = terminal.pollInput();
            if (key != null && key.getKeyType() == KeyType.Character) {
                char c = Character.toLowerCase(key.getCharacter());
                if (c == 'x') {
                    dodgeSuccess = true;
                    break;
                }
            }
            // Petite pause pour éviter une boucle trop intensive
            Thread.sleep(1);
        }

        // Afficher le résultat
        clearScreen();
        if (dodgeSuccess) {
            animatedText("🎯 ESQUIVE RÉUSSIE ! 🎯", TextColor.ANSI.GREEN, 30);
            printColoredLine("Vous avez évité l'attaque avec brio !", TextColor.ANSI.GREEN);
        } else {
            animatedText("💥 ESQUIVE RATÉE ! 💥", TextColor.ANSI.RED, 30);
            printColoredLine("L'attaque vous touche de plein fouet !", TextColor.ANSI.RED);
        }

        Thread.sleep(1500);
        return dodgeSuccess;
    }

    /**
     * Version alternative avec différents symboles aléatoires
     */
    public boolean showDodgeQTEWithRandomSymbol() throws IOException, InterruptedException {
        // Symboles possibles avec leurs touches correspondantes
        String[] symbols = {"❌ X", "⭕ O", "🔵 B", "🔴 A"};
        char[] keys = {'x', 'o', 'b', 'a'};

        Random random = new Random();
        int symbolIndex = random.nextInt(symbols.length);

        // Délai aléatoire avant l'apparition du bouton (1-3 secondes)
        int randomDelay = ThreadLocalRandom.current().nextInt(1000, 3001);

        printColoredLine("⚔️ L'ennemi lance son attaque...", TextColor.ANSI.RED);
        printColoredLine("🛡️ Préparez-vous à esquiver !", TextColor.ANSI.YELLOW);

        Thread.sleep(randomDelay);
        clearInputBuffer();

        // Afficher le symbole choisi
        clearScreen();
        printLine("");
        printLine("");
        printColoredLine("           ╔═══════════════════════════╗", TextColor.ANSI.GREEN);
        printColoredLine("           ║                           ║", TextColor.ANSI.GREEN);
        printColoredLine("           ║     " + symbols[symbolIndex] + " VITE !      ║", TextColor.ANSI.GREEN);
        printColoredLine("           ║                           ║", TextColor.ANSI.GREEN);
        printColoredLine("           ╚═══════════════════════════╝", TextColor.ANSI.GREEN);
        terminal.flush();

        long startTime = System.currentTimeMillis();
        long timeLimit = 350;
        boolean dodgeSuccess = false;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            KeyStroke key = terminal.pollInput();
            if (key != null && key.getKeyType() == KeyType.Character) {
                char c = Character.toLowerCase(key.getCharacter());
                if (c == keys[symbolIndex]) {
                    dodgeSuccess = true;
                    break;
                }
            }
            Thread.sleep(1);
        }

        clearScreen();
        if (dodgeSuccess) {
            animatedText("🎯 ESQUIVE RÉUSSIE ! 🎯", TextColor.ANSI.GREEN, 30);
            printColoredLine("Réflexes exceptionnels !", TextColor.ANSI.GREEN);
        } else {
            animatedText("💥 ESQUIVE RATÉE ! 💥", TextColor.ANSI.RED, 30);
            printColoredLine("Trop lent ! L'attaque vous atteint !", TextColor.ANSI.RED);
        }

        Thread.sleep(1500);
        return dodgeSuccess;
    }

    /**
     * Permet à l'utilisateur de saisir une ligne de texte
     */
    public String readLine() throws IOException {
        StringBuilder input = new StringBuilder();
        KeyStroke keyStroke;

        // Position initiale du curseur
        int x = 0;
        int y = terminal.getCursorPosition().getRow();

        while (true) {
            keyStroke = terminal.readInput();

            if (keyStroke.getKeyType() == KeyType.Enter) {
                break; // Fin de la saisie
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                    // Effacer le dernier caractère à l'écran
                    terminal.setCursorPosition(x + input.length(), y);
                    terminal.putCharacter(' ');
                    terminal.setCursorPosition(x + input.length(), y);
                    terminal.flush();
                }
            } else if (keyStroke.getCharacter() != null) {
                input.append(keyStroke.getCharacter());
                terminal.putCharacter(keyStroke.getCharacter());
                terminal.flush();
            }
        }

        // Aller à la ligne après validation
        terminal.putCharacter('\n');
        terminal.flush();

        return input.toString();
    }

}