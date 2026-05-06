/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;
/**
 *
 * @author Asus
 */

import com.mycompany.fantasy.shop.gui.GUIBridge;
import java.util.*;

/**
 * Event.java — versi GUI.
 * Semua getChoice() dan System.out sekarang routing lewat GUIBridge.
 * Jika bridge = null, fallback ke Scanner (mode console tetap jalan).
 */
public class Event {
    private final Random  random  = new Random();
    private final Scanner scanner = new Scanner(System.in);

    private final List<String> usedEventsToday = new ArrayList<>();

    // Bridge ke GUI — di-set dari GameManager
    private GUIBridge bridge;

    public void setBridge(GUIBridge bridge) {
        this.bridge = bridge;
    }

    private void log(String text) {
        if (bridge != null) bridge.appendLog(text);
        else System.out.println(text);
    }

    // ── Reset harian ───────────────────────────────────────────────
    public void resetDailyEvents() {
        usedEventsToday.clear();
    }

    // ── Trigger event acak ─────────────────────────────────────────
    public boolean triggerEvent(Player player) {
        int reputation = player.getReputation();

        List<String> available = new ArrayList<>();
        if (!usedEventsToday.contains("poorCustomer"))    available.add("poorCustomer");
        if (!usedEventsToday.contains("angryCustomer"))   available.add("angryCustomer");
        if (!usedEventsToday.contains("brokenItem"))      available.add("brokenItem");
        if (!usedEventsToday.contains("thiefEvent"))      available.add("thiefEvent");
        if (!usedEventsToday.contains("gangsterEvent"))   available.add("gangsterEvent");
        if (reputation >= 25 && !usedEventsToday.contains("vipCustomer"))
            available.add("vipCustomer");
        if (reputation >= 40 && !usedEventsToday.contains("illegalDeal"))
            available.add("illegalDeal");

        int eventChance = 30 + (usedEventsToday.size() * 5);
        if (random.nextInt(100) >= eventChance || available.isEmpty()) return false;

        Collections.shuffle(available, random);
        String chosen = available.get(0);
        usedEventsToday.add(chosen);

        switch (chosen) {
            case "poorCustomer"  -> poorCustomer(player);
            case "angryCustomer" -> angryCustomer(player);
            case "brokenItem"    -> brokenItem(player);
            case "thiefEvent"    -> thiefEvent(player);
            case "gangsterEvent" -> gangsterEvent(player);
            case "vipCustomer"   -> vipCustomer(player);
            case "illegalDeal"   -> illegalDeal(player);
        }
        return true;
    }

    // ══════════════════════════════════════════════════════════════
    //  EVENT DEFINITIONS
    // ══════════════════════════════════════════════════════════════

    private void poorCustomer(Player player) {
        String narrative =
            "Seorang ibu desa datang dengan wajah cemas.\n" +
            "Anaknya sakit dan butuh potion, tapi dia hanya punya 10 gold...\n\n" +
            "Apa yang akan kamu lakukan?";

        if (bridge != null) {
            bridge.showDecision("🧓", "Customer Miskin", narrative,
                new String[]{
                    "Kasih potion secara gratis (+5 Reputasi, +12 EXP)",
                    "Tetap jual dengan harga normal (+20 Gold, -2 Reputasi)"
                },
                choice -> {
                    if (choice == 0) {
                        log("Ibu itu menangis haru dan berterima kasih padamu.");
                        player.gainReputation(5);
                        player.gainExp(12);
                    } else {
                        log("Kamu tetap menjualnya. Ibu itu pergi dengan wajah sedih.");
                        player.gainGold(20);
                        player.loseReputation(2);
                    }
                    bridge.refreshStatus();
                });
        } else {
            System.out.println("\n Seorang ibu desa datang dengan wajah cemas.");
            System.out.println("1. Kasih potion secara gratis");
            System.out.println("2. Tetap jual dengan harga normal");
            int c = getChoiceConsole(1, 2);
            if (c == 1) { player.gainReputation(5); player.gainExp(12); }
            else { player.gainGold(20); player.loseReputation(2); }
        }
    }

