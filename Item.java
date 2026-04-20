public class Item {
    private String name;
    private int buyPrice;
    private int sellPrice;
    private String description;

    public Item(String name, int buyPrice, int sellPrice, String description) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.description = description;
    }

    // Getter
    public String getName() {
        return name;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public String getDescription() {
        return description;
    }

    // Method untuk menampilkan info barang
    public void showInfo() {
        System.out.println(name + " | Beli: " + buyPrice + "g | Jual: " + sellPrice + "g");
        System.out.println("   └─ " + description);
    }
}

