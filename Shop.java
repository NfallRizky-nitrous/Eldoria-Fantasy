/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import java.util.ArrayList;
import java.util.Scanner;

public class Shop {
    private Scanner scanner = new Scanner(System.in);

    // Katalog item yang bisa dibeli sebagai stok
    private ArrayList<Item> catalog;

    // Upgrade toko
    private int rackLevel    = 0;
    private int signLevel    = 0;
    private int storageLevel = 0;
    
 public ArrayList<Item> getCatalog() {
    return catalog;
}
    
public String getCatalogText(Player player) {
    StringBuilder sb = new StringBuilder();

    sb.append("=== TOKO ===\n");
    sb.append("Gold kamu : ").append(player.getGold()).append("\n");
    sb.append("Stok      : ")
      .append(player.getInventory().size())
      .append(" / ")
      .append(getMaxStock())
      .append("\n\n");

    sb.append("Katalog:\n");
    sb.append("----------------------------------\n");

    for (int i = 0; i < catalog.size(); i++) {
        Item item = catalog.get(i);
        int profit = item.getSellPrice() - item.getBuyPrice();

        sb.append(i + 1).append(". ")
          .append(item.getName())
          .append(" | Beli: ").append(item.getBuyPrice())
          .append(" | Jual: ").append(item.getSellPrice())
          .append(" | Profit: +").append(profit)
          .append("\n");
    }

    sb.append("----------------------------------\n");
    sb.append("Klik tombol 1-6 untuk beli item\n");

    return sb.toString();
}



    // Kapasitas stok maksimal (naik seiring upgrade gudang)
    public int getMaxStock() { return 10 + (storageLevel * 5); }

    public Shop() {
        catalog = new ArrayList<>();
        // nama, hargaBeli(modal), hargaJual(ke customer), deskripsi
        catalog.add(new Item("Potion Kesehatan", 25,  45,  "Memulihkan HP"));
        catalog.add(new Item("Potion Mana",      30,  55,  "Memulihkan mana mage"));
        catalog.add(new Item("Potion Kekuatan",  40,  70,  "Meningkatkan kekuatan sementara"));
        catalog.add(new Item("Antidote",         20,  35,  "Menghilangkan efek racun"));
        catalog.add(new Item("Elixir Besar",     80,  140, "Potion langka yang sangat powerful"));
        catalog.add(new Item("Herbal Remedy",    15,  28,  "Obat herbal penyembuhan ringan"));
    }

    // ?????? BELI STOK (player beli dari supplier) ?????????????????????????????????????????????????????????
    public void buyStockMenu(Player player) {
        while (true) {
            System.out.println("\n=== BELI STOK ===");
            System.out.println("Gold kamu : " + player.getGold());
            System.out.println("Stok      : " + player.getInventory().size() + " / " + getMaxStock());
            System.out.println();
            System.out.println("Katalog barang (harga modal):");
            System.out.println("-".repeat(50));
            for (int i = 0; i < catalog.size(); i++) {
                Item item = catalog.get(i);
                int profit = item.getSellPrice() - item.getBuyPrice();
                System.out.printf("  %d. %-20s Beli: %3dg  Jual: %3dg  Profit: +%dg%n",
                    i + 1, item.getName(), item.getBuyPrice(), item.getSellPrice(), profit);
            }
            System.out.println("-".repeat(50));
            System.out.println((catalog.size() + 1) + ". Kembali");
            System.out.print("Pilih item (0 = batal): ");

            int choice = getChoice(0, catalog.size() + 1);
            if (choice == 0 || choice == catalog.size() + 1) break;

            Item selected = catalog.get(choice - 1);

            // Cek kapasitas stok
            if (player.getInventory().size() >= getMaxStock()) {
                System.out.println("Gudang penuh! Upgrade gudang atau jual stok dulu.");
                continue;
            }

            // Cek gold
            if (player.getGold() < selected.getBuyPrice()) {
                System.out.println("Gold tidak cukup! Butuh " + selected.getBuyPrice() + " gold.");
                continue;
            }

            // Beli stok ??? potong gold, masuk inventory
            player.spendGold(selected.getBuyPrice());
            player.addItem(new Item(selected.getName(), selected.getBuyPrice(),
                                    selected.getSellPrice(), selected.getDescription()));
            System.out.println("[OK] " + selected.getName() + " masuk ke stok toko.");
        }
    }

