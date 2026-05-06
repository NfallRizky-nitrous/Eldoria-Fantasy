/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.gui.GUIBridge;
import java.util.ArrayList;

public class Player {
    private String name;
    private int gold;
    private int exp;
    private int level;
    private int reputation;
    private int defense = 5;
    private ArrayList<Item> inventory;

    private int maxHp;
    private int hp;
    private int attack;
    private boolean defending;

    private int dailyGoldStart;
    private int dailyRepStart;

    private GUIBridge bridge;

    public Player(String name, int par) {
        this.name       = name;
        this.gold       = 80;
        this.exp        = 0;
        this.level      = 1;
        this.reputation = 20;
        this.inventory  = new ArrayList<>();
        this.maxHp      = 120;
        this.hp         = 120;
        this.attack     = 10;
        this.defending  = false;
    }

    public void setBridge(GUIBridge bridge) {
        this.bridge = bridge;
    }

    private void updateGUI() {
        if (bridge != null) bridge.refreshStatus();
    }

    private void log(String text) {
        if (bridge != null) bridge.appendLog(text);
    }

    public void snapshotDayStart() {
        dailyGoldStart = gold;
        dailyRepStart  = reputation;
    }

    public void showDailySummary(int day) {
        int goldDiff = gold - dailyGoldStart;
        int repDiff  = reputation - dailyRepStart;
        log("\n=============================================");
        log("         RINGKASAN HARI KE-" + day);
        log("=============================================");
        log(String.format("  Gold     : %d  (%s%d)", gold, goldDiff >= 0 ? "+" : "", goldDiff));
        log(String.format("  Reputasi : %d  (%s%d)", reputation, repDiff >= 0 ? "+" : "", repDiff));
        log(String.format("  HP       : %d / %d", hp, maxHp));
        log(String.format("  Item     : %d item di inventory", inventory.size()));
        log("=============================================");
    }

    public String getName()               { return name; }
    public int getGold()                  { return gold; }
    public int getExp()                   { return exp; }
    public int getLevel()                 { return level; }
    public int getReputation()            { return reputation; }
    public ArrayList<Item> getInventory() { return inventory; }
    public int getHp()                    { return hp; }
    public int getMaxHp()                 { return maxHp; }
    public int getAttack()                { return attack; }
    public boolean isDefending()          { return defending; }
    public void setDefending(boolean v)   { defending = v; }
    
    public void setHp(int hp) { this.hp = hp; updateGUI(); }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; updateGUI(); }
    public void setGold(int gold) { this.gold = gold; updateGUI(); }
    public void setReputation(int rep) { this.reputation = rep; updateGUI(); }

    public void gainGold(int amount) {
        if (amount <= 0) return;
        gold += amount;
        log("💰 Gold bertambah +" + amount + " (Total: " + gold + ")");
        updateGUI();
    }

    public void spendGold(int amount) {
        if (amount <= 0) return;
        if (gold >= amount) {
            gold -= amount;
            log("💸 Gold digunakan -" + amount + " (Sisa: " + gold + ")");
        } else {
            log("⚠️ Gold tidak cukup! Kehilangan semua gold (" + gold + ")");
            gold = 0;
        }
        updateGUI();
    }

    public void spendGoldSilent(int amount) {
        gold = Math.max(0, gold - amount);
        updateGUI();
    }

    public void takeDamage(int damage) {
        int finalDamage = Math.max(1, damage - defense); 
        hp = Math.max(0, hp - finalDamage);
        updateGUI();
    }

    public void healHp(int amount) {
        int before = hp;
        hp = Math.min(maxHp, hp + amount);
        log("❤️ HP pulih +" + (hp - before) + " (" + hp + "/" + maxHp + ")");
        updateGUI();
    }

    public void restHeal() {
        int healed = 20 + (level * 3);
        int before = hp;
        hp = Math.min(maxHp, hp + healed);
        log("🌙 Kamu beristirahat. HP pulih +" + (hp - before) + " (" + hp + "/" + maxHp + ")");
        updateGUI();
    }

    public boolean isAlive() { return hp > 0; }

    public void gainExp(int amount) {
        if (amount <= 0) return;
        exp += amount;
        log("✨ EXP bertambah +" + amount + " (" + exp + "/" + expNeeded() + ")");
        checkLevelUp();
    }

    private int expNeeded() {
        return 100 + (level - 1) * 30;
    }

    private void checkLevelUp() {
        while (exp >= expNeeded()) {
            exp -= expNeeded();
            level++;
            log("\n🎊 *** LEVEL UP! Sekarang Level " + level + " ***");
            applyLevelUpBonus();
        }
    }

    private void applyLevelUpBonus() {
        int goldBonus = level * 20;
        gainGold(goldBonus);
        maxHp   += 15;
        hp       = maxHp;
        attack  += 3;
        log("> 🎁 Bonus gold     : +" + goldBonus);
        log("> ❤️ Max HP naik    : +15 (Sekarang " + maxHp + ")");
        log("> ⚔️ Attack naik    : +3 (Sekarang " + attack + ")");
        if (level % 2 == 0) {
            gainReputation(3);
            log("> ⭐ Reputasi naik  : +3 (Bonus level genap)");
        }
        updateGUI();
    }

    public void gainReputation(int amount) {
        if (amount <= 0) return;
        reputation = Math.min(reputation + amount, 100);
        log("⭐ Reputasi naik +" + amount + " (Sekarang: " + reputation + ")");
        updateGUI();
    }

    public void loseReputation(int amount) {
        reputation = Math.max(reputation - amount, 0);
        log("💢 Reputasi turun -" + amount + " (Sekarang: " + reputation + ")");
        updateGUI();
    }

    public void addItem(Item item) {
        if (item == null) return;
        inventory.add(item);
    }

    public int useHealthItem() {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            String n = item.getName().toLowerCase();
            if (n.contains("potion") || n.contains("elixir") || n.contains("herbal") || n.contains("remedy")) {
                int healAmount = item.getSellPrice() / 2;
                healAmount = Math.max(healAmount, 15);
                inventory.remove(i);
                log("🧪 Kamu menggunakan " + item.getName() + "!");
                healHp(healAmount);
                return healAmount;
            }
        }
        log("❌ Tidak ada potion di inventory!");
        return 0;
    }

    public boolean hasHealthItem() {
        for (Item item : inventory) {
            String n = item.getName().toLowerCase();
            if (n.contains("potion") || n.contains("elixir") || n.contains("herbal") || n.contains("remedy"))
                return true;
        }
        return false;
    }

    public void showInventory() {
        log("\n=============================================");
        log("              INVENTORY TOKO");
        log("=============================================");
        if (inventory.isEmpty()) {
            log("Inventory masih kosong.");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                log((i + 1) + ". " + inventory.get(i).getName());
            }
            log("\nTotal: " + inventory.size() + " item");
        }
        log("=============================================");
    }

    public void showStatus() {
        log("\n=== STATUS TOKO " + name.toUpperCase() + " ===");
        log("Level       : " + level);
        log("Gold        : " + gold);
        log("EXP         : " + exp + " / " + expNeeded());
        log("HP          : " + hp + " / " + maxHp);
        log("Reputasi    : " + reputation + " / 100");
        log("Item        : " + inventory.size() + " item");
        log("====================================\n");
    }

    public void showCombatStatus() {
        log("  " + name + "  HP: " + hp + "/" + maxHp 
                         + "  ATK: " + attack + "  Gold: " + gold
                         + (hasHealthItem() ? "  [Punya Potion]" : ""));
    }
}