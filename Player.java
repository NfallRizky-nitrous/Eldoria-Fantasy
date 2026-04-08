public class Player {
    private int gold;
    private int exp;
    private int level;

  // cons
    public Player() {
        this.gold = 0;
        this.exp = 0;
        this.level = 1;
    }
  
    // get
    public int getGold() {
        return gold;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    // Method tambah gold
    public void gainGold(int amount) {
        if (amount > 0) {
            gold += amount;
            System.out.println("Gold bertambah " + amount);
        }
    }

    // Method pakai gold
    public void spendGold(int amount) {
        if (amount > 0 && gold >= amount) {
            gold -= amount;
            System.out.println("Gold digunakan " + amount);
        } else {
            System.out.println("Gold tidak cukup!");
        }
    }

    // Method tambah EXP
    public void gainExp(int amount) {
        if (amount > 0) {
            exp += amount;
            System.out.println("EXP bertambah " + amount);
            levelUp();
        }
    }

    // Logic level up
    private void levelUp() {
        while (exp >= 100) {
            exp -= 100;
            level++;
            System.out.println("Level naik! Sekarang level " + level);
        }
    }
}