    // ?????? PROSES CUSTOMER (player pilih item + set harga) ??????????????????????????????
    public void serveCustomer(Player player, boolean marketDay) {
        ArrayList<Item> stock = player.getInventory();

        if (stock.isEmpty()) {
            System.out.println("Stok toko kosong! Customer kecewa dan pergi.");
            player.loseReputation(2);
            return;
        }

        // Generate request customer
        Item requested = generateCustomerRequest(stock);
        int customerBudget = requested.getSellPrice() + (int)(Math.random() * 30) - 10;
        if (marketDay) customerBudget = (int)(customerBudget * 1.3);

        System.out.println("Customer mencari  : " + requested.getName());
        System.out.println("Budget customer   : ~" + customerBudget + " gold");
        System.out.println("Harga normal      : " + requested.getSellPrice() + " gold");
        System.out.println();

        // Tampilkan stok yang dimiliki
        System.out.println("Stok kamu:");
        System.out.println("-".repeat(45));
        for (int i = 0; i < stock.size(); i++) {
            Item item = stock.get(i);
            System.out.printf("  %d. %-20s (modal: %dg, harga normal: %dg)%n",
                i + 1, item.getName(), item.getBuyPrice(), item.getSellPrice());
        }
        System.out.println("-".repeat(45));
        System.out.print("Tawarkan item nomor berapa? (0 = tolak customer): ");

        int itemChoice = getChoice(0, stock.size());
        if (itemChoice == 0) {
            System.out.println("Kamu menolak melayani customer.");
            player.loseReputation(1);
            return;
        }

        Item offered = stock.get(itemChoice - 1);

        // Player set harga jual
        System.out.print("Masukkan harga jual (modal kamu: " + offered.getBuyPrice() + "g): ");
        int askingPrice = getPriceInput(offered.getBuyPrice());

        // Logika tawar-menawar
        processTransaction(player, stock, itemChoice - 1, offered, askingPrice, customerBudget, marketDay);
    }

    private void processTransaction(Player player, ArrayList<Item> stock, int index,
                                     Item offered, int askingPrice, int budget, boolean marketDay) {
        int normalPrice = offered.getSellPrice();
        int modal = offered.getBuyPrice();

        if (askingPrice <= modal) {
            // Jual rugi
            stock.remove(index);
            player.gainGold(askingPrice);
            player.gainExp(3);
            int loss = modal - askingPrice;
            System.out.println("Customer langsung setuju! Tapi kamu jual di bawah modal...");
            System.out.println("Rugi " + loss + " gold dari modal.");
            player.loseReputation(1);

        } else if (askingPrice <= budget) {
            // Deal normal
            stock.remove(index);
            player.gainGold(askingPrice);
            player.gainExp(5);
            int profit = askingPrice - modal;
            System.out.println("Customer setuju! Profit: +" + profit + " gold.");
            if (askingPrice >= normalPrice) {
                player.gainReputation(1);
            }

        } else if (askingPrice <= budget + 15) {
            // Sedikit mahal ??? customer menawar
            int counterOffer = budget;
            System.out.println("Customer mengerutkan dahi...");
            System.out.println("\"Mahal sekali. Bagaimana kalau " + counterOffer + " gold?\"");
            System.out.println("1. Terima tawaran (" + counterOffer + " gold)");
            System.out.println("2. Tetap di harga " + askingPrice + " gold");
            System.out.println("3. Turunkan ke harga normal (" + normalPrice + " gold)");
            System.out.print("Pilih: ");
            int neg = getChoice(1, 3);

            if (neg == 1) {
                stock.remove(index);
                player.gainGold(counterOffer);
                player.gainExp(5);
                System.out.println("Deal di " + counterOffer + " gold. Profit: +" + (counterOffer - modal) + " gold.");
            } else if (neg == 2) {
                System.out.println("Customer pergi karena harga terlalu tinggi.");
                player.loseReputation(1);
            } else {
                stock.remove(index);
                player.gainGold(normalPrice);
                player.gainExp(5);
                player.gainReputation(1);
                System.out.println("Customer senang dengan harga wajar! Profit: +" + (normalPrice - modal) + " gold.");
            }

        } else {
            // Terlalu mahal ??? customer langsung pergi
            System.out.println("Customer menggeleng dan langsung pergi.");
            System.out.println("Harga kamu terlalu jauh dari budget mereka.");
            player.loseReputation(2);
        }
    }

