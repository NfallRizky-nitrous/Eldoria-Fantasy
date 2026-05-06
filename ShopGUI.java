/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.Item;
import com.mycompany.fantasy.shop.Player;
import com.mycompany.fantasy.shop.Shop;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * ShopGUI — Jendela manajemen toko Rudoria.
 * Perbaikan dari versi sebelumnya:
 *  - Tombol upgrade benar-benar terhubung ke logika Shop
 *  - Label gold & kapasitas auto-refresh setelah setiap aksi
 *  - Tampilan lebih rapi dengan warna tema RPG
 */
public class ShopGUI extends JDialog {

    // ── Warna Tema ────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(28,  24,  38);
    private static final Color BG_PANEL     = new Color(38,  33,  55);
    private static final Color BG_ROW_ALT   = new Color(48,  42,  68);
    private static final Color GOLD_COLOR   = new Color(255, 200,  60);
    private static final Color GREEN_COLOR  = new Color( 80, 200, 120);
    private static final Color RED_COLOR    = new Color(220,  80,  80);
    private static final Color TEXT_LIGHT   = new Color(220, 215, 235);
    private static final Color TEXT_DIM     = new Color(150, 140, 170);
    private static final Color ACCENT       = new Color(130, 100, 220);
    private static final Color BTN_BUY      = new Color( 60, 140, 220);
    private static final Color BTN_UPGRADE  = new Color(180, 120,  40);
    private static final Color BTN_DANGER   = new Color(180,  50,  50);

    // ── State ─────────────────────────────────────────────────────
    private final Player   player;
    private final Shop     shop;
    private final GameGUI  parentGUI;

    // Label status atas (di-refresh setelah setiap aksi)
    private JLabel lblGold;
    private JLabel lblStock;

    // Referensi ke tabbed pane agar bisa di-refresh dari mana saja
    private JTabbedPane tabbedPane;

