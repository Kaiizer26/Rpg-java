import personnage.Chevalier;
import personnage.Ennemi;
import personnage.Gobelin;
import personnage.Personnage;
import stats.Stat;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class rpg_main {
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

    public static void main(String[] args) throws InterruptedException {
        Scanner scan = new Scanner(System.in);
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

        //Inventaire, collection
        List<Personnage> equipe = new ArrayList<>();
        equipe.add(new Chevalier("Maelle"));
        equipe.add(new Gobelin("Frantz"));

        knight1.addItem("Pistolet", 1);
        knight1.addItem("Arbre", 1);
        knight1.addItem("Zebre", 1);

        //Test Afficher les items par ordre alphabétique
//        knight1.getInventory().keySet().stream().sorted().forEach(item -> System.out.println(item + " : " + knight1.getInventory().get(item)));;

//        System.out.println(equipe);
        System.out.println("Ton équipe : ");

//        Test animation
//        Thread.sleep(2000); // attend 2 sec
//        System.out.print("\rL'ennemi esquive !      "); // \r revient au début de la ligne
//        Thread.sleep(2000);
//        System.out.println("\rTon chevalier retente une attaque !   ");

        System.out.println(knight1);
        System.out.println("--------");
        System.out.println("Ton adversaire : ");
        for (Chevalier ennemi : knightTab ){
            System.out.println(ennemi);
            System.out.println("");
        }

//        System.out.println(knight1.getInventory());


        System.out.println("Début du combat !");
        System.out.println("");
        for (int i = 0; i < knightTab.length; i++) {
            System.out.println("Ennemi : " + knightTab[i].getName() + "\n");
            while (knight1.getStat(Stat.HP) >= 0 && hasAliveEnemies(knightTab)) {
                if (knight1.isStart()) {
                    System.out.println("au tour de " + knight1.getName());
                    System.out.println("Que faire ? \n 1 - Attaquer \n 2 - Fuir");
                    int input = scan.nextInt();
                    if (input == 1) {
                        knight1.performAttack(knightTab[i]);
                        System.out.println(knightTab[i].getName() + " a " + knightTab[i].getStat(Stat.HP) + " HP\n");
                    } else if (input == 2) {
                        System.out.println("fuite");
                        break;
                    } else {
                        System.out.println("erreur");
                    }
                }

                if (knightTab[i].isStart() && knightTab[i].getStat(Stat.HP) > 0) {
                    System.out.println("au tour de : " + knightTab[i].getName());
                    System.out.println("Que faire ? \n 1 - Attaquer \n 2 - Fuir");
                    if (scan.nextInt() == 1) {
                        knightTab[i].performAttack(knight1);
                        System.out.println(knight1.getName() + " a " + knight1.getStat(Stat.HP) + " HP\n");

                    } else if (scan.nextInt() == 2) {
                        System.out.println("fuite");
                        break;
                    } else {
                        System.out.println("erreur");
                    }
                }
                boolean temp = knight1.isStart();
                knight1.setStart(knightTab[i].isStart());
                knightTab[i].setStart(temp);

                if(knight1.getStat(Stat.HP) <= 0){
                    break;
                }
                if(knightTab[i].getStat(Stat.HP) <= 0){
                    break;
                }
            }
        }
            System.out.println("Fin du combat !");
            System.out.println(knight1.EndCombatMessage());
    }
}

