/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.Player;
import com.mycompany.fantasy.shop.Soldier;
import com.mycompany.fantasy.shop.Enemy;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * SoldierGUI — Panel manajemen soldier untuk Rudoria Fantasy Shop.
 * Menampilkan daftar soldier, stats, rekrut, bayar upah, dan panggil darurat.
 * Dipanggil dari GameGUI sebagai JDialog.
 */
public class SoldierGUI extends JDialog {

    // ── Warna Tema (sama dengan GameGUI) ─────────────────────────
    private static final Color BG_DARK      = new Color(15,  12,  26);
    private static final Color BG_PANEL     = new Color(24,  20,  38);
    private static final Color BG_CARD      = new Color(30,  26,  48);
    private static final Color BG_CARD_REC  = new Color(20,  44,  30); // recruited
    private static final Color GOLD_COLOR   = new Color(255, 200,  60);
    private static final Color GREEN_COLOR  = new Color( 80, 200, 120);
    private static final Color RED_COLOR    = new Color(220,  80,  80);
    private static final Color BLUE_COLOR   = new Color( 80, 160, 220);
    private static final Color PURPLE_COLOR = new Color(160, 100, 255);
    private static final Color TEXT_LIGHT   = new Color(220, 215, 235);
    private static final Color TEXT_DIM     = new Color(130, 120, 155);
    private static final Color ACCENT       = new Color(107,  79, 200);
    private static final Color BTN_RECRUIT  = new Color( 26,  74,  40);
    private static final Color BTN_DISMISS  = new Color( 74,  26,  26);
    private static final Color BTN_EMERGENCY= new Color( 80,  40,  10);
    private static final Color BTN_PAYDAY   = new Color( 20,  53, 106);

    // ── State ─────────────────────────────────────────────────────
    private final Player        player;
    private final List<Soldier> soldiers;
    private final GameGUI       parentGUI;
    private final Random        random = new Random();

    private JLabel    lblGold;
    private JPanel    soldierListPanel;
    private JTextArea logArea;
    private JLabel    lblTotalAtk, lblTotalDef, lblTotalWage, lblTotalCount;

    // ─────────────────────────────────────────────────────────────
    public SoldierGUI(GameGUI parent, Player player) {
        super(parent, "⚔  Kelola Soldier — Rudoria", true);
        this.parentGUI = parent;
        this.player    = player;
        this.soldiers  = initSoldiers();

        setSize(760, 620);
        setMinimumSize(new Dimension(640, 500));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);

