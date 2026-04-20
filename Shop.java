import java.util.ArrayList;
import java.util.Scanner;

public class Shop {
    private ArrayList<Item> itemsForSale;
    private Scanner scanner = new Scanner(System.in);

    // Upgrade toko: level upgrade (0 = belum, max 3)
    private int rackLevel = 0;      // Rak bagus → item rusak berkurang
    private int signLevel = 0;      // Papan nama → lebih banyak customer
    private int storageLevel = 0;   // Gudang → inventory lebih besar

    public Shop() {
        itemsForSale = new ArrayList<>();
        itemsForSale.add(new Item("Potion Kesehatan", 25, 45, "Memulihkan HP pemain"));
        itemsForSale.add(new Item("Potion Mana",      30, 55, "Memulihkan mana mage"));
        itemsForSale.add(new Item("Potion Kekuatan",  40, 70, "Meningkatkan kekuatan sementara"));
        itemsForSale.add(new Item("Antidote",         20, 35, "Menghilangkan efek racun"));
        itemsForSale.add(new Item("Elixir Besar",     80, 140, "Potion langka yang sangat powerful"));
        itemsForSale.add(new Item("Herbal Remedy",    15, 28, "Obat herbal untuk penyembuhan ringan"));
    }

    // Dipanggil tiap customer datang — customer beli & item masuk inventory player
    public void sellRandomItem(Player player) {
        if (itemsForSale.isEmpty()) {
            System.out.println("Toko sedang kehabisan stok...");
            return;
        }

        int randomIndex = (int) (Math.random() * itemsForSale.size());
        Item item = itemsForSale.get(randomIndex);

        // Buat salinan item supaya tidak share referensi yang sama
        Item sold = new Item(item.getName(), item.getBuyPrice(), item.getSellPrice(), item.getDescription());

        System.out.println("Seorang customer membeli " + sold.getName());
        player.gainGold(sold.getSellPrice());
        player.gainExp(5);
        player.addItem(sold); // ← INI yang sebelumnya hilang!

        // 30% chance reputasi naik karena pelayanan bagus
        if (Math.random() < 0.30) {
            player.gainReputation(1);
        }
    }

    // Menu kelola inventory — lihat, jual, atau gunakan item
    public void manageInventory(Player player) {
        while (true) {
            System.out.println("\n=== KELOLA INVENTORY ===");
            player.showInventory();

            if (player.getInventory().isEmpty()) {
                System.out.println("(Tidak ada yang bisa dilakukan, inventory kosong)");
                return;
            }

            System.out.println("\n1. Jual item");
            System.out.println("2. Kembali");
            System.out.print("Pilih: ");

            int choice = getChoice(1, 2);
            if (choice == 1) {
                sellItemFromInventory(player);
            } else {
                break;
            }
        }
    }

    private void sellItemFromInventory(Player player) {
        ArrayList<Item> inv = player.getInventory();
        if (inv.isEmpty()) {
            System.out.println("Inventory kosong.");
            return;
        }

        System.out.print("Pilih nomor item yang ingin dijual (0 = batal): ");
        int choice = getChoice(0, inv.size());
        if (choice == 0) return;

        Item itemToSell = inv.get(choice - 1);
        int sellPrice = itemToSell.getSellPrice();
        player.gainGold(sellPrice);
        inv.remove(choice - 1);
        System.out.println("Berhasil menjual " + itemToSell.getName() + " seharga " + sellPrice + " gold!");
    }

    // Menu upgrade toko — efek nyata ke gameplay
    public void upgradeMenu(Player player) {
        System.out.println("\n=== UPGRADE TOKO ===");
        System.out.println("Gold kamu: " + player.getGold());
        System.out.println();
        System.out.println("1. Upgrade Rak  (Level " + rackLevel + "/3) — Kurangi risiko item rusak    | Biaya: " + rackUpgradeCost() + " gold");
        System.out.println("2. Upgrade Papan Nama  (Level " + signLevel + "/3) — Tambah customer per hari  | Biaya: " + signUpgradeCost() + " gold");
        System.out.println("3. Upgrade Gudang  (Level " + storageLevel + "/3) — Tampilkan kapasitas gudang  | Biaya: " + storageUpgradeCost() + " gold");
        System.out.println("4. Kembali");
        System.out.print("Pilih: ");

        int choice = getChoice(1, 4);
        switch (choice) {
            case 1 -> doUpgrade(player, "rak", rackLevel, rackUpgradeCost(), () -> rackLevel++);
            case 2 -> doUpgrade(player, "papan nama", signLevel, signUpgradeCost(), () -> signLevel++);
            case 3 -> doUpgrade(player, "gudang", storageLevel, storageUpgradeCost(), () -> storageLevel++);
            case 4 -> { return; }
        }
    }

    private void doUpgrade(Player player, String name, int currentLevel, int cost, Runnable levelUp) {
        if (currentLevel >= 3) {
            System.out.println("Upgrade " + name + " sudah maksimal!");
            return;
        }
        if (player.getGold() < cost) {
            System.out.println("Gold tidak cukup! Butuh " + cost + " gold.");
            return;
        }
        player.spendGold(cost);
        levelUp.run();
        System.out.println("✅ Upgrade " + name + " berhasil!");
    }

    // Getter untuk dipakai Event.java (rak rusak lebih jarang kalau diupgrade)
    public int getRackLevel()    { return rackLevel; }
    public int getSignLevel()    { return signLevel; }
    public int getStorageLevel() { return storageLevel; }

    private int rackUpgradeCost()    { return 100 + (rackLevel * 80); }
    private int signUpgradeCost()    { return 120 + (signLevel * 90); }
    private int storageUpgradeCost() { return 80  + (storageLevel * 60); }

    private int getChoice(int min, int max) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                int input = sc.nextInt();
                if (input >= min && input <= max) return input;
                System.out.print("Pilih antara " + min + "-" + max + ": ");
            } catch (Exception e) {
                sc.nextLine();
                System.out.print("Masukkan angka yang valid: ");
            }
        }
    }
}
