import java.util.Random;
import java.util.Scanner;

public class Event {
    private final Random random = new Random();
    private final Scanner input = new Scanner(System.in);

    public void triggerEvent(Player player) {
        int event = random.nextInt(8);

        switch (event) {
            case 0 -> normalCustomer(player);
            case 1 -> thiefEvent(player);
            case 2 -> angryCustomer(player);
            case 3 -> vipCustomer(player);
            case 4 -> gangsterEvent(player);
            case 5 -> brokenItem(player);
            case 6 -> poorCustomer(player);
            case 7 -> illegalDeal(player);
        }
    }

    // 1. Customer Normal
    private void normalCustomer(Player player) {
        System.out.println("\nSeorang knight membeli potion!");
        player.gainGold(30);
        player.gainExp(10);
    }

    // 2. Pencuri
    private void thiefEvent(Player player) {
        Soldier s = new Soldier();

        System.out.println("\nPENCURI MASUK!");
        System.out.println("1. Panggil Prajurit");
        System.out.println("2. Abaikan");

        int choice = input.nextInt();

        if (choice == 1) {
            if (s.defend(player)) {
                System.out.println("Pencuri ditangkap!");
                player.gainGold(50);
                player.gainExp(20);
            } else {
                System.out.println("Pencuri kabur...");
            }
        } else {
            System.out.println("Kehilangan 30 gold!");
            player.spendGold(30);
        }
    }

    // 3. Customer Marah
    private void angryCustomer(Player player) {
        System.out.println("\nCustomer marah!");

        System.out.println("1. Turunkan harga");
        System.out.println("2. Tolak");

        int choice = input.nextInt();

        if (choice == 1) {
            System.out.println("Customer jadi beli (murah)");
            player.gainGold(15);
            player.gainExp(5);
        } else {
            System.out.println("Customer pergi...");
        }
    }

    // 4. VIP Customer
    private void vipCustomer(Player player) {
        System.out.println("\nVIP membeli banyak barang!");
        player.gainGold(80);
        player.gainExp(30);
    }

    // 5. Preman
    private void gangsterEvent(Player player) {
        Soldier s = new Soldier();

        System.out.println("\nPreman datang minta uang!");
        System.out.println("1. Bayar (aman)");
        System.out.println("2. Lawan (panggil prajurit)");

        int choice = input.nextInt();

        if (choice == 1) {
            player.spendGold(25);
        } else {
            if (s.defend(player)) {
                System.out.println("Preman kabur!");
                player.gainGold(40);
                player.gainExp(15);
            } else {
                System.out.println("Toko rusak!");
                player.spendGold(50);
            }
        }
    }

    // 6. Barang Rusak
    private void brokenItem(Player player) {
        System.out.println("\nBarang rusak!");
        player.spendGold(20);
    }

    // 7. Customer Miskin
    private void poorCustomer(Player player) {
        System.out.println("\nSeorang warga miskin butuh potion...");

        System.out.println("1. Kasih gratis");
        System.out.println("2. Tetap jual");

        int choice = input.nextInt();

        if (choice == 1) {
            System.out.println("Kamu membantu orang.");
            player.gainExp(15);
        } else {
            System.out.println("Kamu tetap jual.");
            player.gainGold(20);
        }
    }

    // 8. Barang Ilegal
    private void illegalDeal(Player player) {
        System.out.println("\nSeorang pria menawarkan barang ilegal...");

        System.out.println("1. Terima (untung besar)");
        System.out.println("2. Tolak");

        int choice = input.nextInt();

        if (choice == 1) {
            System.out.println("Kamu dapat banyak gold!");
            player.gainGold(100);
        } else {
            System.out.println("Kamu tetap aman.");
        }
    }
}