    // Customer minta item yang ada di stok player
    private Item generateCustomerRequest(ArrayList<Item> stock) {
        return stock.get((int)(Math.random() * stock.size()));
    }

    // ?????? KELOLA STOK (lihat inventory, buang item rusak) ???????????????????????????
    public void manageInventory(Player player) {
        while (true) {
            System.out.println("\n=== KELOLA STOK TOKO ===");
            System.out.println("Kapasitas: " + player.getInventory().size() + " / " + getMaxStock());
            player.showInventory();

            if (player.getInventory().isEmpty()) {
                System.out.println("Stok kosong. Beli stok dulu sebelum buka toko!");
                return;
            }

            System.out.println("1. Buang item (hapus dari stok)");
            System.out.println("2. Kembali");
            System.out.print("Pilih: ");
            int choice = getChoice(1, 2);
            if (choice == 2) break;

            System.out.print("Nomor item yang ingin dibuang (0 = batal): ");
            int idx = getChoice(0, player.getInventory().size());
            if (idx == 0) continue;

            Item removed = player.getInventory().remove(idx - 1);
            System.out.println(removed.getName() + " dibuang dari stok.");
        }
    }

    // ?????? UPGRADE TOKO ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public void upgradeMenu(Player player) {
        System.out.println("\n=== UPGRADE TOKO ===");
        System.out.println("Gold kamu: " + player.getGold());
        System.out.println();
        System.out.printf("  1. Rak Penyimpanan  (Lv %d/3) - Item tidak mudah rusak        | %dg%n", rackLevel, rackUpgradeCost());
        System.out.printf("  2. Papan Nama       (Lv %d/3) - Lebih banyak customer/hari    | %dg%n", signLevel, signUpgradeCost());
        System.out.printf("  3. Gudang           (Lv %d/3) - Kapasitas stok +5 per level   | %dg%n", storageLevel, storageUpgradeCost());
        System.out.println("  4. Kembali");
        System.out.print("Pilih: ");

        int choice = getChoice(1, 4);
        switch (choice) {
            case 1 -> doUpgrade(player, "Rak Penyimpanan", rackLevel,    rackUpgradeCost(),     () -> rackLevel++);
            case 2 -> doUpgrade(player, "Papan Nama",      signLevel,    signUpgradeCost(),     () -> signLevel++);
            case 3 -> doUpgrade(player, "Gudang",          storageLevel, storageUpgradeCost(),  () -> storageLevel++);
            case 4 -> { return; }
        }
    }

    private void doUpgrade(Player player, String name, int currentLevel, int cost, Runnable levelUp) {
        if (currentLevel >= 3) { System.out.println(name + " sudah maksimal!"); return; }
        if (player.getGold() < cost) { System.out.println("Gold tidak cukup! Butuh " + cost + " gold."); return; }
        player.spendGold(cost);
        levelUp.run();
        System.out.println("[OK] " + name + " berhasil diupgrade!");
    }

    public int getRackLevel()    { return rackLevel; }
    public int getSignLevel()    { return signLevel; }
    public int getStorageLevel() { return storageLevel; }

    private int rackUpgradeCost()    { return 100 + (rackLevel * 80); }
    private int signUpgradeCost()    { return 120 + (signLevel * 90); }
    private int storageUpgradeCost() { return 80  + (storageLevel * 60); }

    // ?????? Helper ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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

    private int getPriceInput(int minPrice) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input > 0) return input;
                System.out.print("Harga harus lebih dari 0: ");
            } catch (Exception e) {
                scanner.nextLine();
                System.out.print("Masukkan angka: ");
            }
        }
    }
}