    private void angryCustomer(Player player) {
        String narrative =
            "Seorang knight marah besar karena potion yang dibelinya kemarin tidak manjur.\n\n" +
            "Dia menuntut ganti rugi di depan banyak orang.\n" +
            "Apa responmu?";

        if (bridge != null) {
            bridge.showDecision("😤", "Customer Marah", narrative,
                new String[]{
                    "Turunkan harga & minta maaf (+15 Gold, +3 Reputasi)",
                    "Tolak dan suruh pergi (-4 Reputasi)"
                },
                choice -> {
                    if (choice == 0) {
                        log("Knight itu tenang dan mau beli lagi dengan harga diskon.");
                        player.gainGold(15);
                        player.gainReputation(3);
                    } else {
                        log("Knight itu pergi sambil mengumpat.");
                        player.loseReputation(4);
                    }
                    bridge.refreshStatus();
                });
        } else {
            System.out.println("\n Seorang knight marah besar...");
            System.out.println("1. Turunkan harga & minta maaf");
            System.out.println("2. Tolak dan suruh pergi");
            int c = getChoiceConsole(1, 2);
            if (c == 1) { player.gainGold(15); player.gainReputation(3); }
            else player.loseReputation(4);
        }
    }

    private void brokenItem(Player player) {
        String narrative =
            "Beberapa potion pecah di rak karena rak tua yang rapuh.\n\n" +
            "Kamu kehilangan barang senilai 25 gold.";

        if (bridge != null) {
            bridge.showContinue(narrative, () -> {
                player.spendGold(25);
                bridge.refreshStatus();
            });
        } else {
            System.out.println("\n Beberapa potion pecah di rak...");
            player.spendGold(25);
        }
    }

    public void thiefEvent(Player player) {
        String narrative =
            "MALAM INI — Suara mencurigakan terdengar di belakang toko!\n" +
            "Seorang pencuri mencoba membobol gudang!\n\n" +
            "Apa yang akan kamu lakukan?";

        if (bridge != null) {
            bridge.showDecision("🦹", "Pencuri Malam!", narrative,
                new String[]{
                    "Panggil prajurit kerajaan (70% berhasil)",
                    "Abaikan dan diam saja (-40 Gold, -5 Reputasi)"
                },
                choice -> {
                    if (choice == 0) {
                        int successChance = 70 + (player.getLevel() * 5);
                        if (random.nextInt(100) < successChance) {
                            log("Prajurit datang cepat dan menangkap pencuri! (+60 Gold, +3 Reputasi)");
                            player.gainGold(60);
                            player.gainExp(20);
                            player.gainReputation(3);
                        } else {
                            log("Prajurit terlambat... Pencuri kabur membawa beberapa barang. (-45 Gold)");
                            player.spendGold(45);
                            player.loseReputation(2);
                        }
                    } else {
                        log("Kamu memilih diam. Pencuri berhasil mencuri 40 gold.");
                        player.spendGold(40);
                        player.loseReputation(5);
                    }
                    bridge.refreshStatus();
                });
        } else {
            System.out.println("\n Suara mencurigakan...");
            System.out.println("1. Panggil prajurit  2. Abaikan");
            int c = getChoiceConsole(1, 2);
            if (c == 1) {
                if (random.nextInt(100) < 70) { player.gainGold(60); player.gainReputation(3); }
                else { player.spendGold(45); }
            } else { player.spendGold(40); player.loseReputation(5); }
        }
    }

