/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CombatSystem {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    // Kembalikan true = menang, false = kalah
    public static boolean startCombat(Player player, Enemy enemy, List<Soldier> soldiers) {
        enemy.showIntro();
        System.out.println("\nTekan Enter untuk mulai pertarungan...");
        scanner.nextLine();

        int round = 1;

        while (enemy.isAlive() && player.getHp() > 0) {
            System.out.println("\n--- Ronde " + round + " -------------------------------------------");
            showCombatStatus(player, enemy);
            System.out.println();

            playerTurn(player, enemy, soldiers);
            if (!enemy.isAlive()) break;

            enemyTurn(player, enemy, soldiers);
            round++;

            if (round > 20) {
                System.out.println("\nPertarungan terlalu lama! " + enemy.getName() + " mundur...");
                return false;
            }
        }

        if (!enemy.isAlive()) {
            onWin(player, enemy);
            return true;
        } else {
            onLose(player, enemy);
            return false;
        }
    }

    // ?????? Player Turn ???????????????????????????????????????????????????????????????????????????????????????
    private static void playerTurn(Player player, Enemy enemy, List<Soldier> soldiers) {
        boolean hasSoldier = soldiers != null && soldiers.stream().anyMatch(Soldier::isRecruited);
        boolean hasPotion  = player.hasHealthItem();

        System.out.println(">>> Giliran kamu!");
        System.out.println("1. Serang          (DMG: " + player.getAttack() + "-" + (player.getAttack() + 10) + ")");
        System.out.println("2. Serang Keras    (2x DMG, 60% hit)");
        System.out.println("3. Bertahan        (Damage masuk -50% ronde ini)");
        System.out.println("4. Gunakan Potion  " + (hasPotion ? "(Pulihkan HP)" : "(Tidak ada potion)"));
        System.out.println("5. Perintah Soldier" + (hasSoldier ? "" : " (Belum ada soldier)"));
        System.out.println("6. Panggil Darurat (30 gold)");
        System.out.print("Pilih aksi: ");

        int choice = getChoice(1, 6);

        switch (choice) {
            case 1 -> {
                int dmg = player.getAttack() + random.nextInt(11);
                enemy.takeDamage(dmg);
                System.out.println("Kamu menyerang " + enemy.getName() + "! (-" + dmg + " HP)");
            }
            case 2 -> {
                if (random.nextInt(100) < 60) {
                    int dmg = (player.getAttack() + random.nextInt(11)) * 2;
                    enemy.takeDamage(dmg);
                    System.out.println("[KERAS] Serangan mengenai! (-" + dmg + " HP)");
                } else {
                    System.out.println("[MISS] Serangan keras meleset!");
                }
            }
            case 3 -> {
                player.setDefending(true);
                System.out.println("[BERTAHAN] Damage berkurang 50% ronde ini.");
            }
            case 4 -> {
                if (hasPotion) {
                    player.useHealthItem();
                } else {
                    System.out.println("Tidak ada potion! Giliran terbuang.");
                }
            }
            case 5 -> {
                if (hasSoldier) {
                    for (Soldier s : soldiers) {
                        if (s.isRecruited()) s.fightWith(enemy);
                    }
                } else {
                    System.out.println("Tidak ada soldier yang direkrut. Giliran terbuang.");
                }
            }
            case 6 -> Soldier.emergencyCall(player, enemy);
        }
    }

    // ?????? Enemy Turn ??????????????????????????????????????????????????????????????????????????????????????????
    private static void enemyTurn(Player player, Enemy enemy, List<Soldier> soldiers) {
        if (player.getHp() <= 0) return;

        System.out.println("\n>>> Giliran " + enemy.getName() + "!");

        int baseDmg = enemy.rollAttack();

        // Soldier blok damage
        int blocked = 0;
        if (soldiers != null) {
            for (Soldier s : soldiers) {
                if (s.isRecruited()) {
                    int def = s.defend();
                    blocked += def;
                    System.out.println("[BLOK] " + s.getName() + " memblok " + def + " damage!");
                }
            }
        }

        if (player.isDefending()) {
            baseDmg = baseDmg / 2;
            player.setDefending(false);
        }

        int finalDmg = Math.max(1, baseDmg - blocked);
        player.takeDamage(finalDmg);
        System.out.println(enemy.getName() + " menyerang! (-" + finalDmg + " HP)");

        specialEnemyEffect(player, enemy);
    }

    // ?????? Efek Khusus Enemy ????????????????????????????????????????????????????????????????????????
    private static void specialEnemyEffect(Player player, Enemy enemy) {
        switch (enemy.getType()) {
            case "preman" -> {
                if (random.nextInt(100) < 20 && player.getGold() > 0) {
                    int stolen = Math.min(15, player.getGold());
                    player.spendGoldSilent(stolen);
                    System.out.println("[EFEK] " + enemy.getName() + " mencuri " + stolen + " gold!");
                }
            }
            case "ksatria_jahat" -> {
                if (random.nextInt(100) < 25) {
                    int bonusDmg = enemy.rollAttack() / 2;
                    player.takeDamage(bonusDmg);
                    System.out.println("[EFEK] Serangan ganda ksatria! (-" + bonusDmg + " HP bonus)");
                }
            }
            case "naga" -> {
                if (random.nextInt(100) < 30) {
                    int fireDmg = 15 + random.nextInt(20);
                    player.takeDamage(fireDmg);
                    System.out.println("[EFEK] Naga menyemburkan api! (-" + fireDmg + " HP)");
                }
            }
        }
    }

    // ?????? Hasil Combat ???????????????????????????????????????????????????????????????????????????????????????
    private static void onWin(Player player, Enemy enemy) {
        System.out.println("\n==========================================");
        System.out.println("          KAMU MENANG!");
        System.out.println("==========================================");
        System.out.println(enemy.getName() + " berhasil dikalahkan!");
        player.gainGold(enemy.getGoldReward());
        player.gainExp(enemy.getExpReward());
        player.gainReputation(enemy.getRepReward());
    }

    private static void onLose(Player player, Enemy enemy) {
        System.out.println("\n==========================================");
        System.out.println("          KAMU KALAH...");
        System.out.println("==========================================");
        System.out.println(enemy.getName() + " mengalahkanmu.");
        System.out.println("Toko mengalami kerusakan...");
        player.loseReputation(8);
        int goldLost = Math.min(50, player.getGold());
        player.spendGoldSilent(goldLost);
        System.out.println("Kehilangan " + goldLost + " gold dan 8 reputasi.");
    }

    // ?????? Display ??????????????????????????????????????????????????????????????????????????????????????????????????????
    private static void showCombatStatus(Player player, Enemy enemy) {
        System.out.println("[ PLAYER ]");
        player.showCombatStatus();
        System.out.println("[ MUSUH  ]");
        enemy.showStatus();
    }

    // ?????? Helper ?????????????????????????????????????????????????????????????????????????????????????????????????????????
    private static int getChoice(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) return input;
                System.out.print("Pilih " + min + "-" + max + ": ");
            } catch (Exception e) {
                scanner.nextLine();
                System.out.print("Masukkan angka: ");
            }
        }
    }
}