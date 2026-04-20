import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Event {
    private final Random random = new Random();
    private final Scanner scanner = new Scanner(System.in);

    // Daftar event yang sudah muncul hari ini — reset tiap hari baru
    private final List<String> usedEventsToday = new ArrayList<>();

    // Dipanggil di awal hari baru (dari Shop/Game loop)
    public void resetDailyEvents() {
        usedEventsToday.clear();
    }

    // Ambil 1 event acak untuk customer ini
    // Kembalikan false kalau tidak ada event (customer datang normal saja)
    public boolean triggerEvent(Player player) {
        int reputation = player.getReputation();

        // Buat pool event yang BELUM muncul hari ini
        List<String> available = new ArrayList<>();

        // Bobot event disesuaikan reputasi
        // Reputasi rendah  → lebih banyak event negatif
        // Reputasi tinggi  → lebih banyak event positif

        if (!usedEventsToday.contains("poorCustomer"))
            available.add("poorCustomer");

        if (!usedEventsToday.contains("angryCustomer"))
            available.add("angryCustomer");

        if (!usedEventsToday.contains("brokenItem"))
            available.add("brokenItem");

        if (!usedEventsToday.contains("thiefEvent"))
            available.add("thiefEvent");

        if (!usedEventsToday.contains("gangsterEvent"))
            available.add("gangsterEvent");

        // Event positif hanya masuk pool kalau reputasi cukup tinggi
        if (reputation >= 25 && !usedEventsToday.contains("vipCustomer"))
            available.add("vipCustomer");

        if (reputation >= 40 && !usedEventsToday.contains("illegalDeal"))
            available.add("illegalDeal");

        // Hitung peluang event muncul hari ini untuk customer ini
        // Makin banyak customer sudah lewat, makin besar peluang event
        int eventChance = 30 + (usedEventsToday.size() * 5); // 30%, 35%, 40%, dst
        if (random.nextInt(100) >= eventChance || available.isEmpty()) {
            return false; // Customer ini tidak dapat event
        }

        // Pilih event acak dari pool yang tersedia
        Collections.shuffle(available, random);
        String chosenEvent = available.get(0);
        usedEventsToday.add(chosenEvent);

        // Jalankan event
        switch (chosenEvent) {
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

    // ─────────────────────────────────────────
    // EVENT DEFINITIONS
    // ─────────────────────────────────────────

    // 1. Customer Miskin (pilihan moral)
    private void poorCustomer(Player player) {
        System.out.println("\n Seorang ibu desa datang dengan wajah cemas.");
        System.out.println("Anaknya sakit dan butuh potion, tapi dia hanya punya 10 gold...");
        System.out.println("1. Kasih potion secara gratis");
        System.out.println("2. Tetap jual dengan harga normal");
        int choice = getChoice(1, 2);

        if (choice == 1) {
            System.out.println("Ibu itu menangis haru dan berterima kasih padamu.");
            player.gainReputation(5);
            player.gainExp(12);
        } else {
            System.out.println("Kamu tetap menjualnya. Ibu itu pergi dengan wajah sedih.");
            player.gainGold(20);
            player.loseReputation(2);
        }
    }

    // 2. Customer Marah (maksimal 1x per hari)
    private void angryCustomer(Player player) {
        System.out.println("\n Seorang knight marah besar karena potion yang dibelinya kemarin tidak manjur.");
        System.out.println("1. Turunkan harga & minta maaf");
        System.out.println("2. Tolak dan suruh pergi");
        int choice = getChoice(1, 2);

        if (choice == 1) {
            System.out.println("Knight itu akhirnya tenang dan mau beli lagi dengan harga diskon.");
            player.gainGold(15);
            player.gainReputation(3);
        } else {
            System.out.println("Knight itu pergi sambil mengumpat. Beberapa orang di desa mendengarnya.");
            player.loseReputation(4);
        }
    }

    // 3. Barang Rusak
    private void brokenItem(Player player) {
        System.out.println("\n Beberapa potion pecah di rak karena rak tua yang rapuh.");
        System.out.println("Kamu kehilangan barang senilai 25 gold.");
        player.spendGold(25);
    }

    // 4. Pencuri
    public void thiefEvent(Player player) {
        System.out.println("\n MALAM INI - Suara mencurigakan terdengar di belakang toko!");
        System.out.println("Seorang pencuri mencoba membobol gudang!");
        System.out.println("1. Panggil prajurit kerajaan");
        System.out.println("2. Abaikan dan diam saja");
        int choice = getChoice(1, 2);

        if (choice == 1) {
            int successChance = 70 + (player.getLevel() * 5);
            if (random.nextInt(100) < successChance) {
                System.out.println("Prajurit datang cepat dan menangkap pencuri!");
                player.gainGold(60);
                player.gainExp(20);
                player.gainReputation(3);
            } else {
                System.out.println("Prajurit terlambat... Pencuri kabur membawa beberapa barang.");
                player.spendGold(45);
                player.loseReputation(2);
            }
        } else {
            System.out.println("Kamu memilih diam. Pencuri berhasil mencuri 40 gold.");
            player.spendGold(40);
            player.loseReputation(5);
        }
    }

    // 5. Preman / Gangster
    private void gangsterEvent(Player player) {
        System.out.println("\n Seorang preman dari geng lokal datang dengan sombong.");
        System.out.println("\"Bayar pajak perlindungan kalau tidak mau toko rusak!\"");
        System.out.println("1. Bayar 30 gold");
        System.out.println("2. Panggil prajurit untuk melawan");
        int choice = getChoice(1, 2);

        if (choice == 1) {
            player.spendGold(30);
            System.out.println("Preman pergi sambil tertawa.");
            player.loseReputation(1);
        } else {
            if (random.nextInt(100) < 65) {
                System.out.println("Prajurit berhasil mengusir preman tersebut!");
                player.gainGold(50);
                player.gainExp(18);
                player.gainReputation(5);
            } else {
                System.out.println("Toko sedikit rusak akibat perkelahian...");
                player.spendGold(65);
                player.loseReputation(6);
            }
        }
    }

    // 6. VIP Customer (butuh reputasi >= 25)
    private void vipCustomer(Player player) {
        System.out.println("\n Seorang Mage terkenal dari kota besar masuk ke toko!");
        System.out.println("Dia membeli banyak barang langka.");
        player.gainGold(95);
        player.gainExp(25);
        player.gainReputation(4);
    }

    // 7. Tawaran Ilegal (butuh reputasi >= 40)
    private void illegalDeal(Player player) {
        System.out.println("\n Seorang pria berjubah hitam mendekatimu diam-diam.");
        System.out.println("Dia menawarkan \"barang spesial\" yang sangat mahal harganya.");
        System.out.println("1. Terima tawaran (keuntungan besar, tapi berisiko)");
        System.out.println("2. Tolak dengan sopan");
        int choice = getChoice(1, 2);

        if (choice == 1) {
            if (random.nextInt(100) < 55) {
                System.out.println("Kesepakatan berhasil! Kamu mendapat keuntungan besar.");
                player.gainGold(130);
                player.gainReputation(2);
            } else {
                System.out.println("Ternyata itu jebakan! Kamu hampir ditangkap petugas kerajaan.");
                player.spendGold(90);
                player.loseReputation(10);
            }
        } else {
            System.out.println("Kamu menolak tawaran itu. Pria itu pergi tanpa kata.");
        }
    }

    // ─────────────────────────────────────────
    // HELPER: input validation biar tidak crash
    // ─────────────────────────────────────────
    private int getChoice(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.println("Pilih antara " + min + " - " + max + ":");
            } catch (Exception e) {
                scanner.nextLine(); // buang input invalid
                System.out.println("Masukkan angka yang valid:");
            }
        }
    }
}
