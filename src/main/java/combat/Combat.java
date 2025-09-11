package combat;

// Ajoutez ces imports en haut de votre classe Combat.java
import personnage.Ally;
import stats.StatCombat;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.Random;

import personnage.Chevalier;
import stats.Stat;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 * Classe responsable de la gestion des combats
 * Centralise toute la logique de combat (tours, tournois, etc.)
 */
public class Combat {
    private TerminalUI ui;

    /**
     * Constructeur
     */
    public Combat(TerminalUI ui) {
        this.ui = ui;
    }

    /**
     * Détermine qui attaque en premier selon la vitesse
     */
    private void determineFirstAttacker(Chevalier knight1, Chevalier knight2) {
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
    private boolean hasAliveEnemies(Chevalier[] enemies) {
        for (Chevalier enemy : enemies) {
            if (enemy.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    /**
     * Gère le tour de combat d'un joueur
     * @return true si le combat continue, false si le joueur fuit
     */
    private boolean handlePlayerTurn(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        ui.printColoredLine("🗡 Tour de " + player.getName(), TextColor.ANSI.CYAN);

        String[] options = {"Attaquer", "Fuir"};
        int choice = ui.showMenu(options, "Que voulez-vous faire ?");

        if (choice == 1) { // Attaquer
            ui.animatedText("⚔ " + player.getName() + " attaque !", TextColor.ANSI.YELLOW, 30);
            player.performAttack(enemy);

            if (enemy.getStat(Stat.HP) > 0) {
                ui.printColoredText("💔 ", TextColor.ANSI.RED);
                ui.printLine(enemy.getName() + " a maintenant " + enemy.getStat(Stat.HP) + " HP");
            } else {
                ui.animatedText("💀 " + enemy.getName() + " est vaincu !", TextColor.ANSI.GREEN, 50);
            }
            return true; // Continue le combat

        } else if (choice == 2) { // Fuir
            ui.animatedText("🏃 " + player.getName() + " prend la fuite !", TextColor.ANSI.YELLOW, 50);
            return false; // Fuit le combat
        }

        return true;
    }

    /**
     * Gère le tour de combat d'un ennemi avec système de dodge interactif
     */
    private void handleEnemyTurn(Chevalier enemy, Chevalier player) throws IOException, InterruptedException {
        ui.displayCombatStatus(player, enemy);

        ui.printColoredLine("💀 Tour de " + enemy.getName(), TextColor.ANSI.RED);
        ui.printColoredLine("L'ennemi réfléchit...", TextColor.ANSI.RED);
        Thread.sleep(1500);

        // === SYSTÈME DE DODGE INTERACTIF ===
        boolean playerDodged = ui.showDodgeQTE(); // ou showDodgeQTEWithRandomSymbol()

        if (playerDodged) {
            // Le joueur a esquivé avec succès
            ui.animatedText("🌟 " + player.getName() + " esquive brillamment l'attaque de " + enemy.getName() + " !",
                    TextColor.ANSI.CYAN, 30);

            // Ajouter des stats de combat pour l'esquive réussie
            if (player instanceof Ally) {
                ((Ally) player).addStatsCombat(StatCombat.HITS_DODGED, 1);
            }

        } else {
            // Le joueur n'a pas réussi à esquiver, il reçoit l'attaque
            ui.animatedText("💥 " + enemy.getName() + " attaque !", TextColor.ANSI.RED, 30);
            enemy.performAttack(player);

            if (player.getStat(Stat.HP) > 0) {
                ui.printColoredText("💔 ", TextColor.ANSI.RED);
                ui.printLine(player.getName() + " a maintenant " + player.getStat(Stat.HP) + " HP");
            } else {
                ui.animatedText("💀 " + player.getName() + " est vaincu !", TextColor.ANSI.RED, 50);
            }
        }
    }

    /**
     * Gère un combat individuel entre le joueur et un ennemi
     * @return true si le joueur a gagné, false s'il a perdu ou fui
     */
    private boolean handleSingleCombat(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        // Déterminer qui commence
        determineFirstAttacker(player, enemy);

        ui.printLine("");
        ui.animatedText("🎯 Adversaire: " + enemy.getName(), TextColor.ANSI.RED, 30);
        Thread.sleep(1500);

        // Boucle de combat principal
        while (player.getStat(Stat.HP) > 0 && enemy.getStat(Stat.HP) > 0) {
            ui.displayCombatStatus(player, enemy);

            // Tour du joueur
            if (player.isStart()) {
                boolean continuesCombat = handlePlayerTurn(player, enemy);
                if (!continuesCombat) {
                    return false; // Joueur a fui
                }
                Thread.sleep(2000);
            }

            // Tour de l'ennemi (si il est encore vivant)
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

    /**
     * Lance le mode tournoi (combat contre plusieurs ennemis)
     * @param player Le joueur
     * @param enemies Tableau des ennemis à affronter
     */
    public void startTournament(Chevalier player, Chevalier[] enemies) throws IOException, InterruptedException {
        // Affichage du héros du joueur
        ui.showPlayerHeroSection(player);

        // Affichage des adversaires
        ui.showAdversariesSection(enemies);

        // Début du combat
        ui.showCombatStart();

        // === BOUCLE DE COMBAT PRINCIPALE ===
        boolean playerAlive = true;

        for (int i = 0; i < enemies.length && playerAlive; i++) {
            boolean wonFight = handleSingleCombat(player, enemies[i]);

            if (!wonFight) {
                playerAlive = false;
                break;
            }

            if (player.getStat(Stat.HP) <= 0) {
                playerAlive = false;
                break;
            }
        }

        // === FIN DU TOURNOI ===
        boolean playerWon = player.getStat(Stat.HP) > 0 && playerAlive;
        if (playerWon) {
            ui.showCombatEnd(true, "🏆 Vous avez remporté le tournoi ! " + player.EndCombatMessage());
        } else {
            ui.showCombatEnd(false, "💀 Vous avez été éliminé du tournoi... " + player.EndCombatMessage());
        }
    }

    /**
     * Lance un combat simple entre le joueur et un ennemi
     * @param player Le joueur
     * @param enemy L'ennemi à affronter
     * @return true si le joueur a gagné, false sinon
     */
    public boolean startSingleCombat(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        ui.showCombatStart();
        boolean result = handleSingleCombat(player, enemy);

        String endMessage = result ?
                "🏆 Victoire ! " + player.EndCombatMessage() :
                "💀 Défaite... " + player.EndCombatMessage();

        ui.showCombatEnd(result, endMessage);
        return result;
    }
}