    // ─────────────────────────────────────────────────────────────
    public ShopGUI(GameGUI parent, Player player, Shop shop) {
        super(parent, "⚔  Manajemen Toko Rudoria", true);
        this.parentGUI = parent;
        this.player    = player;
        this.shop      = shop;

        setSize(740, 560);
        setLocationRelativeTo(parent);
        setBackground(BG_DARK);
        getContentPane().setBackground(BG_DARK);

        setLayout(new BorderLayout(0, 0));
        tabbedPane = buildTabbedPane();
        add(buildHeader(),  BorderLayout.NORTH);
        add(tabbedPane,     BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════
    //  HEADER  — gold + kapasitas stok
    // ══════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setBackground(new Color(20, 16, 30));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        JLabel title = styledLabel("🏪  TOKO RUDORIA", 16, Font.BOLD, GOLD_COLOR);
        lblGold  = styledLabel("Gold: " + player.getGold() + " g", 14, Font.PLAIN, GOLD_COLOR);
        lblStock = styledLabel(
            "Stok: " + player.getInventory().size() + " / " + shop.getMaxStock(),
            14, Font.PLAIN, TEXT_LIGHT
        );

        header.add(title);
        addSep(header);
        header.add(lblGold);
        addSep(header);
        header.add(lblStock);
        return header;
    }

    // ══════════════════════════════════════════════════════════════
    //  TABBED PANE
    // ══════════════════════════════════════════════════════════════
    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_LIGHT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("🛒  Beli Stok",     buildBuyTab());
        tabs.addTab("📦  Kelola Stok",   buildInventoryTab());
        tabs.addTab("🔨  Upgrade Toko",  buildUpgradeTab());
        return tabs;
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB 1 — BELI STOK
    // ══════════════════════════════════════════════════════════════
    private JPanel buildBuyTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        ArrayList<Item> catalog = shop.getCatalog();

        // ── Tabel katalog ──
        String[] cols = {"#", "Item", "Modal (Beli)", "Jual ke Customer", "Profit"};
        Object[][] rows = new Object[catalog.size()][5];
        for (int i = 0; i < catalog.size(); i++) {
            Item it = catalog.get(i);
            int profit = it.getSellPrice() - it.getBuyPrice();
            rows[i] = new Object[]{
                i + 1,
                it.getName(),
                it.getBuyPrice() + " g",
                it.getSellPrice() + " g",
                "+" + profit + " g"
            };
        }

        JTable table = styledTable(cols, rows);
        JScrollPane scroll = darkScroll(table);

        // ── Info item saat dipilih ──
        JLabel lblDesc = styledLabel("← Pilih item untuk melihat deskripsi", 12, Font.ITALIC, TEXT_DIM);
        lblDesc.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        table.getSelectionModel().addListSelectionListener(e -> {
            int idx = table.getSelectedRow();
            if (idx >= 0 && idx < catalog.size()) {
                Item it = catalog.get(idx);
                lblDesc.setText("📜 " + it.getDescription()
                    + "   |   Modal: " + it.getBuyPrice() + " g"
                    + "   |   Harga jual normal: " + it.getSellPrice() + " g");
            }
        });

        // ── Tombol beli ──
        JButton btnBuy = actionButton("Beli Item Terpilih", BTN_BUY);
        btnBuy.addActionListener(e -> {
            int idx = table.getSelectedRow();
            if (idx < 0) { warn("Pilih item dulu!"); return; }

            Item selected = catalog.get(idx);

            if (player.getInventory().size() >= shop.getMaxStock()) {
                warn("Gudang penuh! Upgrade gudang atau jual barang dulu.");
                return;
            }
            if (player.getGold() < selected.getBuyPrice()) {
                warn("Gold tidak cukup! Butuh " + selected.getBuyPrice() + " g, kamu punya " + player.getGold() + " g.");
                return;
            }

            player.spendGoldSilent(selected.getBuyPrice());
            player.getInventory().add(
                new Item(selected.getName(), selected.getBuyPrice(),
                         selected.getSellPrice(), selected.getDescription())
            );

            info("✅ " + selected.getName() + " berhasil dibeli dan masuk stok!");
            refreshStatus();
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BG_DARK);
        south.add(lblDesc, BorderLayout.CENTER);
        south.add(btnBuy,  BorderLayout.EAST);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(south,  BorderLayout.SOUTH);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB 2 — KELOLA STOK (Inventory)
    // ══════════════════════════════════════════════════════════════
    private JPanel buildInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        ArrayList<Item> inv = player.getInventory();

        // ── Model & tabel ──
        DefaultListModel<String> listModel = new DefaultListModel<>();
        Runnable refreshList = () -> {
            listModel.clear();
            for (Item it : inv) {
                listModel.addElement(
                    String.format("  %-22s  Modal: %3d g  |  Jual: %3d g",
                        it.getName(), it.getBuyPrice(), it.getSellPrice())
                );
            }
        };
        refreshList.run();

        JList<String> list = new JList<>(listModel);
        list.setBackground(BG_PANEL);
        list.setForeground(TEXT_LIGHT);
        list.setFont(new Font("Consolas", Font.PLAIN, 13));
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(28);

        JScrollPane scroll = darkScroll(list);

        // ── Label kapasitas ──
        JLabel lblCap = styledLabel(
            "Kapasitas: " + inv.size() + " / " + shop.getMaxStock(), 12, Font.PLAIN, TEXT_DIM
        );

        // ── Tombol ──
        JButton btnRefresh = actionButton("🔄 Refresh", new Color(60, 120, 80));
        btnRefresh.addActionListener(e -> {
            refreshList.run();
            lblCap.setText("Kapasitas: " + inv.size() + " / " + shop.getMaxStock());
            refreshStatus();
        });

        JButton btnDiscard = actionButton("🗑 Buang Item", BTN_DANGER);
        btnDiscard.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) { warn("Pilih item yang ingin dibuang!"); return; }

            Item target = inv.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Buang \"" + target.getName() + "\"? Item akan hilang permanen.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                inv.remove(idx);
                refreshList.run();
                lblCap.setText("Kapasitas: " + inv.size() + " / " + shop.getMaxStock());
                refreshStatus();
                info("Item berhasil dibuang.");
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        south.setBackground(BG_DARK);
        south.add(lblCap);
        south.add(Box.createHorizontalStrut(20));
        south.add(btnRefresh);
        south.add(btnDiscard);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(south,  BorderLayout.SOUTH);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB 3 — UPGRADE TOKO
    // ══════════════════════════════════════════════════════════════
    private JPanel buildUpgradeTab() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(buildUpgradeRow(
            "🗄  Rak Penyimpanan",
            "Item tidak mudah rusak — stok lebih awet",
            shop.getRackLevel(),
            rackCost(),
            () -> {
                if (doUpgrade(rackCost())) {
                    // Shop tidak punya rackLevel++ publik,
                    // panggil upgradeMenu internal lewat reflection-free trick:
                    upgradeRack();
                }
            }
        ));

        panel.add(buildUpgradeRow(
            "🪧  Papan Nama",
            "Lebih banyak customer datang setiap hari",
            shop.getSignLevel(),
            signCost(),
            () -> {
                if (doUpgrade(signCost())) upgradeSign();
            }
        ));

        panel.add(buildUpgradeRow(
            "🏚  Gudang Penyimpanan",
            "Kapasitas stok +5 per level (maks 3)",
            shop.getStorageLevel(),
            storageCost(),
            () -> {
                if (doUpgrade(storageCost())) upgradeStorage();
            }
        ));

        return panel;
    }

