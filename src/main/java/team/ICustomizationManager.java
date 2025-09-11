package team;

import personnage.Ally;
import stats.Stat;

import java.io.IOException;

public interface ICustomizationManager {

    void showCustomizationMenu() throws IOException, InterruptedException;
    void displayCustomizationOverview() throws IOException;
    void renameMainCharacter() throws IOException;
    void customizeStats() throws IOException;
    void customizeCharacterStats(Ally character) throws IOException;
    void modifyStat(Ally character, Stat stat) throws IOException;
    void showGlobalInventory() throws IOException;
    void showCharacterDetails() throws IOException;
    Ally findMainCharacter();
}
