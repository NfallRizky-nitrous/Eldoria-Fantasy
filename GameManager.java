import java.util.Scanner;

public class GameManager {
    private Player player;
    private Shop shop;
    private Event eventSystem;
    private Scanner scanner = new Scanner(System.in);
    private int day = 1;
    private boolean gameRunning = true;
    private boolean shopOpenedToday = false; // Cegah buka toko 2x sehari

    public GameManager() {
        System.out.print("Masukkan nama penjaga toko: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Penjaga";

        player = new Player(name);
        shop = new Shop();
        eventSystem = new Event();
    }

    public void startGame() {
        printIntroStory();
        while (gameRunning && day <= 40) {
            dailyMenu();
        }
        showFinalEnding();
    }

    private void printIntroStory() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          SELAMAT DATANG DI ELDORIA");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("Di sebuah desa kecil yang tenang di dunia Eldoria,");
        System.out.println("terdapat sebuah toko tua yang dulu sangat terkenal.");
        System.out.println();
        System.out.println("Suatu hari, seorang kakek renta pemilik toko berkata padamu:");
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

    private void dailyMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                    HARI KE-" + day);
        System.out.println("=".repeat(50));
        player.showStatus();

        System.out.println("Apa yang akan kamu lakukan hari ini?");
        if (!shopOpenedToday) {
            System.out.println("1. Buka Toko Hari Ini     (Jual beli + Event)");
        } else {
            System.out.println("1. [Toko sudah dibuka hari ini]");
        }
        System.out.println("2. Kelola Inventory");
        System.out.println("3. Upgrade Toko              (Butuh Gold)");
        System.out.println("4. Akhiri Hari");
        System.out.println("5. Keluar Game");
        System.out.print("Pilih (1-5): ");

        int choice = getChoice(1, 5);

        switch (choice) {
            case 1 -> {
                if (!shopOpenedToday) openShopForTheDay();
                else System.out.println("Toko sudah dibuka hari ini. Istirahat dulu atau akhiri hari.");
            }
            case 2 -> shop.manageInventory(player);
            case 3 -> shop.upgradeMenu(player);
            case 4 -> endTheDay();
            case 5 -> {
                System.out.println("Terima kasih telah bermain di Eldoria.");
                gameRunning = false;
            }
        }
    }

    private void openShopForTheDay() {
        shopOpenedToday = true;
        eventSystem.resetDailyEvents(); // PENTING: reset event tiap hari baru dibuka

        System.out.println("\nToko dibuka... Angin pagi bertiup pelan.");
        System.out.println("Customer mulai berdatangan ke toko tua ini.\n");

        // Customer bertambah seiring level & reputasi
        int baseCustomer = 3 + player.getLevel();
        int bonusFromRep = player.getReputation() / 20;
        int customerCount = Math.min(baseCustomer + bonusFromRep, 8); // max 8 customer

        for (int i = 1; i <= customerCount; i++) {
            System.out.println("-".repeat(40));
            System.out.println("Customer ke-" + i + " dari " + customerCount);
            System.out.println("-".repeat(40));

            shop.sellRandomItem(player); // customer beli → item masuk inventory player
            eventSystem.triggerEvent(player); // event: mungkin muncul, mungkin tidak
        }

        System.out.println("\nToko ditutup untuk hari ini. Hari yang melelahkan.");
    }

    private void endTheDay() {
        System.out.println("\nHari berakhir. Kamu menutup toko dan beristirahat.");

        // Event malam: 40% chance pencuri
        if (Math.random() < 0.40) {
            System.out.println("\nMalam ini terasa tidak tenang...");
            eventSystem.thiefEvent(player);
        } else {
            System.out.println("Malam berlalu dengan tenang.");
        }

        // Bonus reputasi kecil tiap hari toko dibuka
        if (shopOpenedToday) {
            player.gainReputation(1);
            System.out.println("(Reputasi toko perlahan naik karena konsisten buka setiap hari)");
        }

        day++;
        shopOpenedToday = false; // reset untuk hari berikutnya

        // Cek kondisi menang
        if (player.getReputation() >= 92) {
            System.out.println("\nReputasi tokomu sudah luar biasa! Eldoria mengenalmu!");
            gameRunning = false;
        }
    }

    private void showFinalEnding() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    AKHIR CERITA");
        System.out.println("=".repeat(60));

        if (player.getReputation() >= 90) {
            System.out.println("\nBeberapa bulan kemudian...");
            System.out.println("Pemilik toko yang lama kembali berkunjung ke desa.");
            System.out.println("Ia terkejut melihat toko yang dulu sepi kini ramai dipenuhi");
            System.out.println("petualang, knight, mage, dan customer dari berbagai penjuru.");
            System.out.println();
            System.out.println("Dengan mata berkaca-kaca, ia berkata:");
            System.out.println("\"Kamu telah melakukan sesuatu yang bahkan aku tidak bisa lakukan.\"");
            System.out.println();
            System.out.println("✨ SELAMAT! KAMU BERHASIL MENGEMBALIKAN KEJAYAAN TOKO ELDORIA! ✨");
        } else if (player.getReputation() >= 60) {
            System.out.println("\nToko kamu sudah cukup terkenal di desa.");
            System.out.println("Meski belum berjaya penuh, perjalananmu tidak sia-sia.");
            System.out.println("Mungkin suatu hari nanti...");
        } else {
            System.out.println("\nSetelah " + (day - 1) + " hari, toko masih bertahan.");
            System.out.println("Namun perjalananmu masih panjang untuk mengembalikan kejayaannya.");
        }

        System.out.println();
        player.showStatus();
        System.out.println("Terima kasih telah bermain!");
    }

    private int getChoice(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) return input;
                System.out.print("Pilih antara " + min + "-" + max + ": ");
            } catch (Exception e) {
                scanner.nextLine();
                System.out.print("Masukkan angka yang valid: ");
            }
        }
    }
}
