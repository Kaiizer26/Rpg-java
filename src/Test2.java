import Personnage.Chevalier;

import java.util.Scanner;
public class Test2 {
    public static void firstToAttack(Chevalier knight1, Chevalier knight2) {
        if (knight1.getSpeed() >= knight2.getSpeed()) {
            knight1.setStart(true);
            knight2.setStart(false);
        } else {
            knight2.setStart(true);
            knight1.setStart(false);
        }
    }

    public static boolean hasAliveEnemies(Chevalier[] enemies) {
        for (Chevalier enemy : enemies) {
            if (enemy.getHp() > 0) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Chevalier knight1 = new Chevalier();
        knight1.setName("Gustave");
        knight1.setHp(999);
        knight1.setDefense(32);
        knight1.setAtk(45);
        knight1.setSpeed(45);
        knight1.setLuck(57);
        knight1.setStart(true);

        Chevalier knight2 = new Chevalier();
        knight2.setName("Simon");
        knight2.setHp(99);
        knight2.setDefense(32);
        knight2.setAtk(85);
        knight2.setSpeed(60);
        knight2.setLuck(57);


        Chevalier[] knightTab = {new Chevalier("Jordy"), new Chevalier("Johan"), new Chevalier("Mansha"), new Chevalier("Lonnye"), new Chevalier("Johanpok")};
//        for(personnages.Chevalier valeur : knightTab){
//            System.out.println(valeur);
//        }
//
//        firstToAttack(knight1, knight2);
//        System.out.println(knight1);
//        System.out.println("----");
//        System.out.println(knight2);

        System.out.println("Début du combat !");
        System.out.println("");
        for (int i = 0; i < knightTab.length; i++) {
            System.out.println("Ennemi : " + knightTab[i].getName() + "\n");
            while (knight1.getHp() >= 0 && hasAliveEnemies(knightTab)) {
                if (knight1.isStart()) {
                    System.out.println("au tour de " + knight1.getName());
                    System.out.println("Que faire ? \n 1 - Attaquer \n 2 - Fuir");
                    int input = scan.nextInt();
                    if (input == 1) {
                        knight1.performAttack(knightTab[i]);
                        System.out.println(knightTab[i].getName() + " a " + knightTab[i].getHp() + " HP\n");
                    } else if (input == 2) {
                        System.out.println("fuite");
                        break;
                    } else {
                        System.out.println("erreur");
                    }
                }

                if (knightTab[i].isStart() && knightTab[i].getHp() > 0) {
                    System.out.println("au tour de : " + knightTab[i].getName());
                    System.out.println("Que faire ? \n 1 - Attaquer \n 2 - Fuir");
                    if (scan.nextInt() == 1) {
                        knightTab[i].performAttack(knight1);
                        System.out.println(knight1.getName() + " a " + knight1.getHp() + " HP\n");

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

                if(knight1.getHp() <= 0){
                    break;
                }
                if(knightTab[i].getHp() <= 0){
                    break;
                }
            }
        }
            System.out.println("Fin du combat !");
    }
}

