/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import java.util.Random;

public class Enemy {
    private String name;
    private String description;
    private int maxHp;
    private int hp;
    private int attackMin;
    private int attackMax;
    private int goldReward;
    private int expReward;
    private int repReward;
    private String type; // "bandit", "preman", "ksatria_jahat", "naga"

    private static final Random random = new Random();

    public Enemy(String name, String description, int hp, int attackMin, int attackMax,
                 int goldReward, int expReward, int repReward, String type) {
        this.name        = name;
        this.description = description;
        this.maxHp       = hp;
        this.hp          = hp;
        this.attackMin   = attackMin;
        this.attackMax   = attackMax;
        this.goldReward  = goldReward;
        this.expReward   = expReward;
        this.repReward   = repReward;
        this.type        = type;
    }

    //  Factory Methods 
    public static Enemy createBandit() {
        return new Enemy(
            "Bandit Jalanan",
            "Pencuri biasa yang sering berkeliaran di sekitar desa.",
            40, 8, 14, 50, 20, 2, "bandit"
        );
    }

    public static Enemy createPreman() {
        return new Enemy(
            "Preman Geng",
            "Anggota geng lokal yang lebih berbahaya dari bandit biasa.",
            65, 12, 20, 80, 30, 3, "preman"
        );
    }

    public static Enemy createKsatriaJahat() {
        return new Enemy(
            "Ksatria Jahat",
            "Bekas knight kerajaan yang berbalik jahat. Terlatih dan berbahaya.",
            100, 18, 28, 130, 50, 5, "ksatria_jahat"
        );
    }

    public static Enemy createNaga() {
        return new Enemy(
            "Naga Rudoria",
            "Makhluk legendaris penjaga harta. Kemunculannya pertanda bahaya besar.",
            200, 30, 45, 300, 100, 15, "naga"
        );
    }

    //  Combat 
    public int rollAttack() {
        return attackMin + random.nextInt(attackMax - attackMin + 1);
    }

    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    //  Display 
    public void showStatus() {
        int barFilled = (int) ((double) hp / maxHp * 10);
        String bar = "".repeat(barFilled) + "".repeat(10 - barFilled);
        System.out.printf("%-20s HP: [%s] %d/%d%n", name, bar, hp, maxHp);
    }

    public void showIntro() {
        System.out.println("");
        System.out.println("         [ATK]  MUSUH MUNCUL!  [ATK]         ");
        System.out.println("");
        System.out.println("  " + name + " telah muncul!");
        System.out.println("  \"" + description + "\"");
        System.out.println("  HP: " + maxHp + "  |  ATK: " + attackMin + "-" + attackMax);
    }

    //  Getter 
    public String getName()    { return name; }
    public String getType()    { return type; }
    public int getHp()         { return hp; }
    public int getMaxHp()      { return maxHp; }
    public int getGoldReward() { return goldReward; }
    public int getExpReward()  { return expReward; }
    public int getRepReward()  { return repReward; }
}