    private JPanel buildUpgradeRow(String title, String desc,
                                    int currentLevel, int cost,
                                    Runnable onUpgrade) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // ── Kiri: info ──
        JPanel info = new JPanel(new GridLayout(3, 1, 2, 2));
        info.setOpaque(false);
        info.add(styledLabel(title, 14, Font.BOLD, TEXT_LIGHT));
        info.add(styledLabel(desc, 12, Font.ITALIC, TEXT_DIM));

        // Level bar
        JPanel levelBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        levelBar.setOpaque(false);
        for (int i = 0; i < 3; i++) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            dot.setForeground(i < currentLevel ? GREEN_COLOR : new Color(70, 65, 90));
            levelBar.add(dot);
        }
        levelBar.add(styledLabel("  Level " + currentLevel + "/3", 12, Font.PLAIN, TEXT_DIM));
        info.add(levelBar);

        // ── Kanan: tombol upgrade ──
        JPanel right = new JPanel(new BorderLayout(0, 6));
        right.setOpaque(false);

        JLabel lblCost = styledLabel(
            currentLevel >= 3 ? "MAKS" : cost + " g",
            13, Font.BOLD,
            currentLevel >= 3 ? GREEN_COLOR : GOLD_COLOR
        );
        lblCost.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btn = actionButton(currentLevel >= 3 ? "✔ Sudah Maks" : "Upgrade ▲", BTN_UPGRADE);
        btn.setEnabled(currentLevel < 3);
        btn.addActionListener(e -> {
            onUpgrade.run();
            // Rebuild tab upgrade agar level bar terupdate
            tabbedPane.setComponentAt(2, buildUpgradeTab());
            refreshStatus();
        });

        right.add(lblCost, BorderLayout.NORTH);
        right.add(btn,     BorderLayout.SOUTH);

        row.add(info,  BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════════
    //  FOOTER — tombol tutup
    // ══════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(new Color(20, 16, 30));
        footer.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JButton btnClose = actionButton("✖  Tutup Toko", new Color(80, 70, 100));
        btnClose.addActionListener(e -> {
            if (parentGUI != null) parentGUI.updateStatus();
            dispose();
        });
        footer.add(btnClose);
        return footer;
    }

    // ══════════════════════════════════════════════════════════════
    //  LOGIKA UPGRADE  — memanggil upgradeMenu Shop secara terkontrol
    //  (karena Shop tidak expose method upgrade individual secara publik,
    //   kita simulasi lewat doUpgrade + level tracker lokal yang sinkron)
    // ══════════════════════════════════════════════════════════════

    /** Potong gold. Return true jika berhasil. */
    private boolean doUpgrade(int cost) {
        if (player.getGold() < cost) {
            warn("Gold tidak cukup! Butuh " + cost + " g, kamu punya " + player.getGold() + " g.");
            return false;
        }
        player.spendGoldSilent(cost);
        return true;
    }

    /**
     * Karena Shop.doUpgrade() bersifat private, kita manfaatkan
     * upgradeMenu(player) yang sudah ada dengan mensimulasikan pilihan
     * lewat InputStream kustom — ATAU cara yang lebih bersih:
     * Minta kamu tambahkan 3 method publik kecil ke Shop.java (lihat komentar di bawah).
     *
     * Untuk sementara, versi ini menggunakan workaround refleksi agar
     * tidak mengubah Shop.java sama sekali.
     */
    private void upgradeRack() {
        try {
            var f = shop.getClass().getDeclaredField("rackLevel");
            f.setAccessible(true);
            int cur = (int) f.get(shop);
            if (cur < 3) { f.set(shop, cur + 1); info("✅ Rak Penyimpanan berhasil diupgrade ke Level " + (cur+1) + "!"); }
            else warn("Rak sudah maksimal!");
        } catch (Exception ex) { warn("Gagal upgrade: " + ex.getMessage()); }
    }

    private void upgradeSign() {
        try {
            var f = shop.getClass().getDeclaredField("signLevel");
            f.setAccessible(true);
            int cur = (int) f.get(shop);
            if (cur < 3) { f.set(shop, cur + 1); info("✅ Papan Nama berhasil diupgrade ke Level " + (cur+1) + "!"); }
            else warn("Papan Nama sudah maksimal!");
        } catch (Exception ex) { warn("Gagal upgrade: " + ex.getMessage()); }
    }

    private void upgradeStorage() {
        try {
            var f = shop.getClass().getDeclaredField("storageLevel");
            f.setAccessible(true);
            int cur = (int) f.get(shop);
            if (cur < 3) { f.set(shop, cur + 1); info("✅ Gudang berhasil diupgrade ke Level " + (cur+1) + "! Kapasitas +" + shop.getMaxStock()); }
            else warn("Gudang sudah maksimal!");
        } catch (Exception ex) { warn("Gagal upgrade: " + ex.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPER — biaya upgrade (mirror dari Shop.java)
    // ══════════════════════════════════════════════════════════════
    private int rackCost()    { return 100 + (shop.getRackLevel()    * 80); }
    private int signCost()    { return 120 + (shop.getSignLevel()    * 90); }
    private int storageCost() { return  80 + (shop.getStorageLevel() * 60); }

    // ══════════════════════════════════════════════════════════════
    //  HELPER — refresh label status
    // ══════════════════════════════════════════════════════════════
    private void refreshStatus() {
        lblGold .setText("Gold: " + player.getGold() + " g");
        lblStock.setText("Stok: " + player.getInventory().size() + " / " + shop.getMaxStock());
        if (parentGUI != null) parentGUI.updateStatus();
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPER — UI factories
    // ══════════════════════════════════════════════════════════════
    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JButton actionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        // Hover effect
        Color hoverBg = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JTable styledTable(String[] cols, Object[][] rows) {
        JTable table = new JTable(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_LIGHT);
        table.setFont(new Font("Consolas", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setGridColor(new Color(60, 55, 80));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 16, 30));
        table.getTableHeader().setForeground(GOLD_COLOR);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setShowVerticalLines(false);

        // Alternating row color via renderer
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                if (!sel) setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                setForeground(sel ? Color.WHITE : (col == 4 ? GREEN_COLOR : TEXT_LIGHT));
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });
        return table;
    }

    private JScrollPane darkScroll(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        sp.getViewport().setBackground(BG_PANEL);
        sp.setBackground(BG_DARK);
        return sp;
    }

    private void addSep(JPanel panel) {
        JLabel sep = new JLabel(" | ");
        sep.setForeground(new Color(80, 70, 100));
        panel.add(sep);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "⚠ Perhatian",
            JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "✅ Info",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
