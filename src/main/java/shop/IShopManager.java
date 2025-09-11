package shop;

import personnage.Ally;
import java.io.IOException;

public interface IShopManager {


    void showShopMenu() throws IOException, InterruptedException;

    void showAllyShop() throws IOException, InterruptedException;
    void showItemShop() throws IOException, InterruptedException;
    Ally[] createAvailableAllies();
    int calculateAllyPrice(Ally ally);
    void buyAlly(Ally ally) throws IOException, InterruptedException;
    void buyItem(String[] itemData) throws IOException, InterruptedException;
    void showPlayerStats() throws IOException;

}