    private void gangsterEvent(Player player) {
        String narrative =
            "Seorang preman dari geng lokal datang dengan sombong.\n\n" +
            "\"Bayar pajak perlindungan kalau tidak mau toko rusak!\"\n\n" +
            "Apa yang akan kamu lakukan?";

        if (bridge != null) {
            bridge.showDecision("🔪", "Preman Geng", narrative,
                new String[]{
                    "Bayar 30 gold (aman, tapi memalukan)",
                    "Panggil prajurit untuk melawan (65% berhasil)"
                },
                choice -> {
                    if (choice == 0) {
                        player.spendGold(30);
                        log("Preman pergi sambil tertawa. (-30 Gold, -1 Reputasi)");
                        player.loseReputation(1);
                    } else {
                        if (random.nextInt(100) < 65) {
                            log("Prajurit berhasil mengusir preman! (+50 Gold, +5 Reputasi)");
                            player.gainGold(50);
                            player.gainExp(18);
                            player.gainReputation(5);
                        } else {
                            log("Toko sedikit rusak akibat perkelahian. (-65 Gold, -6 Reputasi)");
                            player.spendGold(65);
                            player.loseReputation(6);
                        }
                    }
                    bridge.refreshStatus();
                });
        } else {
            System.out.println("\n Preman datang menagih pajak...");
            System.out.println("1. Bayar 30 gold  2. Panggil prajurit");
            int c = getChoiceConsole(1, 2);
            if (c == 1) { player.spendGold(30); player.loseReputation(1); }
            else {
                if (random.nextInt(100) < 65) { player.gainGold(50); player.gainReputation(5); }
                else { player.spendGold(65); player.loseReputation(6); }
            }
        }
    }

    private void vipCustomer(Player player) {
        String narrative =
            "Seorang Mage terkenal dari kota besar masuk ke toko!\n\n" +
            "Dia membeli banyak barang langka dengan harga penuh.\n" +
            "+95 Gold  |  +25 EXP  |  +4 Reputasi";

        if (bridge != null) {
            bridge.showContinue(narrative, () -> {
                player.gainGold(95);
                player.gainExp(25);
                player.gainReputation(4);
                bridge.refreshStatus();
            });
        } else {
            System.out.println("\n Mage terkenal masuk ke toko!");
            player.gainGold(95); player.gainExp(25); player.gainReputation(4);
        }
    }

    private void illegalDeal(Player player) {
        String narrative =
            "Seorang pria berjubah hitam mendekatimu diam-diam.\n\n" +
            "Dia menawarkan \"barang spesial\" yang sangat mahal harganya.\n" +
            "Keuntungan besar... tapi berisiko tinggi.\n\n" +
            "Apa keputusanmu?";

        if (bridge != null) {
            bridge.showDecision("🕵", "Tawaran Misterius", narrative,
                new String[]{
                    "Terima tawaran (55% dapat +130 Gold, 45% kehilangan -90 Gold)",
                    "Tolak dengan sopan (aman)"
                },
                choice -> {
                    if (choice == 0) {
                        if (random.nextInt(100) < 55) {
                            log("Kesepakatan berhasil! Kamu mendapat keuntungan besar. (+130 Gold)");
                            player.gainGold(130);
                            player.gainReputation(2);
                        } else {
                            log("Ternyata itu jebakan! Hampir ditangkap petugas kerajaan. (-90 Gold, -10 Reputasi)");
                            player.spendGold(90);
                            player.loseReputation(10);
                        }
                    } else {
                        log("Kamu menolak tawaran itu. Pria itu pergi tanpa kata.");
                    }
                    bridge.refreshStatus();
                });
        } else {
            System.out.println("\n Pria berjubah hitam menawarkan sesuatu...");
            System.out.println("1. Terima  2. Tolak");
            int c = getChoiceConsole(1, 2);
            if (c == 1) {
                if (random.nextInt(100) < 55) { player.gainGold(130); }
                else { player.spendGold(90); player.loseReputation(10); }
            }
        }
    }

    // ── Console fallback ───────────────────────────────────────────
    private int getChoiceConsole(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) return input;
            } catch (Exception e) { scanner.nextLine(); }
        }
    }
}
