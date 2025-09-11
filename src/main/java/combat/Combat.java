package combat;

import personnage.Ally;
import personnage.Personnage;
import stats.StatCombat;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.Random;
import java.util.List;

import personnage.Chevalier;
import stats.Stat;
import ui.TerminalUI;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 * Classe responsable de la gestion des combats en équipe
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
    private void determineFirstAttacker(Personnage player, Personnage ennemy) {
        if (player.getStat(Stat.SPEED) >= ennemy.getStat(Stat.SPEED)) {
            player.setStart(true);
            ennemy.setStart(false);
        } else {
            ennemy.setStart(true);
            player.setStart(false);
        }
    }

    /**
     * Trouve le prochain allié vivant dans l'équipe
     */
    private Ally getNextAliveAlly(List<Ally> team, int currentIndex) {
        for (int i = currentIndex + 1; i < team.size(); i++) {
            if (team.get(i).getStat(Stat.HP) > 0) {
                return team.get(i);
            }
        }
        return null; // Aucun allié vivant trouvé
    }

    /**
     * Vérifie s'il reste des alliés vivants dans l'équipe
     */
    private boolean hasAliveAllies(List<Ally> team) {
        for (Ally ally : team) {
            if (ally.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    /**
     * Vérifie s'il reste des ennemis vivants
     */
    private boolean hasAliveEnemies(Personnage[] enemies) {
        for (Personnage enemy : enemies) {
            if (enemy.getStat(Stat.HP) > 0) return true;
        }
        return false;
    }

    /**
     * Trouve le prochain ennemi vivant
     */
    private Personnage getNextAliveEnemy(Personnage[] enemies, int currentIndex) {
        for (int i = currentIndex + 1; i < enemies.length; i++) {
            if (enemies[i].getStat(Stat.HP) > 0) {
                return enemies[i];
            }
        }
        return null; // Aucun ennemi vivant trouvé
    }

    /**
     * Gère le changement d'allié quand l'actuel tombe
     */
    private Ally handleAllySwitch(List<Ally> team, int currentIndex) throws IOException, InterruptedException {
        Ally nextAlly = getNextAliveAlly(team, currentIndex);

        if (nextAlly != null) {
            ui.animatedText("🔄 " + nextAlly.getName() + " entre en combat !", TextColor.ANSI.CYAN, 50);
            Thread.sleep(1500);

            // Afficher les stats du nouvel allié
            ui.printColoredLine("Stats de " + nextAlly.getName() + ":", TextColor.ANSI.GREEN);
            ui.printColoredLine("❤️ HP: " + nextAlly.getStat(Stat.HP), TextColor.ANSI.RED);
            ui.printColoredLine("⚔️ ATK: " + nextAlly.getStat(Stat.ATTAQUE), TextColor.ANSI.YELLOW);
            ui.printColoredLine("🛡️ DEF: " + nextAlly.getStat(Stat.DEFENSE), TextColor.ANSI.BLUE);
            Thread.sleep(2000);
        }

        return nextAlly;
    }

    /**
     * Gère le tour de combat d'un joueur
     * @return true si le combat continue, false si le joueur fuit
     */
    private boolean handlePlayerTurn(Ally player, Personnage enemy) throws IOException, InterruptedException {
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

                // Ajouter de l'expérience au joueur actuel
                int expGained = 25 + (enemy.getLevel() * 5); // Plus l'ennemi est fort, plus d'exp
                player.addExperience(expGained);
                ui.printColoredLine("✨ " + player.getName() + " gagne " + expGained + " points d'expérience!", TextColor.ANSI.MAGENTA);
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
    private void handleEnemyTurn(Personnage enemy, Ally player) throws IOException, InterruptedException {
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
            player.addStatsCombat(StatCombat.HITS_DODGED, 1);

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
     * Gère un combat entre l'équipe du joueur et un ennemi
     * @return true si l'équipe a gagné, false si elle a perdu ou fui
     */
    private boolean handleTeamVsEnemyCombat(List<Ally> team, Personnage enemy) throws IOException, InterruptedException {
        if (team.isEmpty() || !hasAliveAllies(team)) {
            return false; // Pas d'alliés vivants
        }

        // Commencer avec le premier allié vivant
        int currentAllyIndex = 0;
        Ally currentAlly = team.get(currentAllyIndex);

        // Trouver le premier allié vivant
        while (currentAlly.getStat(Stat.HP) <= 0 && currentAllyIndex < team.size() - 1) {
            currentAllyIndex++;
            currentAlly = team.get(currentAllyIndex);
        }

        // Déterminer qui commence
        determineFirstAttacker(currentAlly, enemy);

        ui.printLine("");
        ui.animatedText("🎯 Adversaire: " + enemy.getName(), TextColor.ANSI.RED, 30);
        Thread.sleep(1500);

        // Variable pour suivre le tour actuel
        boolean playerTurn = currentAlly.isStart();

        // Boucle de combat principal
        while (hasAliveAllies(team) && enemy.getStat(Stat.HP) > 0) {
            ui.displayCombatStatus(currentAlly, enemy);

            if (playerTurn) {
                // Tour du joueur
                boolean continuesCombat = handlePlayerTurn(currentAlly, enemy);
                if (!continuesCombat) {
                    return false; // Joueur a fui
                }

                // Vérifier si l'allié actuel est mort et le remplacer si nécessaire
                if (currentAlly.getStat(Stat.HP) <= 0 && hasAliveAllies(team)) {
                    // Chercher le prochain allié vivant
                    Ally nextAlly = getNextAliveAlly(team, currentAllyIndex);
                    if (nextAlly != null) {
                        currentAllyIndex = team.indexOf(nextAlly);
                        currentAlly = nextAlly;
                        currentAlly = handleAllySwitch(team, team.indexOf(currentAlly) - 1);
                        if (currentAlly == null) break; // Plus d'alliés
                        currentAllyIndex = team.indexOf(currentAlly);
                    } else {
                        break; // Plus d'alliés vivants
                    }
                }

                Thread.sleep(2000);
            } else {
                // Tour de l'ennemi (attaque l'allié actuel)
                if (currentAlly.getStat(Stat.HP) > 0) {
                    handleEnemyTurn(enemy, currentAlly);

                    // Vérifier si l'allié actuel est mort après l'attaque
                    if (currentAlly.getStat(Stat.HP) <= 0 && hasAliveAllies(team)) {
                        Ally nextAlly = getNextAliveAlly(team, currentAllyIndex);
                        if (nextAlly != null) {
                            currentAllyIndex = team.indexOf(nextAlly);
                            currentAlly = nextAlly;
                            currentAlly = handleAllySwitch(team, currentAllyIndex - 1);
                            if (currentAlly == null) break;
                            currentAllyIndex = team.indexOf(currentAlly);
                        }
                    }
                }
                Thread.sleep(2000);
            }

            // Alternance des tours (seulement si les deux camps ont encore des combattants vivants)
            if (hasAliveAllies(team) && enemy.getStat(Stat.HP) > 0) {
                playerTurn = !playerTurn;
            }
        }

        return hasAliveAllies(team) && enemy.getStat(Stat.HP) <= 0; // true si l'équipe a gagné
    }

    /**
     * Lance le mode tournoi avec une équipe contre plusieurs ennemis
     * @param team L'équipe du joueur
     * @param enemies Tableau des ennemis à affronter
     */
    public boolean startTeamTournament(List<Ally> team, Personnage[] enemies) throws IOException, InterruptedException {
        if (team.isEmpty()) {
            ui.showCombatEnd(false, "❌ Aucune équipe disponible pour le combat !");
            return false;
        }

        // Affichage de l'équipe du joueur
        ui.clearScreen();
        ui.printColoredLine("👥 VOTRE ÉQUIPE", TextColor.ANSI.CYAN);
        for (Ally ally : team) {
            ui.printColoredLine("⚔️ " + ally.getName() + " (" + ally.getClass().getSimpleName() + ") - HP: " + ally.getStat(Stat.HP), TextColor.ANSI.GREEN);
        }
        Thread.sleep(2000);

        // Affichage des adversaires
        ui.showAdversariesSection(enemies);

        // Début du combat
        ui.showCombatStart();

        boolean teamAlive = true;
        int enemyIndex = 0;

        while (enemyIndex < enemies.length && teamAlive && hasAliveAllies(team)) {
            Personnage currentEnemy = enemies[enemyIndex];

            if (currentEnemy.getStat(Stat.HP) > 0) {
                boolean wonFight = handleTeamVsEnemyCombat(team, currentEnemy);

                if (!wonFight) {
                    teamAlive = false;
                    break;
                }

                if (currentEnemy.getStat(Stat.HP) <= 0) {
                    enemyIndex++;
                    ui.animatedText("🏆 Ennemi vaincu ! Prochain adversaire...", TextColor.ANSI.GREEN, 50);
                    Thread.sleep(2000);
                }
            } else {
                enemyIndex++;
            }

            if (!hasAliveAllies(team)) {
                teamAlive = false;
                break;
            }
        }

        // === FIN DU TOURNOI ===
        boolean teamWon = hasAliveAllies(team) && teamAlive;

        if (teamWon) {
            int survivors = 0;
            for (Ally ally : team) {
                if (ally.getStat(Stat.HP) > 0) survivors++;
            }

            ui.showCombatEnd(true, "🏆 Votre équipe a remporté le tournoi ! " + survivors + " survivant(s) !");
            for (Ally ally : team) {
                if (ally.getStat(Stat.HP) > 0) {
                    ally.addExperience(50); // Bonus de tournoi
                }
            }
        } else {
            ui.showCombatEnd(false, "💀 Votre équipe a été éliminée du tournoi...");
        }

        return teamWon; // ← IMPORTANT : retourne le résultat final
    }


    /**
     * Lance un combat simple entre une équipe et un ennemi
     * @param team L'équipe du joueur
     * @param enemy L'ennemi à affronter
     * @return true si l'équipe a gagné, false sinon
     */
    public boolean startTeamVsEnemyCombat(List<Ally> team, Personnage enemy) throws IOException, InterruptedException {
        ui.showCombatStart();
        boolean result = handleTeamVsEnemyCombat(team, enemy);

        int survivors = 0;
        for (Ally ally : team) {
            if (ally.getStat(Stat.HP) > 0) survivors++;
        }

        String endMessage = result ?
                "🏆 Victoire ! " + survivors + " survivant(s) dans votre équipe !" :
                "💀 Défaite... Votre équipe a été vaincue.";

        ui.showCombatEnd(result, endMessage);
        return result;
    }

    // Maintenir la compatibilité avec l'ancien système pour les combats solo
    public boolean startSingleCombat(Chevalier player, Chevalier enemy) throws IOException, InterruptedException {
        ui.showCombatStart();

        // Convertir en combat d'équipe avec un seul membre si c'est un Ally
        if (player instanceof Ally) {
            List<Ally> soloTeam = List.of((Ally) player);
            boolean result = handleTeamVsEnemyCombat(soloTeam, enemy);

            String endMessage = result ?
                    "🏆 Victoire ! " + player.EndCombatMessage() :
                    "💀 Défaite... " + player.EndCombatMessage();

            ui.showCombatEnd(result, endMessage);
            return result;
        }

        // Fallback pour les anciens combats (ne devrait pas arriver avec le nouveau système)
        return false;
    }
}