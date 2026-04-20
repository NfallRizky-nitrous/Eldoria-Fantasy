import java.util.ArrayList;

public class Player {
    private String name;
    private int gold;
    private int exp;
    private int level;
    private int reputation;
    private ArrayList<Item> inventory;

    public Player(String name) {
        this.name       = name;
        this.gold       = 80;
        this.exp        = 0;
        this.level      = 1;
        this.reputation = 20;
        this.inventory  = new ArrayList<>();
    }

    // ── Getter ──────────────────────────────────
    public String getName()              { return name; }
    public int getGold()                 { return gold; }
    public int getExp()                  { return exp; }
    public int getLevel()                { return level; }
    public int getReputation()           { return reputation; }
    public ArrayList<Item> getInventory(){ return inventory; }

    // ── Gold ────────────────────────────────────
    public void gainGold(int amount) {
        if (amount <= 0) return;
        gold += amount;
        System.out.println("💰 Gold bertambah +" + amount + " (Total: " + gold + ")");
    }

    public void spendGold(int amount) {
        if (amount <= 0) return;
        if (gold >= amount) {
            gold -= amount;
            System.out.println("💸 Gold digunakan -" + amount + " (Sisa: " + gold + ")");
        } else {
            // Kalau tidak mampu bayar penuh, kurangi semua gold yang ada
            System.out.println("❌ Gold tidak cukup! Kehilangan semua gold (" + gold + ")");
            gold = 0;
        }
    }

    // ── EXP & Level Up ──────────────────────────
    public void gainExp(int amount) {
        if (amount <= 0) return;
        exp += amount;
        System.out.println("⭐ EXP bertambah +" + amount + " (" + exp + "/" + expNeeded() + ")");
        checkLevelUp();
    }

    // EXP yang dibutuhkan makin besar seiring level
    private int expNeeded() {
        return 100 + (level - 1) * 30;
    }

    private void checkLevelUp() {
        while (exp >= expNeeded()) {
            exp -= expNeeded();
            level++;
            System.out.println();
            System.out.println("╔══════════════════════════════╗");
            System.out.println("║   🎉  LEVEL UP! Level " + level + "   🎉  ║");
            System.out.println("╚══════════════════════════════╝");
            applyLevelUpBonus();
        }
    }

    // Efek nyata dari level up
    private void applyLevelUpBonus() {
        int goldBonus = level * 20;
        gainGold(goldBonus);
        System.out.println("► Bonus gold naik level: +" + goldBonus);
        System.out.println("► Customer per hari bertambah (level " + level + " → +" + level + " customer)");
        System.out.println("► Peluang sukses lawan pencuri/preman meningkat");
        if (level % 2 == 0) {
            gainReputation(3);
            System.out.println("► Reputasi toko ikut naik karena tokomu makin dikenal!");
        }
    }

    // ── Reputation ──────────────────────────────
    public void gainReputation(int amount) {
        if (amount <= 0) return;
        reputation = Math.min(reputation + amount, 100);
        System.out.println("🌟 Reputasi toko naik +" + amount + " (Sekarang: " + reputation + ")");
    }

    public void loseReputation(int amount) {
        reputation = Math.max(reputation - amount, 0);
        System.out.println("📉 Reputasi toko turun -" + amount + " (Sekarang: " + reputation + ")");
    }

    // ── Inventory ───────────────────────────────
    public void addItem(Item item) {
        if (item == null) return;
        inventory.add(item);
        // Tidak print setiap kali beli supaya tidak spam
    }

    public void showInventory() {
        System.out.println("\n" + "═".repeat(45));
        System.out.println("              INVENTORY TOKO");
        System.out.println("═".repeat(45));

        if (inventory.isEmpty()) {
            System.out.println("Inventory masih kosong.");
            System.out.println("Item akan masuk sini ketika customer membeli.");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                System.out.print((i + 1) + ". ");
                inventory.get(i).showInfo();
            }
            System.out.println("\nTotal: " + inventory.size() + " item");
        }
        System.out.println("═".repeat(45));
    }

    // ── Status ──────────────────────────────────
    public void showStatus() {
        System.out.println("=== STATUS TOKO " + name.toUpperCase() + " ===");
        System.out.println("Level       : " + level);
        System.out.println("Gold        : " + gold);
        System.out.println("EXP         : " + exp + " / " + expNeeded());
        System.out.println("Reputasi    : " + reputation + " / 100  " + reputationBar());
        System.out.println("Item        : " + inventory.size() + " item");
        System.out.println("====================================\n");
    }

    // Visual bar reputasi
    private String reputationBar() {
        int filled = reputation / 10;
        return "[" + "█".repeat(filled) + "░".repeat(10 - filled) + "]";
    }
}
