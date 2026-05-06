/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.GameManager;
import com.mycompany.fantasy.shop.Player;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * GameGUI — Layar utama game Rudoria Fantasy Shop.
 * Mengimplementasikan GUIBridge sehingga Event, StoryManager, dan Shop
 * dapat menampilkan decision panel di dalam jendela ini tanpa pop-up.
 */

public class GameGUI extends JFrame implements GUIBridge {

    // ── Warna Tema ────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(15,  12,  26);
    private static final Color BG_PANEL     = new Color(24,  20,  38);
    private static final Color BG_LOG       = new Color(10,   8,  18);
    private static final Color GOLD_COLOR   = new Color(255, 200,  60);
    private static final Color GREEN_COLOR  = new Color( 80, 200, 120);
    private static final Color RED_COLOR    = new Color(220,  80,  80);
    private static final Color BLUE_COLOR   = new Color( 80, 160, 220);
    private static final Color PURPLE_COLOR = new Color(160, 100, 255);
    private static final Color TEXT_LIGHT   = new Color(220, 215, 235);
    private static final Color TEXT_DIM     = new Color(130, 120, 155);
    private static final Color ACCENT       = new Color(107,  79, 200);

    private static final Color BTN_SHOP    = new Color( 26,  74,  40);
    private static final Color BTN_BUY     = new Color( 20,  53, 106);
    private static final Color BTN_MANAGE  = new Color( 42,  32,  80);
    private static final Color BTN_UPGRADE = new Color( 74,  50,  16);
    private static final Color BTN_SOLDIER = new Color( 74,  26,  26);
    private static final Color BTN_END     = new Color( 30,  30,  56);
    private static final Color BTN_QUIT    = new Color( 58,  16,  16);
    private static final Color BTN_SAVE    = new Color( 10,  56,  40);

    // ── State ─────────────────────────────────────────────────────
    private GameManager   game;
    private JTextPane     txtOutput;
    private StyledDocument doc;
    private JLabel        lblDay, lblGold, lblRep, lblHP;
    private JProgressBar  hpBar, repBar;
    private JButton[]     actionButtons;
    private DecisionPanel decisionPanel;

