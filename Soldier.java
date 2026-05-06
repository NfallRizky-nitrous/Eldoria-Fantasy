package com.mycompany.fantasy.shop;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Asus
 */
import java.util.Random;

public class Soldier {
    private String name;
    private int rank;           // 1=Prajurit, 2=Veteran, 3=Kapten
    private int attackBonus;    // Damage bonus ke enemy
    private int defenseBonus;   // Kurangi damage ke player
    private int dailyWage;      // Upah per hari
    private boolean recruited;  // Sudah direkrut atau belum
    private int loyaltyDays;    // Sudah berapa hari bersama player

    private static final Random random = new Random();

    public Soldier(String name, int rank, int attackBonus, int defenseBonus, int dailyWage) {
        this.name         = name;
        this.rank         = rank;
        this.attackBonus  = attackBonus;
        this.defenseBonus = defenseBonus;
        this.dailyWage    = dailyWage;
        this.recruited    = false;
        this.loyaltyDays  = 0;
    }

    //  Preset Soldiers 
    public static Soldier createPrajurit() {
        return new Soldier("Prajurit Desa", 1, 5, 3, 20);
    }

    public static Soldier createVeteran() {
        return new Soldier("Veteran Perang", 2, 12, 8, 45);
    }

    public static Soldier createKapten() {
        return new Soldier("Kapten Garda", 3, 20, 15, 80);
    }

    //  Rekrutmen 
    public boolean recruit(Player player) {
        if (recruited) {
            System.out.println(name + " sudah bersamamu.");
            return false;
        }
        int cost = getRecruitCost();
        if (player.getGold() < cost) {
            System.out.println("Gold tidak cukup! Butuh " + cost + " gold untuk merekrut " + name + ".");
            return false;
        }
        player.spendGold(cost);
        recruited = true;
        System.out.println("[OK] " + name + " bergabung dengan tokomu!");
        System.out.println("   Upah harian: " + dailyWage + " gold/hari");
        return true;
    }

    public void payDailyWage(Player player) {
        if (!recruited) return;
        if (player.getGold() >= dailyWage) {
            player.spendGold(dailyWage);
            loyaltyDays++;
            System.out.println("[+] Upah " + name + " dibayar (" + dailyWage + " gold)");
        } else {
            System.out.println("[!!]  Kamu tidak bisa membayar " + name + "!");
            System.out.println("   " + name + " kecewa dan memilih pergi...");
            recruited = false;
            loyaltyDays = 0;
        }
    }

    //  Combat Support 

    // Dipanggil saat darurat (tanpa rekrut)  lebih mahal, chance gagal
    public static int emergencyCall(Player player, Enemy enemy) {
        System.out.println("\n[!!] Kamu memanggil prajurit kerajaan untuk bantuan darurat!");
        int cost = 30;
        if (player.getGold() < cost) {
            System.out.println("Tidak ada gold untuk memanggil prajurit! Kamu harus hadapi sendiri.");
            return 0;
        }
        player.spendGold(cost);

        // Chance sukses: 60% base + bonus dari level player
        int successChance = 60 + (player.getLevel() * 5);
        if (random.nextInt(100) < successChance) {
            int damage = 20 + random.nextInt(15);
            enemy.takeDamage(damage);
            System.out.println("Prajurit datang dan menyerang " + enemy.getName() + "! (-" + damage + " HP)");
            return damage;
        } else {
            System.out.println("Prajurit terlambat datang... tidak banyak membantu.");
            return 0;
        }
    }

    // Soldier yang sudah direkrut ikut bertarung
    public int fightWith(Enemy enemy) {
        if (!recruited) return 0;
        int damage = attackBonus + random.nextInt(10);
        // Loyalitas lama  damage lebih tinggi
        damage += loyaltyDays / 3;
        enemy.takeDamage(damage);
        System.out.println("[ATK]  " + name + " menyerang " + enemy.getName() + "! (-" + damage + " HP)");
        return damage;
    }

    public int defend() {
        // Kurangi damage yang diterima player
        return defenseBonus + (loyaltyDays / 5);
    }

    //  Display 
    public void showInfo() {
        String rankName = switch (rank) {
            case 1 -> "Prajurit";
            case 2 -> "Veteran";
            case 3 -> "Kapten";
            default -> "Tidak dikenal";
        };
        System.out.println("  " + name + " [" + rankName + "]");
        System.out.println("  ATK Bonus: +" + attackBonus + "  |  DEF Bonus: +" + defenseBonus);
        System.out.println("  Rekrut: " + getRecruitCost() + " gold  |  Upah/hari: " + dailyWage + " gold");
        if (recruited) System.out.println("  Status: [OK] Sudah direkrut (" + loyaltyDays + " hari bersama)");
        else System.out.println("  Status: [!!] Belum direkrut");
    }

    //  Getter 
    public String getName()      { return name; }
    public boolean isRecruited() { return recruited; }
    public int getDailyWage()    { return dailyWage; }
    public int getRecruitCost()  { return dailyWage * 5; }
    public int getDefenseBonus() { return defenseBonus; }
}