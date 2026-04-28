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

    private List<Soldier> availableSoldiers;
    private StoryManager storyManager;

    public GameManager() {
    // 1. Siapkan sistem dasar dulu
    shop = new Shop();
    eventSystem = new Event();
    storyManager = new StoryManager();
    availableSoldiers = new ArrayList<>();
    availableSoldiers.add(Soldier.createPrajurit());
    availableSoldiers.add(Soldier.createVeteran());
    availableSoldiers.add(Soldier.createKapten());

    // 2. Tampilkan Menu
    System.out.println("Welcome To The Solhaven Rudoria Game");
    System.out.println("1. PLAY (Main Baru)");
    System.out.println("2. LOAD (Lanjutkan)");
    System.out.print("Pilih (1/2): ");
    
    String choice = scanner.nextLine().trim();

    if (choice.equals("2")) {
        // JIKA PILIH LOAD
        loadGame(); // Memanggil fungsi loadGame yang sudah kamu buat
    } else {
        // JIKA PILIH PLAY (Atau input selain 2)
        System.out.print("Masukkan nama penjaga toko: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Penjaga";
        player = new Player(name);
    }
}

    public void startGame() {
        printIntroStory();
        while (gameRunning && day <= 40) {
            dailyMenu();
        }
        showFinalEnding();
    }

    // Intro 
    private void printIntroStory() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          SELAMAT DATANG DI RUDORIA");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("Di sebuah desa kecil yang tenang di dunia Rudoria,");
        System.out.println("terdapat sebuah toko tua yang dulu sangat terkenal.");
        System.out.println();
        System.out.println("Suatu hari, seorang kakek tua pemilik toko berkata padamu:");
        System.out.println("\"Aku sudah tidak mampu menjaga tempat ini lagi...\"");
        System.out.println("\"Lanjutkanlah toko ini, nak...\"");
        System.out.println();
        System.out.println("Tanpa banyak pilihan, kamu menerima tawaran itu.");
        System.out.println("Hari ini adalah hari pertamamu sebagai penjaga toko baru.");
        System.out.println();
        System.out.println("Apakah kamu bisa mengembalikan kejayaan toko ini?");
        System.out.println("Tekan Enter untuk memulai perjalananmu...");
        scanner.nextLine();
    }
    
    //save & load
   public void saveGame() {
    try {
        String filename = player.getName() + ".txt"; // <-- INI YANG BARU
        java.io.FileWriter writer = new java.io.FileWriter(filename);

        writer.write(player.getName() + "\n");
        writer.write(player.getHp() + "\n");
        writer.write(player.getMaxHp() + "\n");
        writer.write(player.getGold() + "\n");
        writer.write(player.getReputation() + "\n");
        writer.write(day + "\n");

        writer.close();
        System.out.println("Game disimpan sebagai: " + filename);
    } catch (Exception e) {
        System.out.println("Gagal save!");
    }
}
   
    public void loadGame() {
    try {
        System.out.print("Masukkan nama save: ");
        String name = scanner.nextLine();

        java.io.File file = new java.io.File(name + ".txt");

        if (!file.exists()) {
            System.out.println("Save tidak ditemukan!");
            return;
        }

        java.util.Scanner sc = new java.util.Scanner(file);

        String playerName = sc.nextLine();
        int hp = Integer.parseInt(sc.nextLine());
        int maxHp = Integer.parseInt(sc.nextLine());
        int gold = Integer.parseInt(sc.nextLine());
        int rep = Integer.parseInt(sc.nextLine());
        int savedDay = Integer.parseInt(sc.nextLine());

        player = new Player(playerName);
        player.setHp(hp);
        player.setMaxHp(maxHp);
        player.setGold(gold);
        player.setReputation(rep);

        day = savedDay;

        sc.close();
        System.out.println("Game berhasil di-load!");
    } catch (Exception e) {
        System.out.println("Gagal load!");
    }
}

    // ?????? Menu Harian ?????????????????????????????????????????????????????????????????????????????????????????????
    private void dailyMenu() {
        // Snapshot di awal hari untuk ringkasan nanti
        player.snapshotDayStart();

        // Tampilkan story beat kalau ada di hari ini (hanya sekali)
        if (!storyToldToday) {
            storyManager.checkStory(day, player);
            storyToldToday = true;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.printf("          HARI KE-%d / 40", day);
        printSpecialDayLabel();
        System.out.println("\n" + "=".repeat(50));
        player.showStatus();

        System.out.println("Apa yang akan kamu lakukan hari ini?");
        System.out.println("1. " + (shopOpenedToday ? "[Toko sudah dibuka hari ini]"
                                                     : "Buka Toko          (Layani customer)"));
        System.out.println("2. Beli Stok               (Isi barang dagangan)");
        System.out.println("3. Kelola Stok             (Lihat / buang item)");
        System.out.println("4. Upgrade Toko            (Butuh Gold)");
        System.out.println("5. Kelola Soldier          (Rekrut / Lihat)");
        System.out.println("6. Akhiri Hari             (tidur)");
        System.out.println("7. Keluar Game");
        System.out.println("8. Save Game");
        System.out.print("Pilih (1-7): ");

        int choice = getChoice(1, 8);
        switch (choice) {
            case 1 -> { if (!shopOpenedToday) openShopForTheDay();
                        else System.out.println("Toko sudah dibuka hari ini."); }
            case 2 -> shop.buyStockMenu(player);
            case 3 -> shop.manageInventory(player);
            case 4 -> shop.upgradeMenu(player);
            case 5 -> manageSoldiers();
            case 6 -> endTheDay();
            case 7 -> { System.out.println("Terima kasih telah bermain di Rudoria."); gameRunning = false; }
            case 8 -> saveGame();
        }
    }

    // ?????? Hari Spesial ??????????????????????????????????????????????????????????????????????????????????????????
    private void printSpecialDayLabel() {
        if (day % 10 == 0) System.out.print("  [HARI BOSS!]");
        else if (day % 5 == 0) System.out.print("  [HARI PASAR]");
        else if (day == 35)    System.out.print("  [HARI NAGA!]");
    }

    private boolean isMarketDay() { return day % 5 == 0 && day % 10 != 0; }
    private boolean isBossDay()   { return day % 10 == 0; }

    // ?????? Buka Toko ???????????????????????????????????????????????????????????????????????????????????????????????????
    private void openShopForTheDay() {
        shopOpenedToday = true;
        eventSystem.resetDailyEvents();

        // Hari Pasar: semua customer hadir, bonus gold
        if (isMarketDay()) {
            System.out.println("\n== HARI PASAR! Desa ramai, lebih banyak pembeli hari ini! ==\n");
        } else {
            System.out.println("\nToko dibuka... Angin pagi bertiup pelan.");
            System.out.println("Customer mulai berdatangan.\n");
        }

        // MAX 3 customer ??? naik 1 tiap 2 level, tapi tetap max 3
        int customerCount = Math.min(2 + (player.getLevel() / 2), 3);
        // Hari pasar: bonus 1 customer
        if (isMarketDay()) customerCount = Math.min(customerCount + 1, 4);

        for (int i = 1; i <= customerCount; i++) {
            System.out.println("-".repeat(40));
            System.out.println("Customer ke-" + i + " dari " + customerCount);
            System.out.println("-".repeat(40));

            // Player layani customer ??? pilih item & set harga
            if (isMarketDay()) {
                shop.serveCustomer(player, true);
            } else {
                shop.serveCustomer(player, false);
            }

            eventSystem.triggerEvent(player);

            // 15% chance combat siang setelah customer pertama
            if (i > 1 && Math.random() < 0.15) {
                System.out.println("\n[!!] Tiba-tiba ada yang menyerang toko di siang hari!");
                triggerCombat(chooseDayEnemy());
                if (!player.isAlive()) return;
            }
        }

        System.out.println("\nToko ditutup untuk hari ini.");
    }

    // ?????? Akhiri Hari ?????????????????????????????????????????????????????????????????????????????????????????????
    private void endTheDay() {
        // Tampilkan ringkasan hari ini
        player.showDailySummary(day);

        System.out.println("\nHari berakhir. Kamu menutup toko dan beristirahat.");

        // Bayar upah soldier
        payAllSoldierWages();

        // Pulihkan HP
        player.restHeal();

        // Event malam
        double nightRoll = Math.random();
        if (nightRoll < 0.20) {
            System.out.println("\nMalam ini terasa tidak tenang...");
            eventSystem.thiefEvent(player);
        } else if (nightRoll < 0.35) {
            System.out.println("\n[!!] Serangan malam! Musuh menyusup ke toko!");
            triggerCombat(chooseNightEnemy());
            if (!player.isAlive()) return;
        } else {
            System.out.println("Malam berlalu dengan tenang.");
        }

        // Bonus reputasi kalau toko dibuka hari ini
        if (shopOpenedToday) player.gainReputation(1);

        day++;
        shopOpenedToday = false;
        storyToldToday = false;

        // Boss day setiap 10 hari
        if (isBossDay()) {
            System.out.println("\n[BOSS] Ancaman besar mendekat ke desa Rudoria!");
            System.out.println("Tekan Enter untuk menghadapi boss...");
            scanner.nextLine();
            triggerCombat(Enemy.createKsatriaJahat());
            if (!player.isAlive()) return;
        }

        // Naga hari ke-35
        if (day == 35) {
            System.out.println("\n[NAGA] Langit memerah... Naga Rudoria telah terbangun!");
            System.out.println("Tekan Enter untuk menghadapi Naga...");
            scanner.nextLine();
            triggerCombat(Enemy.createNaga());
            if (!player.isAlive()) return;
        }

        // Cek menang
        if (player.getReputation() >= 92) {
            System.out.println("\nReputasi tokomu luar biasa! Seluruh Rudoria mengenalmu!");
            gameRunning = false;
        }
    }

    // ?????? Combat ????????????????????????????????????????????????????????????????????????????????????????????????????????????
    private void triggerCombat(Enemy enemy) {
        CombatSystem.startCombat(player, enemy, availableSoldiers);
        if (!player.isAlive()) {
            System.out.println("\nHP kamu habis. GAME OVER.");
            gameRunning = false;
        }
    }

    private Enemy chooseDayEnemy() {
        return Math.random() < 0.6 ? Enemy.createBandit() : Enemy.createPreman();
    }

    private Enemy chooseNightEnemy() {
        return Math.random() < 0.65 ? Enemy.createPreman() : Enemy.createKsatriaJahat();
    }

    // ?????? Soldier ?????????????????????????????????????????????????????????????????????????????????????????????????????????
    private void manageSoldiers() {
        while (true) {
            System.out.println("\n=== KELOLA SOLDIER ===");
            for (int i = 0; i < availableSoldiers.size(); i++) {
                System.out.println((i + 1) + ".");
                availableSoldiers.get(i).showInfo();
                System.out.println();
            }
            System.out.println((availableSoldiers.size() + 1) + ". Kembali");
            System.out.print("Pilih: ");

            int choice = getChoice(1, availableSoldiers.size() + 1);
            if (choice == availableSoldiers.size() + 1) break;

            Soldier chosen = availableSoldiers.get(choice - 1);
            if (!chosen.isRecruited()) chosen.recruit(player);
            else System.out.println(chosen.getName() + " sudah bersamamu.");
        }
    }

    private void payAllSoldierWages() {
        boolean any = availableSoldiers.stream().anyMatch(Soldier::isRecruited);
        if (!any) return;
        System.out.println("\nMembayar upah soldier...");
        for (Soldier s : availableSoldiers) {
            if (s.isRecruited()) s.payDailyWage(player);
        }
    }

    // ?????? Final Ending ??????????????????????????????????????????????????????????????????????????????????????????
    private void showFinalEnding() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    AKHIR CERITA");
        System.out.println("=".repeat(60));

        if (!player.isAlive()) {
            System.out.println("\nKamu gugur dalam pertempuran di hari ke-" + day + ".");
            System.out.println("Toko Rudoria kembali sunyi tanpa penjaganya...");
        } else if (player.getReputation() >= 90) {
            System.out.println("\nBeberapa bulan kemudian...");
            System.out.println("Pemilik toko lama kembali berkunjung ke desa.");
            System.out.println("Ia terkejut melihat toko yang dulu sepi kini ramai.");
            System.out.println();
            System.out.println("*** SELAMAT! KAMU BERHASIL MENGEMBALIKAN KEJAYAAN TOKO RUDORIA! ***");
        } else if (player.getReputation() >= 60) {
            System.out.println("\nToko kamu sudah cukup terkenal di desa.");
            System.out.println("Perjalananmu belum selesai, tapi kamu sudah membuktikan dirimu.");
        } else {
            System.out.println("\nSetelah " + (day - 1) + " hari, toko masih bertahan.");
            System.out.println("Perjalananmu masih panjang untuk mengembalikan kejayaannya.");
        }

        System.out.println();
        player.showStatus();
        System.out.println("Terima kasih telah bermain!");
    }

    // ?????? Helper ????????????????????????????????????????????????????????????????????????????????????????????????????????????
    private int getChoice(int min, int max) {
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