    // ─────────────────────────────────────────────────────────────
    public GameGUI() {
        setTitle("⚔  Rudoria Fantasy Shop");
        setSize(880, 700);
        setMinimumSize(new Dimension(720, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        showStartDialog();
    }

    // ══════════════════════════════════════════════════════════════
    //  DIALOG AWAL
    // ══════════════════════════════════════════════════════════════
    private void showStartDialog() {
        JPanel dlg = new JPanel(new BorderLayout(12, 16));
        dlg.setBackground(BG_PANEL);
        dlg.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("⚔  Selamat Datang di RUDORIA", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(GOLD_COLOR);

        JLabel subtitle = new JLabel("Fantasy Shop Simulator", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(TEXT_DIM);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel btns = new JPanel(new GridLayout(1, 2, 14, 0));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnPlay = bigBtn("🗡  Main Baru", BTN_SHOP);
        JButton btnLoad = bigBtn("📂  Load Game", BTN_BUY);
        btns.add(btnPlay);
        btns.add(btnLoad);

        dlg.add(titlePanel, BorderLayout.NORTH);
        dlg.add(btns,       BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Rudoria Fantasy Shop", true);
        dialog.setContentPane(dlg);
        dialog.setSize(400, 170);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        final boolean[] loadMode = {false};
        btnPlay.addActionListener(e -> { loadMode[0] = false; dialog.dispose(); });
        btnLoad.addActionListener(e -> { loadMode[0] = true;  dialog.dispose(); });
        dialog.setVisible(true);

        game = new GameManager();

        if (loadMode[0]) {
            String save = JOptionPane.showInputDialog(this,
                "Masukkan nama file save:", "Load Game", JOptionPane.QUESTION_MESSAGE);
            if (save != null && !save.trim().isEmpty()) {
                game.loadGameGUI(save.trim());
            } else {
                askName();
            }
        } else {
            askName();
        }

        game.setGUI(this);
        buildUI();
        startGame();
    }

    private void askName() {
        String name = JOptionPane.showInputDialog(this,
            "Masukkan nama penjaga toko:", "Nama Karakter", JOptionPane.QUESTION_MESSAGE);
        game.initNewGame(name == null || name.trim().isEmpty() ? "Penjaga" : name.trim());
    }

    // ══════════════════════════════════════════════════════════════
    //  BUILD UI
    // ══════════════════════════════════════════════════════════════
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBtnBar(), BorderLayout.SOUTH);
    }

    // ── Top Status Bar ────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 1, 0));
        bar.setBackground(new Color(8, 6, 16));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        bar.add(simpleCard("📅  HARI",    "1 / 40",    GOLD_COLOR,   "day"));
        bar.add(simpleCard("💰  GOLD",    "80 g",      GOLD_COLOR,   "gold"));

        repBar = mkBar(PURPLE_COLOR, 20);
        bar.add(barCard("⭐  REPUTASI",  "20 / 100",  PURPLE_COLOR, repBar, "rep"));

        hpBar = mkBar(GREEN_COLOR, 100);
        bar.add(barCard("❤  HP",         "120 / 120", RED_COLOR,    hpBar,  "hp"));

        return bar;
    }

    private JPanel simpleCard(String title, String val, Color color, String tag) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 3));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(38, 32, 58)),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        card.add(lbl(title, 10, Font.PLAIN, TEXT_DIM));

        JLabel v = new JLabel(val);
        v.setFont(new Font("Consolas", Font.BOLD, 15));
        v.setForeground(color);
        if ("day" .equals(tag)) lblDay  = v;
        if ("gold".equals(tag)) lblGold = v;
        card.add(v);
        return card;
    }

    private JPanel barCard(String title, String val, Color color,
                           JProgressBar bar, String tag) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(38, 32, 58)),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel lTitle = lbl(title, 10, Font.PLAIN, TEXT_DIM);
        JLabel lVal   = new JLabel(val);
        lVal.setFont(new Font("Consolas", Font.BOLD, 14));
        lVal.setForeground(color);
        if ("rep".equals(tag)) lblRep = lVal;
        if ("hp" .equals(tag)) lblHP  = lVal;

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lTitle, BorderLayout.WEST);
        top.add(lVal,   BorderLayout.EAST);

        card.add(top, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);
        return card;
    }

    // ── Center: Log + Decision Overlay ───────────────────────────
    private JPanel buildCenter() {
        // Log output
        txtOutput = new JTextPane();
        txtOutput.setEditable(false);
        txtOutput.setBackground(BG_LOG);
        txtOutput.setCaretColor(GOLD_COLOR);
        doc = txtOutput.getStyledDocument();

        JScrollPane scroll = new JScrollPane(txtOutput);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));
        scroll.getViewport().setBackground(BG_LOG);

        // Decision overlay (sits on top of log)
        decisionPanel = new DecisionPanel();
        decisionPanel.setAlignmentX(0.5f);
        decisionPanel.setAlignmentY(0.5f);

        JPanel overlay = new JPanel();
        overlay.setLayout(new OverlayLayout(overlay));
        overlay.setBackground(BG_DARK);
        overlay.add(decisionPanel);
        overlay.add(scroll);

        // Log header row
        JPanel logHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        logHeader.setBackground(new Color(8, 6, 16));
        logHeader.add(lbl("📜  Game Log", 11, Font.BOLD, TEXT_DIM));
        JButton clr = miniBtn("Bersihkan");
        clr.addActionListener(e -> txtOutput.setText(""));
        logHeader.add(clr);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_DARK);
        wrap.add(logHeader, BorderLayout.NORTH);
        wrap.add(overlay,   BorderLayout.CENTER);
        return wrap;
    }

    // ── Bottom Action Bar ─────────────────────────────────────────
    private JPanel buildBtnBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(8, 6, 16));
        wrapper.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JPanel grid = new JPanel(new GridLayout(2, 4, 6, 6));
        grid.setBackground(new Color(8, 6, 16));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[][] btns = {
            {"🏪  Buka Toko",       "Layani customer hari ini"},
            {"🛒  Beli Stok",       "Beli barang dari supplier"},
            {"📦  Kelola Stok",     "Atur dan lihat inventory"},
            {"🔨  Upgrade Toko",    "Tingkatkan fasilitas toko"},
            {"⚔   Kelola Soldier", "Rekrut dan lihat pasukan"},
            {"🌙  Akhiri Hari",     "Tidur dan lanjutkan ke hari berikutnya"},
            {"🚪  Keluar",          "Keluar dari game"},
            {"💾  Save Game",       "Simpan progress permainan"},
        };
        Color[] colors = {
            BTN_SHOP, BTN_BUY, BTN_MANAGE, BTN_UPGRADE,
            BTN_SOLDIER, BTN_END, BTN_QUIT, BTN_SAVE
        };

        actionButtons = new JButton[8];
        for (int i = 0; i < 8; i++) {
            JButton b = menuBtn(btns[i][0], btns[i][1], colors[i]);
            final int choice = i + 1;
            b.addActionListener(e -> handleMenu(choice));
            actionButtons[i] = b;
            grid.add(b);
        }

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════
    //  GAME LOGIC
    // ══════════════════════════════════════════════════════════════
    private void startGame() {
        String intro = game.startGameGUI();
        if (intro != null && !intro.isEmpty()) {
            appendColored(intro, TEXT_LIGHT);
        }
        updateStatus();
    }

    private void handleMenu(int choice) {
        if (decisionPanel.isVisible()) return;

        switch (choice) {
            case 7 -> { // Keluar
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin keluar?", "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) System.exit(0);
            }
            case 2 -> { // Buka Toko
                if (game.isShopOpenedToday()) {
                    appendColored("⚠  Toko sudah dibuka hari ini.\n", GOLD_COLOR);
                    return;
                }
                openShopGUI();
            }
            case 5 -> openSoldierGUI(); // Kelola Soldier
            default -> {
                setButtonsEnabled(false);
                String result = game.handleChoiceGUI(choice);
                if (result != null && !result.isEmpty()) {
                    appendColored(result, TEXT_LIGHT);
                }
                updateStatus();
                if (!decisionPanel.isVisible()) setButtonsEnabled(true);
            }
        }
    }

    private void openShopGUI() {
        try {
            ShopGUI shopWindow = new ShopGUI(this, game.getPlayer(), game.getShop());
            shopWindow.setVisible(true);
            game.setShopOpenedToday(true);
            updateStatus();
        } catch (Exception ex) {
            appendColored("❌ Gagal membuka toko: " + ex.getMessage() + "\n", RED_COLOR);
            ex.printStackTrace();
        }
    }

    private void openSoldierGUI() {
        try {
            SoldierGUI soldierWindow = new SoldierGUI(this, game.getPlayer());
            soldierWindow.setVisible(true);
            updateStatus();
        } catch (Exception ex) {
            appendColored("❌ Gagal membuka panel soldier: " + ex.getMessage() + "\n", RED_COLOR);
            ex.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  GUIBridge IMPLEMENTATION
    // ══════════════════════════════════════════════════════════════
    @Override
    public void showDecision(String icon, String title, String narrative,
                              String[] choices, Consumer<Integer> callback) {
        SwingUtilities.invokeLater(() -> {
            setButtonsEnabled(false);
            decisionPanel.showDecision(icon, title, narrative, choices, idx -> {
                callback.accept(idx);
                setButtonsEnabled(true);
                updateStatus();
            });
        });
    }

    @Override
    public void showContinue(String narrative, Runnable callback) {
        SwingUtilities.invokeLater(() -> {
            setButtonsEnabled(false);
            decisionPanel.showContinue(narrative, () -> {
                callback.run();
                setButtonsEnabled(true);
                updateStatus();
            });
        });
    }

    @Override
    public void showCombatActions(String statusText, boolean hasPotion,
                                   boolean hasSoldier, Consumer<Integer> callback) {
        SwingUtilities.invokeLater(() -> {
            setButtonsEnabled(false);
            decisionPanel.showCombatActions(statusText, hasPotion, hasSoldier, idx -> {
                callback.accept(idx);
                setButtonsEnabled(true);
                updateStatus();
            });
        });
    }

    @Override
    public void appendLog(String text) { appendToOutput(text); }

    @Override
    public void refreshStatus()        { updateStatus(); }

    // ══════════════════════════════════════════════════════════════
    //  PUBLIC API — dipanggil dari GameManager / kelas lain
    // ══════════════════════════════════════════════════════════════
    public void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> appendColored(text + "\n", TEXT_LIGHT));
    }

    public void updateStatus() {
        SwingUtilities.invokeLater(() -> {
            Player p = game.getPlayer();
            if (p == null) return;

            int day = game.getDay();

            lblDay .setText(day + " / 40");
            lblGold.setText(p.getGold() + " g");
            lblRep .setText(p.getReputation() + " / 100");
            lblHP  .setText(p.getHp() + " / " + p.getMaxHp());

            repBar.setValue(p.getReputation());

            int hpPct = (int)((double) p.getHp() / p.getMaxHp() * 100);
            hpBar.setValue(hpPct);
            hpBar.setForeground(hpPct > 60 ? GREEN_COLOR
                              : hpPct > 30 ? new Color(220, 180, 40)
                              : RED_COLOR);

            lblGold.setForeground(p.getGold() < 50 ? RED_COLOR : GOLD_COLOR);
            lblDay .setForeground(day % 10 == 0 ? RED_COLOR
                                : day % 5  == 0 ? new Color(255, 160, 60)
                                : GOLD_COLOR);

            if (actionButtons != null) {
                boolean busy = decisionPanel.isVisible();
                for (JButton b : actionButtons) b.setEnabled(!busy);

                // Tombol Buka Toko: nonaktif jika sudah buka hari ini
                boolean shopDone = game.isShopOpenedToday();
                actionButtons[0].setEnabled(!busy && !shopDone);
                actionButtons[0].setText(shopDone
                    ? "✔  Toko Sudah Buka"
                    : "🏪  Buka Toko");
            }
        });
    }

    /** Blokir thread non-EDT sampai user klik OK. */
    public void waitForContinue() {
        Runnable show = () ->
            JOptionPane.showMessageDialog(this,
                "Klik OK untuk melanjutkan...", "Lanjutkan",
                JOptionPane.INFORMATION_MESSAGE);

        if (SwingUtilities.isEventDispatchThread()) {
            show.run();
        } else {
            try { SwingUtilities.invokeAndWait(show); }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  COLORED LOG WRITER
    // ══════════════════════════════════════════════════════════════
    private void appendColored(String text, Color base) {
        try {
            for (String line : text.split("\n", -1)) {
                Color c = resolveColor(line, base);
                SimpleAttributeSet a = new SimpleAttributeSet();
                StyleConstants.setForeground(a, c);
                StyleConstants.setFontFamily(a, "Consolas");
                StyleConstants.setFontSize(a, 13);
                doc.insertString(doc.getLength(), line + "\n", a);
            }
            txtOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /** Pilih warna baris log berdasarkan kontennya. */
    private Color resolveColor(String line, Color fallback) {
        if (line.startsWith("═") || line.startsWith("="))          return ACCENT;
        if (line.contains("GAME OVER") || line.contains("kalah"))  return RED_COLOR;
        if (line.contains("BOSS")     || line.contains("NAGA"))    return GOLD_COLOR;
        if (line.contains("LEVEL UP") || line.contains("berhasil"))return GREEN_COLOR;
        if (line.contains("bertambah")|| line.contains("Profit"))  return GREEN_COLOR;
        if (line.contains("Reputasi"))                              return PURPLE_COLOR;
        if (line.startsWith("Customer"))                            return BLUE_COLOR;
        if (line.startsWith("⚠"))                                  return GOLD_COLOR;
        if (line.startsWith("❌"))                                  return RED_COLOR;
        return fallback;
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPER — Buttons & Labels
    // ══════════════════════════════════════════════════════════════
    private void setButtonsEnabled(boolean on) {
        if (actionButtons == null) return;
        for (JButton b : actionButtons) b.setEnabled(on);
    }

    private JProgressBar mkBar(Color fg, int val) {
        JProgressBar b = new JProgressBar(0, 100);
        b.setValue(val);
        b.setForeground(fg);
        b.setBackground(new Color(28, 22, 44));
        b.setBorderPainted(false);
        b.setStringPainted(false);
        b.setPreferredSize(new Dimension(0, 7));
        return b;
    }

    private JLabel lbl(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JButton menuBtn(String label, String tooltip, Color bg) {
        JButton b = new JButton("<html><center>" + label + "</center></html>");
        b.setToolTipText(tooltip);
        b.setBackground(bg);
        b.setForeground(TEXT_LIGHT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        Color hover = bg.brighter();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(hover); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private JButton bigBtn(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        Color hover = bg.brighter();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private JButton miniBtn(String label) {
        JButton b = new JButton(label);
        b.setBackground(new Color(38, 32, 58));
        b.setForeground(TEXT_DIM);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Coba pakai Nimbus L&F untuk tampilan lebih modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}