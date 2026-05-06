/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.gui.GameGUI;
import com.mycompany.fantasy.shop.gui.CombatGUI;
import com.mycompany.fantasy.shop.gui.ShopGUI;
import com.mycompany.fantasy.shop.gui.GUIBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameManager {

    private Player player;
    private Shop shop;
    private Event eventSystem;
    private Scanner scanner = new Scanner(System.in);
    private int day = 1;
    private boolean gameRunning = true;
    private boolean shopOpenedToday = false;
    private boolean storyToldToday = false;
    private boolean guiMode = false;

    private List<Soldier> availableSoldiers;
    private StoryManager storyManager;

    // ================== GUI SUPPORT ==================
    private GameGUI gui;

    public void setGUI(GameGUI gui) {
        this.gui = gui;
        this.guiMode = true;

        // Hubungkan bridge ke Event dan StoryManager
        GUIBridge bridge = (GUIBridge) gui;
        eventSystem.setBridge(bridge);
        storyManager.setBridge(bridge);
    }

    /** Routing output: GUI atau console */
    private void out(String text) {
        if (gui != null) {
            gui.appendToOutput(text);
        } else {
            System.out.println(text);
        }
    }

    // ================== GETTER ==================
    public Player getPlayer()          { return player; }
    public int getDay()                { return day; }
    public boolean isShopOpenedToday() { return shopOpenedToday; }
    public Shop getShop()              { return shop; }

    // ================== OUTPUT BUFFER ==================
    private StringBuilder output = new StringBuilder();

    public String flushOutput() {
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    // ================== KONSTRUKTOR ==================
    public GameManager() {
        shop = new Shop();
        eventSystem = new Event();
        storyManager = new StoryManager();
        availableSoldiers = new ArrayList<>();
        availableSoldiers.add(Soldier.createPrajurit());
        availableSoldiers.add(Soldier.createVeteran());
        availableSoldiers.add(Soldier.createKapten());
        // Catatan: player di-inisialisasi via initNewGame() atau loadGameGUI() dari GUI
        // Untuk mode console, player di-inisialisasi di startGame()
    }

    // ================== INIT (dipanggil dari GameGUI) ==================
    /** Inisialisasi game baru. Dipanggil dari GameGUI setelah input nama. */
    public void initNewGame(String name) {
        if (name == null || name.trim().isEmpty()) name = "Penjaga";
        player = new Player(name, 200);
    }

    /** Load game lewat GUI — terima nama save sebagai parameter, tidak pakai Scanner. */
    public void loadGameGUI(String saveName) {
        try {
            java.io.File file = new java.io.File(saveName + ".txt");
            if (!file.exists()) {
                player = new Player(saveName, 200);
                return;
            }
            java.util.Scanner sc = new java.util.Scanner(file);
            String playerName = sc.nextLine();
            int hp       = Integer.parseInt(sc.nextLine());
            int maxHp    = Integer.parseInt(sc.nextLine());
            int gold     = Integer.parseInt(sc.nextLine());
            int rep      = Integer.parseInt(sc.nextLine());
            int savedDay = Integer.parseInt(sc.nextLine());
            sc.close();

            player = new Player(playerName, 200);
            player.setHp(hp);
            player.setMaxHp(maxHp);
            player.setGold(gold);
            player.setReputation(rep);
            day = savedDay;
        } catch (Exception e) {
            player = new Player(saveName, 200);
        }
    }

    // ================== START GAME GUI ==================
    public String startGameGUI() {
        output.setLength(0);
        out("==================================================");
        out("              SELAMAT DATANG DI RUDORIA");
        out("==================================================");
        out("Versi GUI telah aktif.");
        out("Gunakan tombol-tombol di bawah untuk bermain.");
        out("");
        return flushOutput();
    }

    // ================== START GAME CONSOLE ==================
    public void startGame() {
        // Mode console: tanya nama di sini
        out("Welcome To The Solhaven Rudoria Game");
        out("1. PLAY (Main Baru)");
        out("2. LOAD (Lanjutkan)");
        out("Pilih (1/2): ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("2")) {
            loadGame();
        } else {
            out("Masukkan nama penjaga toko: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Penjaga";
            player = new Player(name, 200);
        }

        printIntroStory();
        while (gameRunning && day <= 40) {
            dailyMenu();
        }
        showFinalEnding();
    }

    // ================== HANDLE GUI ==================
    public String handleChoiceGUI(int choice) {
        output.setLength(0);

        switch (choice) {
            case 1 -> {
                if (!shopOpenedToday) {
                    out("══════════════════════════════════════");
                    out("           TOKO DIBUKA HARI INI");
                    out("══════════════════════════════════════");
                    openShopForTheDay();
                } else {
                    out("Toko sudah dibuka hari ini.");
                }
            }
            case 2 -> {
                out("=== BELI STOK ===");
                shop.buyStockMenu(player);
            }
            case 3 -> {
                out("=== KELOLA STOK ===");
                shop.manageInventory(player);
            }
            case 4 -> {
                out("=== UPGRADE TOKO ===");
                shop.upgradeMenu(player);
            }
            case 5 -> {
                out("=== KELOLA SOLDIER ===");
                manageSoldiers();
            }
            case 6 -> {
                out("=== AKHIRI HARI ===");
                endTheDay();
            }
            case 7 -> {
                out("Terima kasih telah bermain di Rudoria.");
                gameRunning = false;
            }
            case 8 -> {
                saveGame();
                out("Game berhasil disimpan!");
            }
            default -> out("Pilihan tidak valid.");
        }

        updateGUIStatus();
        return flushOutput();
    }

    private void updateGUIStatus() {
        if (gui != null) gui.updateStatus();
    }

    private void pause() {
        out(">>> Klik OK untuk melanjutkan <<<");
        if (gui != null) {
            gui.waitForContinue();
        } else {
            scanner.nextLine();
        }
    }

    // ================== INTRO ==================
    private void printIntroStory() {
        out("\n" + "=".repeat(60));
        out("          SELAMAT DATANG DI RUDORIA");
        out("=".repeat(60));
        out("");
        out("Di sebuah desa kecil yang tenang di dunia Rudoria,");
        out("terdapat sebuah toko tua yang dulu sangat terkenal.");
        out("");
        out("Suatu hari, seorang kakek tua pemilik toko berkata padamu:");
        out("\"Aku sudah tidak mampu menjaga tempat ini lagi...\"");
        out("\"Lanjutkanlah toko ini, nak...\"");
        out("");
        out("Tanpa banyak pilihan, kamu menerima tawaran itu.");
        out("Hari ini adalah hari pertamamu sebagai penjaga toko baru.");
        out("");
        out("Apakah kamu bisa mengembalikan kejayaan toko ini?");
        pause();
    }

    // ================== SAVE & LOAD ==================
    public void saveGame() {
        try {
            String filename = player.getName() + ".txt";
            java.io.FileWriter writer = new java.io.FileWriter(filename);
            writer.write(player.getName() + "\n");
            writer.write(player.getHp() + "\n");
            writer.write(player.getMaxHp() + "\n");
            writer.write(player.getGold() + "\n");
            writer.write(player.getReputation() + "\n");
            writer.write(day + "\n");
            writer.close();
            out("Game disimpan sebagai: " + filename);
        } catch (Exception e) {
            out("Gagal save!");
        }
    }

    public void loadGame() {
        try {
            out("Masukkan nama save: ");
            String name = scanner.nextLine();
            java.io.File file = new java.io.File(name + ".txt");

            if (!file.exists()) {
                out("Save tidak ditemukan!");
                return;
            }

            java.util.Scanner sc = new java.util.Scanner(file);
            String playerName = sc.nextLine();
            int hp       = Integer.parseInt(sc.nextLine());
            int maxHp    = Integer.parseInt(sc.nextLine());
            int gold     = Integer.parseInt(sc.nextLine());
            int rep      = Integer.parseInt(sc.nextLine());
            int savedDay = Integer.parseInt(sc.nextLine());
            sc.close();

            player = new Player(playerName, 200);
            player.setHp(hp);
            player.setMaxHp(maxHp);
            player.setGold(gold);
            player.setReputation(rep);
            day = savedDay;

            out("Game berhasil di-load!");
        } catch (Exception e) {
            out("Gagal load!");
        }
    }

    // ================== MENU HARIAN (Console) ==================
    private void dailyMenu() {
        player.snapshotDayStart();

        if (!storyToldToday) {
            storyManager.checkStory(day, player);
            storyToldToday = true;
        }

        out("\n" + "=".repeat(50));
        out("          HARI KE-" + day + " / 40" + getSpecialDayLabel());
        out("=".repeat(50));
        player.showStatus();

        out("Apa yang akan kamu lakukan hari ini?");
        out("1. " + (shopOpenedToday ? "[Toko sudah dibuka hari ini]" : "Buka Toko          (Layani customer)"));
        out("2. Beli Stok               (Isi barang dagangan)");
        out("3. Kelola Stok             (Lihat / buang item)");
        out("4. Upgrade Toko            (Butuh Gold)");
        out("5. Kelola Soldier          (Rekrut / Lihat)");
        out("6. Akhiri Hari             (tidur)");
        out("7. Keluar Game");
        out("8. Save Game");
        out("Pilih (1-8): ");

        int choice = getChoice(1, 8);
        switch (choice) {
            case 1 -> { if (!shopOpenedToday) openShopForTheDay(); else out("Toko sudah dibuka hari ini."); }
            case 2 -> shop.buyStockMenu(player);
            case 3 -> shop.manageInventory(player);
            case 4 -> shop.upgradeMenu(player);
            case 5 -> manageSoldiers();
            case 6 -> endTheDay();
            case 7 -> { out("Terima kasih telah bermain di Rudoria."); gameRunning = false; }
            case 8 -> saveGame();
        }
    }

    // ================== HARI SPESIAL ==================
    private String getSpecialDayLabel() {
        if (day % 10 == 0) return "  [HARI BOSS!]";
        if (day % 5 == 0)  return "  [HARI PASAR]";
        if (day == 35)     return "  [HARI NAGA!]";
        return "";
    }

    private boolean isMarketDay() { return day % 5 == 0 && day % 10 != 0; }
    private boolean isBossDay()   { return day % 10 == 0; }

    // ================== BUKA TOKO ==================
    private void openShopForTheDay() {
        shopOpenedToday = true;
        eventSystem.resetDailyEvents();

        if (isMarketDay()) {
            out("\n=== HARI PASAR! Desa sedang ramai ===\n");
        } else {
            out("\nToko dibuka... Customer mulai berdatangan.\n");
        }

        int customerCount = Math.min(2 + (player.getLevel() / 2), 3);
        if (isMarketDay()) customerCount = Math.min(customerCount + 1, 4);

        out("Hari ini ada " + customerCount + " customer.");

        for (int i = 1; i <= customerCount; i++) {
            out("\n────────────────────────────");
            out("Customer ke-" + i + " dari " + customerCount);
            out("────────────────────────────");

            if (isMarketDay()) shop.serveCustomer(player, true);
            else               shop.serveCustomer(player, false);

            eventSystem.triggerEvent(player);

            if (i < customerCount) pause();
        }

        out("\nToko ditutup untuk hari ini.");
    }

    // ================== AKHIRI HARI ==================
    private void endTheDay() {
        player.showDailySummary(day);

        out("\nHari berakhir. Kamu menutup toko dan beristirahat.");

        payAllSoldierWages();
        player.restHeal();

        // Event malam
        double nightRoll = Math.random();
        if (nightRoll < 0.20) {
            out("\nMalam ini terasa tidak tenang...");
            eventSystem.thiefEvent(player);
        } else if (nightRoll < 0.35) {
            out("\n[!!] Serangan malam! Musuh menyusup ke toko!");
            triggerCombat(chooseNightEnemy());
            if (!player.isAlive()) return;
        } else {
            out("Malam berlalu dengan tenang.");
        }

        if (shopOpenedToday) player.gainReputation(1);

        day++;
        shopOpenedToday = false;
        storyToldToday = false;

        // Boss day setiap 10 hari
        if (isBossDay()) {
            out("\n[BOSS] Ancaman besar mendekat ke desa Rudoria!");
            out("Tekan OK untuk menghadapi boss...");
            pause();
            triggerCombat(Enemy.createKsatriaJahat());
            if (!player.isAlive()) return;
        }

        // Naga hari ke-35
        if (day == 35) {
            out("\n[NAGA] Langit memerah... Naga Rudoria telah terbangun!");
            out("Tekan OK untuk menghadapi Naga...");
            pause();
            triggerCombat(Enemy.createNaga());
            if (!player.isAlive()) return;
        }

        // Cek menang
        if (player.getReputation() >= 92) {
            out("\nReputasi tokomu luar biasa! Seluruh Rudoria mengenalmu!");
            gameRunning = false;
        }
    }

    // ================== COMBAT ==================
    private void triggerCombat(Enemy enemy) {
        if (gui != null) {
            CombatGUI combatWindow = new CombatGUI(gui, player, enemy, availableSoldiers);
            combatWindow.setVisible(true);

            if (!player.isAlive()) {
                out("\nHP kamu habis. GAME OVER.");
                gameRunning = false;
            } else {
                out("\nPertarungan selesai. Kamu selamat.");
            }
            updateGUIStatus();
        } else {
            CombatSystem.startCombat(player, enemy, availableSoldiers);
            if (!player.isAlive()) {
                out("\nHP kamu habis. GAME OVER.");
                gameRunning = false;
            }
        }
    }

    private Enemy chooseDayEnemy()   { return Math.random() < 0.6  ? Enemy.createBandit()      : Enemy.createPreman(); }
    private Enemy chooseNightEnemy() { return Math.random() < 0.65 ? Enemy.createPreman()       : Enemy.createKsatriaJahat(); }

    // ================== SOLDIER ==================
    private void manageSoldiers() {
        while (true) {
            out("\n=== KELOLA SOLDIER ===");
            for (int i = 0; i < availableSoldiers.size(); i++) {
                out((i + 1) + ".");
                availableSoldiers.get(i).showInfo();
                out("");
            }
            out((availableSoldiers.size() + 1) + ". Kembali");
            out("Pilih: ");

            int choice = getChoice(1, availableSoldiers.size() + 1);
            if (choice == availableSoldiers.size() + 1) break;

            Soldier chosen = availableSoldiers.get(choice - 1);
            if (!chosen.isRecruited()) chosen.recruit(player);
            else out(chosen.getName() + " sudah bersamamu.");
        }
    }

    private void payAllSoldierWages() {
        boolean any = availableSoldiers.stream().anyMatch(Soldier::isRecruited);
        if (!any) return;
        out("\nMembayar upah soldier...");
        for (Soldier s : availableSoldiers) {
            if (s.isRecruited()) s.payDailyWage(player);
        }
    }

    // ================== FINAL ENDING ==================
    private void showFinalEnding() {
        out("\n" + "=".repeat(60));
        out("                    AKHIR CERITA");
        out("=".repeat(60));

        if (!player.isAlive()) {
            out("\nKamu gugur dalam pertempuran di hari ke-" + day + ".");
            out("Toko Rudoria kembali sunyi tanpa penjaganya...");
        } else if (player.getReputation() >= 90) {
            out("\nBeberapa bulan kemudian...");
            out("Pemilik toko lama kembali berkunjung ke desa.");
            out("Ia terkejut melihat toko yang dulu sepi kini ramai.");
            out("");
            out("*** SELAMAT! KAMU BERHASIL MENGEMBALIKAN KEJAYAAN TOKO RUDORIA! ***");
        } else if (player.getReputation() >= 60) {
            out("\nToko kamu sudah cukup terkenal di desa.");
            out("Perjalananmu belum selesai, tapi kamu sudah membuktikan dirimu.");
        } else {
            out("\nSetelah " + (day - 1) + " hari, toko masih bertahan.");
            out("Perjalananmu masih panjang untuk mengembalikan kejayaannya.");
        }

        out("");
        player.showStatus();
        out("Terima kasih telah bermain!");
    }

    // ================== OPEN SHOP (GUI) ==================
    public void openShop() {
        if (guiMode && gui != null) {
            ShopGUI shopWindow = new ShopGUI(gui, player, shop);
            shopWindow.setVisible(true);
            gui.updateStatus();
        } else {
            out("Mode console tidak tersedia.");
        }
    }

    // ================== HELPER ==================
    private int getChoice(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) return input;
                out("Pilih " + min + "-" + max + ": ");
            } catch (Exception e) {
                scanner.nextLine();
                out("Masukkan angka: ");
            }
        }
    }

    public void setShopOpenedToday(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
