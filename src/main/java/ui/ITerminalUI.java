package ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import personnage.Personnage;

import java.io.IOException;

public interface ITerminalUI {
    void initialize() throws IOException;

    void showNotImplemented(String feature) throws IOException;

    int showMainMenu() throws IOException;

    void cleanup() throws IOException;

    void clearScreen() throws IOException;

    void printColoredText(String text, TextColor color) throws IOException;

    void printLine(String text) throws IOException;

    void printColoredLine(String text, TextColor color) throws IOException;
    KeyStroke waitForKeyPress() throws IOException;
    int showMenu(String[] options, String prompt) throws IOException;
    void clearInputBuffer() throws IOException;
    void displayCharacterStats(Personnage character, TextColor nameColor) throws IOException;
    void animatedText(String text, TextColor color, int delayMs) throws IOException, InterruptedException;
    void displayCombatStatus(Personnage player, Personnage enemy) throws IOException;
    void showTitleScreen() throws IOException, InterruptedException;
    void showCombatStart() throws IOException, InterruptedException;
    void showCombatEnd(boolean playerWon, String endMessage) throws IOException, InterruptedException;

    void showAdversariesSection(Personnage[] enemies) throws IOException;
    void showPlayerHeroSection(Personnage player) throws IOException;
    boolean showDodgeQTE() throws IOException, InterruptedException;
    String readLine() throws IOException;

    }





