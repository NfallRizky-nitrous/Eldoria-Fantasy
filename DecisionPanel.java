/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * DecisionPanel — Panel keputusan yang muncul di dalam GameGUI.
 * Dipakai untuk SEMUA situasi yang butuh pilihan dari player:
 *  - Event (poorCustomer, thiefEvent, gangsterEvent, dll.)
 *  - Tawar-menawar customer
 *  - Pilihan combat (Serang, Bertahan, Potion, dll.)
 *  - Story pause (klik Lanjutkan)
 *
 * Cara pakai:
 *   panel.showDecision("Judul", "Deskripsi narasi",
 *       new String[]{"Pilihan A", "Pilihan B"},
 *       choiceIndex -> { ... lakukan sesuatu dengan pilihan ... });
 *
 *   panel.showContinue("Teks cerita panjang...", () -> { ... setelah klik OK ... });
 */
public class DecisionPanel extends JPanel {

    // ── Warna (konsisten dengan GameGUI & ShopGUI) ────────────────
    private static final Color BG_DARK     = new Color(18,  15,  28);
    private static final Color BG_PANEL    = new Color(32,  28,  48);
    private static final Color BG_CARD     = new Color(40,  35,  60);
    private static final Color GOLD_COLOR  = new Color(255, 200,  60);
    private static final Color GREEN_COLOR = new Color( 80, 200, 120);
    private static final Color RED_COLOR   = new Color(220,  80,  80);
    private static final Color BLUE_COLOR  = new Color( 80, 160, 220);
    private static final Color ACCENT      = new Color(110,  80, 200);
    private static final Color TEXT_LIGHT  = new Color(220, 215, 235);
    private static final Color TEXT_DIM    = new Color(130, 120, 155);

    // ── Layout internal ───────────────────────────────────────────
    private final JLabel     lblIcon;
    private final JLabel     lblTitle;
    private final JTextArea  txtNarrative;
    private final JPanel     btnContainer;
    private final JPanel     cardWrapper;

    // Callback setelah pilihan dibuat
    private Consumer<Integer> onChoice;
    private Runnable          onContinue;

    // ─────────────────────────────────────────────────────────────
    public DecisionPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // overlay semi-transparan