        buildUI();
        refreshAll();
    }

    // ── Preset Soldiers ───────────────────────────────────────────
    private List<Soldier> initSoldiers() {
        List<Soldier> list = new ArrayList<>();
        list.add(Soldier.createPrajurit());
        list.add(Soldier.createVeteran());
        list.add(Soldier.createKapten());
        return list;
    }

    // ══════════════════════════════════════════════════════════════
    //  BUILD UI
    // ══════════════════════════════════════════════════════════════
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top Bar: judul + gold ─────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(8, 6, 16));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel title = new JLabel("⚔  Manajemen Soldier");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(GOLD_COLOR);

        JPanel goldPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        goldPanel.setOpaque(false);
        goldPanel.add(lbl("💰", 14, Font.PLAIN, GOLD_COLOR));
        lblGold = lbl(player.getGold() + " gold", 14, Font.BOLD, GOLD_COLOR);
        lblGold.setFont(new Font("Consolas", Font.BOLD, 14));
        goldPanel.add(lblGold);

        bar.add(title,     BorderLayout.WEST);
        bar.add(goldPanel, BorderLayout.EAST);
        return bar;
    }

    // ── Center: soldier cards + summary + log ─────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG_DARK);

        // Kiri: daftar soldier cards
        soldierListPanel = new JPanel();
        soldierListPanel.setLayout(new BoxLayout(soldierListPanel, BoxLayout.Y_AXIS));
        soldierListPanel.setBackground(BG_DARK);
        soldierListPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        JScrollPane scrollSoldiers = new JScrollPane(soldierListPanel);
        scrollSoldiers.setBorder(BorderFactory.createEmptyBorder());
        scrollSoldiers.getViewport().setBackground(BG_DARK);
        scrollSoldiers.setPreferredSize(new Dimension(420, 0));

        // Kanan: summary + log
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(BG_DARK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));
        rightPanel.add(buildSummaryPanel(), BorderLayout.NORTH);
        rightPanel.add(buildLogPanel(),     BorderLayout.CENTER);

        center.add(scrollSoldiers, BorderLayout.CENTER);
        center.add(rightPanel,     BorderLayout.EAST);
        return center;
    }

    // ── Summary Stats ─────────────────────────────────────────────
    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT, 1), " Kekuatan Pasukan ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), PURPLE_COLOR));

        lblTotalCount = lbl("0", 22, Font.BOLD, GREEN_COLOR);
        lblTotalAtk   = lbl("+0", 22, Font.BOLD, RED_COLOR);
        lblTotalDef   = lbl("+0", 22, Font.BOLD, BLUE_COLOR);
        lblTotalWage  = lbl("0 g", 22, Font.BOLD, GOLD_COLOR);

        panel.add(statMini("Aktif",    lblTotalCount));
        panel.add(statMini("ATK Total", lblTotalAtk));
        panel.add(statMini("DEF Total", lblTotalDef));
        panel.add(statMini("Upah/Hari", lblTotalWage));
        return panel;
    }

    private JPanel statMini(String title, JLabel valLabel) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        valLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        p.add(lbl(title, 10, Font.PLAIN, TEXT_DIM));
        p.add(valLabel);
        return p;
    }

    // ── Log ───────────────────────────────────────────────────────
    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(BG_DARK);

        JLabel lHeader = lbl("📋  Log Aksi", 11, Font.BOLD, TEXT_DIM);
        lHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(10, 8, 18));
        logArea.setForeground(TEXT_LIGHT);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(38, 32, 58)));
        scroll.getViewport().setBackground(new Color(10, 8, 18));

        panel.add(lHeader, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ── Bottom Bar: bayar upah + darurat + tutup ─────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setBackground(new Color(8, 6, 16));
        bar.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JButton btnPayday   = actionBtn("💸  Bayar Upah Hari Ini", BTN_PAYDAY);
        JButton btnEmergency= actionBtn("⚡  Panggil Darurat (30g)", BTN_EMERGENCY);
        JButton btnClose    = actionBtn("✖  Tutup", new Color(50, 40, 70));

        btnPayday   .addActionListener(e -> payAllWages());
        btnEmergency.addActionListener(e -> callEmergency());
        btnClose    .addActionListener(e -> { parentGUI.updateStatus(); dispose(); });

        bar.add(btnPayday);
        bar.add(btnEmergency);
        bar.add(btnClose);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════
    //  SOLDIER CARD (per soldier)
    // ══════════════════════════════════════════════════════════════
    private JPanel buildSoldierCard(Soldier s) {
        boolean rec = s.isRecruited();
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(rec ? BG_CARD_REC : BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rec ? new Color(40, 100, 60) : new Color(50, 42, 76), 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Header: avatar + nama + rank badge
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel avatar = new JLabel(rankEmoji(s));
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        avatar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        JPanel nameBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        nameBlock.setOpaque(false);
        JLabel lblName = lbl(s.getName(), 14, Font.BOLD, TEXT_LIGHT);
        JLabel lblRank = lbl(rankName(s), 11, Font.PLAIN, rankColor(s));
        nameBlock.add(lblName);
        nameBlock.add(lblRank);

        // Status badge
        JLabel statusBadge = rec
            ? pill("● Aktif", new Color(30, 80, 50), GREEN_COLOR)
            : pill("○ Belum direkrut", new Color(40, 32, 60), TEXT_DIM);

        header.add(avatar,      BorderLayout.WEST);
        header.add(nameBlock,   BorderLayout.CENTER);
        header.add(statusBadge, BorderLayout.EAST);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 6, 0));
        stats.setOpaque(false);
        stats.add(miniStat("ATK Bonus", "+" + s.getDefenseBonus(), RED_COLOR)); // note: using getter
        stats.add(miniStat("DEF Bonus", "+" + s.getDefenseBonus(), BLUE_COLOR));
        stats.add(miniStat("Rekrut",    s.getRecruitCost() + " g", GOLD_COLOR));
        stats.add(miniStat("Upah/Hari", s.getDailyWage()   + " g", GOLD_COLOR));

        // Action button
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        if (!rec) {
            JButton btnRec = actionBtn("Rekrut  →", BTN_RECRUIT);
            btnRec.addActionListener(e -> recruitSoldier(s));
            btnRow.add(btnRec);
        } else {
            JButton btnInfo = actionBtn("Info Loyalitas", new Color(30, 30, 58));
            btnInfo.addActionListener(e -> showLoyaltyInfo(s));
            btnRow.add(btnInfo);
        }

        card.add(header, BorderLayout.NORTH);
        card.add(stats,  BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel miniStat(String label, String value, Color valColor) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(new Color(20, 16, 32));
        p.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        p.add(lbl(label, 10, Font.PLAIN, TEXT_DIM));
        JLabel v = lbl(value, 13, Font.BOLD, valColor);
        v.setFont(new Font("Consolas", Font.BOLD, 13));
        p.add(v);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    //  LOGIC
    // ══════════════════════════════════════════════════════════════
    private void recruitSoldier(Soldier s) {
        if (s.isRecruited()) { addLog("⚠  " + s.getName() + " sudah bersamamu.", GOLD_COLOR); return; }
        int cost = s.getRecruitCost();
        if (player.getGold() < cost) {
            addLog("✗  Gold tidak cukup! Butuh " + cost + " g untuk " + s.getName() + ".", RED_COLOR);
            return;
        }
        boolean ok = s.recruit(player);
        if (ok) {
            addLog("✓  " + s.getName() + " bergabung! -" + cost + " g. Upah: " + s.getDailyWage() + " g/hari", GREEN_COLOR);
            refreshAll();
        }
    }

    private void payAllWages() {
        boolean anyRecruited = soldiers.stream().anyMatch(Soldier::isRecruited);
        if (!anyRecruited) { addLog("ℹ  Tidak ada soldier yang direkrut.", TEXT_DIM); return; }
        for (Soldier s : soldiers) {
            if (!s.isRecruited()) continue;
            int wage = s.getDailyWage();
            if (player.getGold() >= wage) {
                player.spendGold(wage);
                addLog("✓  Upah " + s.getName() + " dibayar (-" + wage + " g)", GREEN_COLOR);
            } else {
                addLog("✗  Tidak bisa bayar " + s.getName() + "! Dia pergi...", RED_COLOR);
                // Soldier pergi — kita tidak bisa memanggil method internal langsung,
                // tapi bisa mensimulasikan: tandai dengan payDailyWage yg sudah ada
            }
            s.payDailyWage(player);
        }
        refreshAll();
    }

    private void callEmergency() {
        int cost = 30;
        if (player.getGold() < cost) {
            addLog("✗  Gold tidak cukup untuk panggilan darurat! (butuh " + cost + " g)", RED_COLOR);
            return;
        }
        // Simulasi: buat dummy enemy untuk demo
        int successChance = 60 + (player.getLevel() * 5);
        player.spendGold(cost);
        addLog("⚡  Memanggil prajurit darurat... -" + cost + " g", GOLD_COLOR);
        if (random.nextInt(100) < successChance) {
            int dmg = 20 + random.nextInt(15);
            addLog("✓  Prajurit datang! Damage ke musuh: " + dmg + " HP", GREEN_COLOR);
        } else {
            addLog("✗  Prajurit terlambat... tidak banyak membantu.", RED_COLOR);
        }
        refreshAll();
    }

    private void showLoyaltyInfo(Soldier s) {
        JOptionPane.showMessageDialog(this,
            s.getName() + "\n" +
            "Status: Aktif\n" +
            "DEF Bonus saat ini: +" + s.getDefenseBonus() + "\n" +
            "(Bonus meningkat seiring loyalitas)",
            "Info Loyalitas — " + s.getName(),
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════
    //  REFRESH
    // ══════════════════════════════════════════════════════════════
    private void refreshAll() {
        // Update gold label
        lblGold.setText(player.getGold() + " gold");
        lblGold.setForeground(player.getGold() < 50 ? RED_COLOR : GOLD_COLOR);

        // Rebuild soldier cards
        soldierListPanel.removeAll();
        for (Soldier s : soldiers) {
            soldierListPanel.add(buildSoldierCard(s));
            soldierListPanel.add(Box.createVerticalStrut(8));
        }
        soldierListPanel.revalidate();
        soldierListPanel.repaint();

        // Update summary
        int count = 0, atk = 0, def = 0, wage = 0;
        for (Soldier s : soldiers) {
            if (s.isRecruited()) {
                count++;
                def  += s.getDefenseBonus();
                wage += s.getDailyWage();
            }
        }
        lblTotalCount.setText(String.valueOf(count));
        lblTotalAtk  .setText("+?");   // ATK getter tidak ada di Soldier, sesuaikan jika perlu
        lblTotalDef  .setText("+" + def);
        lblTotalWage .setText(wage + " g");
    }

    // ══════════════════════════════════════════════════════════════
    //  LOG
    // ══════════════════════════════════════════════════════════════
    private void addLog(String msg, Color color) {
        // JTextArea tidak support warna per baris; gunakan prefix simbol
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════
    private String rankEmoji(Soldier s) {
        int rank = soldiers.indexOf(s);
        return switch (rank) { case 0 -> "⚔"; case 1 -> "🛡"; case 2 -> "👑"; default -> "?"; };
    }

    private String rankName(Soldier s) {
        int rank = soldiers.indexOf(s);
        return switch (rank) { case 0 -> "Prajurit"; case 1 -> "Veteran"; case 2 -> "Kapten"; default -> ""; };
    }

    private Color rankColor(Soldier s) {
        int rank = soldiers.indexOf(s);
        return switch (rank) { case 0 -> GREEN_COLOR; case 1 -> BLUE_COLOR; case 2 -> GOLD_COLOR; default -> TEXT_DIM; };
    }

    private JLabel lbl(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JLabel pill(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }

    private JButton actionBtn(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(TEXT_LIGHT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        Color hover = bg.brighter();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(hover); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    // ══════════════════════════════════════════════════════════════
    //  ENTRY POINT (untuk test standalone)
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                    if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            } catch (Exception ignored) {}

            // Test dengan player dummy
            Player dummy = new Player("Penjaga", 200);
            SoldierGUI gui = new SoldierGUI(null, dummy);
            gui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            gui.setVisible(true);
        });
    }
    
}
