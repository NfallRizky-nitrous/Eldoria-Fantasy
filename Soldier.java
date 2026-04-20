public class Soldier {
    private int cost = 20;
    private double successRate = 0.6;

    public boolean defend(Player player) {
        if (player.getGold() < cost) {
            System.out.println("Gold tidak cukup!");
            return false;
        }

        player.spendGold(cost);

        if (Math.random() < successRate) {
            System.out.println("Prajurit berhasil!");
            return true;
        } else {
            System.out.println("Prajurit gagal...");
            return false;
        }
    }
}