        // Kartu tengah
        cardWrapper = new JPanel(new BorderLayout(0, 12));
        cardWrapper.setBackground(BG_CARD);
        cardWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2, true),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        cardWrapper.setMaximumSize(new Dimension(620, 400));

        // ── Icon + Title ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        lblIcon  = new JLabel("⚡");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcon.setForeground(GOLD_COLOR);

        lblTitle = new JLabel("Event");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 16));
        lblTitle.setForeground(GOLD_COLOR);

        header.add(lblIcon);
        header.add(lblTitle);

        // ── Narasi ──
        txtNarrative = new JTextArea();
        txtNarrative.setEditable(false);
        txtNarrative.setLineWrap(true);
        txtNarrative.setWrapStyleWord(true);
        txtNarrative.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNarrative.setBackground(new Color(28, 24, 40));
        txtNarrative.setForeground(TEXT_LIGHT);
        txtNarrative.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtNarrative.setFocusable(false);

        JScrollPane narrativeScroll = new JScrollPane(txtNarrative);
        narrativeScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 55, 80), 1));
        narrativeScroll.setPreferredSize(new Dimension(560, 140));
        narrativeScroll.getViewport().setBackground(new Color(28, 24, 40));

        // ── Tombol pilihan ──
        btnContainer = new JPanel();
        btnContainer.setLayout(new BoxLayout(btnContainer, BoxLayout.Y_AXIS));
        btnContainer.setOpaque(false);
        btnContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        cardWrapper.add(header,          BorderLayout.NORTH);
        cardWrapper.add(narrativeScroll, BorderLayout.CENTER);
        cardWrapper.add(btnContainer,    BorderLayout.SOUTH);

        // Center the card
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(cardWrapper);

        add(center, BorderLayout.CENTER);
        setVisible(false);
    }

    // ══════════════════════════════════════════════════════════════
    //  PUBLIC API
    // ══════════════════════════════════════════════════════════════

    /**
     * Tampilkan panel dengan pilihan-pilihan (2-6 tombol).
     * @param icon      emoji/icon untuk header (misal "🧙", "⚔", "💰")
     * @param title     judul event
     * @param narrative teks cerita/deskripsi
     * @param choices   array label tombol
     * @param callback  dipanggil dengan index pilihan (0-based)
     */
    public void showDecision(String icon, String title, String narrative,
                              String[] choices, Consumer<Integer> callback) {
        this.onChoice   = callback;
        this.onContinue = null;

        lblIcon.setText(icon);
        lblTitle.setText(title);
        txtNarrative.setText(narrative);
        txtNarrative.setCaretPosition(0);

        btnContainer.removeAll();

        // Warna tombol bergilir
        Color[] colors = {
            new Color(40, 110, 60),   // hijau
            new Color(40, 90, 160),   // biru
            new Color(140, 90, 20),   // emas
            new Color(120, 40, 120),  // ungu
            new Color(160, 60, 40),   // merah
            new Color(60, 100, 100),  // teal
        };

        for (int i = 0; i < choices.length; i++) {
            final int idx = i;
            Color bg = colors[i % colors.length];
            JButton btn = choiceButton((i + 1) + ".  " + choices[i], bg);
            btn.addActionListener(e -> {
                setVisible(false);
                if (onChoice != null) onChoice.accept(idx);
            });
            btnContainer.add(btn);
            if (i < choices.length - 1)
                btnContainer.add(Box.createVerticalStrut(6));
        }

        btnContainer.revalidate();
        btnContainer.repaint();
        setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Tampilkan panel story/narasi dengan satu tombol "Lanjutkan".
     * @param narrative teks cerita
     * @param callback  dipanggil setelah klik Lanjutkan
     */
    public void showContinue(String narrative, Runnable callback) {
        this.onContinue = callback;
        this.onChoice   = null;

        lblIcon.setText("📖");
        lblTitle.setText("Cerita");
        txtNarrative.setText(narrative);
        txtNarrative.setCaretPosition(0);

        btnContainer.removeAll();

        JButton btnOk = choiceButton("▶  Lanjutkan", new Color(80, 60, 140));
        btnOk.addActionListener(e -> {
            setVisible(false);
            if (onContinue != null) onContinue.run();
        });

        btnContainer.add(btnOk);
        btnContainer.revalidate();
        btnContainer.repaint();
        setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Tampilkan panel combat dengan pilihan aksi.
     */
    public void showCombatActions(String statusText, boolean hasPotion,
                                   boolean hasSoldier, Consumer<Integer> callback) {
        this.onChoice = callback;

        lblIcon.setText("⚔");
        lblTitle.setText("Giliran Kamu!");
        txtNarrative.setText(statusText);
        txtNarrative.setCaretPosition(0);

        btnContainer.removeAll();

        String[][] actions = {
            {"🗡  Serang Normal",    "Serangan biasa"},
            {"💥  Serang Keras",     "2x DMG, 60% hit"},
            {"🛡  Bertahan",         "Damage masuk -50% ronde ini"},
            {"🧪  Gunakan Potion",   hasPotion ? "Pulihkan HP" : "(Tidak ada potion)"},
            {"⚔  Perintah Soldier", hasSoldier ? "Kirim soldier menyerang" : "(Belum ada soldier)"},
            {"📯  Panggil Darurat",  "Bayar 30 gold, ada peluang gagal"},
        };

        Color[] colors = {
            new Color(160, 60,  40),   // merah — serang
            new Color(200, 80,  20),   // oranye — serang keras
            new Color(40,  80, 160),   // biru — bertahan
            new Color(40, 140,  80),   // hijau — potion
            new Color(120, 40, 120),   // ungu — soldier
            new Color(80,  80,  40),   // coklat — darurat
        };

        for (int i = 0; i < actions.length; i++) {
            final int idx = i;
            boolean enabled = true;
            if (i == 3 && !hasPotion)   enabled = false;
            if (i == 4 && !hasSoldier)  enabled = false;

            JButton btn = choiceButton(actions[i][0] + "  —  " + actions[i][1], colors[i]);
            btn.setEnabled(enabled);
            if (!enabled) {
                btn.setBackground(new Color(40, 38, 55));
                btn.setForeground(TEXT_DIM);
            }
            btn.addActionListener(e -> {
                setVisible(false);
                if (onChoice != null) onChoice.accept(idx);
            });
            btnContainer.add(btn);
            if (i < actions.length - 1)
                btnContainer.add(Box.createVerticalStrut(5));
        }

        btnContainer.revalidate();
        btnContainer.repaint();
        setVisible(true);
        revalidate();
        repaint();
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPER
    // ══════════════════════════════════════════════════════════════
    private JButton choiceButton(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }
}

