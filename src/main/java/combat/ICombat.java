
package combat;

import personnage.Ally;
import personnage.Personnage;
import personnage.Knight;
import inventaire.GlobalInventory;
import java.util.List;
import java.io.IOException;

/**
 * Interface pour la gestion des combats
 * Définit les différents types de combat disponibles
 */
public interface ICombat {

    // ========== GESTION DE L'INVENTAIRE ==========

    /**
     * Définir l'inventaire global utilisé en combat
     */
    void setGlobalInventory(GlobalInventory globalInventory);

    // ========== COMBATS D'ÉQUIPE ==========

    /**
     * Lance un tournoi avec une équipe contre plusieurs ennemis
     * @param team L'équipe du joueur
     * @param enemies Tableau des ennemis à affronter
     * @return true si l'équipe a remporté le tournoi, false sinon
     */
    boolean startTeamTournament(List<Ally> team, Personnage[] enemies) throws IOException, InterruptedException;

    /**
     * Lance un combat entre une équipe et un ennemi unique
     * @param team L'équipe du joueur
     * @param enemy L'ennemi à affronter
     * @return true si l'équipe a gagné, false sinon
     */
    boolean startTeamVsEnemyCombat(List<Ally> team, Personnage enemy) throws IOException, InterruptedException;

    // ========== COMBAT SOLO (COMPATIBILITÉ) ==========

    /**
     * Lance un combat solo (pour compatibilité avec l'ancien système)
     * @param player Le joueur
     * @param enemy L'ennemi
     * @return true si le joueur a gagné, false sinon
     */
    boolean startSingleCombat(Knight player, Knight enemy) throws IOException, InterruptedException;
}