package team;

import personnage.Ally;

import java.io.IOException;
import java.util.List;

public interface ITeamManager {
    void addToCollection(Ally personnage);

    boolean addToTeam(Ally personnage);

    void removeFromTeam(Ally personnage);

    boolean isTeamFull();

    int getCollectionSize();

    List<Ally> getCollection();

    List<Ally> getActiveTeam();

    void showTeamManagementMenu() throws IOException, InterruptedException;

    void displayTeamOverview() throws IOException;

    void showActiveTeam() throws IOException;

    void showCollection() throws IOException;

    void addPersonnageToTeam() throws IOException;

    void removePersonnageFromTeam() throws IOException;
}