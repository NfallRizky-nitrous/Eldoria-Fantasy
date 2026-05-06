/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */

import com.mycompany.fantasy.shop.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * CombatGUI — Jendela pertarungan penuh GUI.
 * Menggantikan CombatSystem.startCombat() di mode GUI.
 *
 * Setiap ronde:
 *  1. Tampilkan status HP player & enemy (dengan bar visual)
 *  2. Tampilkan DecisionPanel dengan 6 aksi
 *  3. Proses aksi → update log & HP bar
 *  4. Enemy turn otomatis setelah player
 *  5. Cek menang/kalah → tutup jendela
 */
public class CombatGUI extends JDialog {

    // ── Warna ─────────────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(14, 10, 24);
    private static final Color BG_PANEL    = new Color(24, 20, 38);
    private static final Color GOLD_COLOR  = new Color(255, 200, 60);
    private static final Color GREEN_COLOR = new Color(80, 200, 120);
    private static final Color RED_COLOR   = new Color(220, 80, 80);
    private static final Color ACCENT      = new Color(110, 80, 200);
    private static final Color TEXT_LIGHT  = new Color(220, 215, 235);
    private static final Color TEXT_DIM    = new Color(130, 120, 155);

    // ── State ─────────────────────────────────────────────────────
    private final Player        player;
    private final Enemy         enemy;
    private final List<Soldier> soldiers;
    private final Random        random = new Random();
    private int                 round  = 1;
    private boolean             combatOver = false;

    // ── UI ────────────────────────────────────────────────────────
    private JTextArea       txtLog;
    private JProgressBar    playerHpBar, enemyHpBar;
    private JLabel          lblPlayerHp, lblEnemyHp;
    private JLabel          lblPlayerName, lblEnemyName;
    private DecisionPanel   decisionPanel;
    private JPanel          mainContent;

    // ─────────────────────────────────────────────────────────────
    public CombatGUI(JFrame parent, Player player, Enemy enemy, List<Soldier> soldiers) {
        super(parent, "⚔  Pertarungan — " + enemy.getName(), true);
        this.player   = player;
        this.enemy    = enemy;
        this.soldiers = soldiers;

        setSize(700, 580);
        setLocationRelativeTo(parent);
        setBackground(BG_DARK);
        getContentPane().setBackground(BG_DARK);
        setResizable(false);

        buildUI();
        showEnemyIntro();
    }

    // ══════════════════════════════════════════════════════════════
    //  BUILD UI
    // ══════════════════════════════════════════════════════════════
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        add(buildStatusBar(), BorderLayout.NORTH);

        // Area tengah: log + decision panel overlay
        mainContent = new JPanel();
        mainContent.setLayout(new OverlayLayout(mainContent));
        mainContent.setBackground(BG_DARK);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtLog.setBackground(new Color(10, 8, 18));
        txtLog.setForeground(TEXT_LIGHT);
        txtLog.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, ACCENT));
        scroll.getViewport().setBackground(new Color(10, 8, 18));
        scroll.setAlignmentX(0.5f);
        scroll.setAlignmentY(0.5f);

        decisionPanel = new DecisionPanel();
        decisionPanel.setAlignmentX(0.5f);
        decisionPanel.setAlignmentY(0.5f);

        mainContent.add(decisionPanel);
        mainContent.add(scroll);

        add(mainContent, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Status bar HP ─────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new GridLayout(1, 2, 4, 0));
        bar.setBackground(new Color(8, 6, 16));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Player
        JPanel playerCard = hpCard(true);
        // Enemy
        JPanel enemyCard  = hpCard(false);

        bar.add(playerCard);
        bar.add(enemyCard);
        return bar;
    }

    private JPanel hpCard(boolean isPlayer) {
        JPanel card = new JPanel(new BorderLayout(6, 4));
        card.setOpaque(false);

        String name = isPlayer ? player.getName() : enemy.getName();
        JLabel lblName = new JLabel(isPlayer ? "👤 " + name : "👹 " + name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(isPlayer ? GREEN_COLOR : RED_COLOR);

        JLabel lblHp = new JLabel(isPlayer
            ? player.getHp() + " / " + player.getMaxHp()
            : enemy.getHp() + " / " + enemy.getMaxHp());
        lblHp.setFont(new Font("Consolas", Font.PLAIN, 12));
        lblHp.setForeground(TEXT_DIM);
        lblHp.setHorizontalAlignment(SwingConstants.RIGHT);

        JProgressBar hpBar = new JProgressBar(0, isPlayer ? player.getMaxHp() : enemy.getMaxHp());
        hpBar.setValue(isPlayer ? player.getHp() : enemy.getHp());
        hpBar.setForeground(isPlayer ? GREEN_COLOR : RED_COLOR);
        hpBar.setBackground(new Color(40, 30, 30));
        hpBar.setBorderPainted(false);
        hpBar.setStringPainted(false);
        hpBar.setPreferredSize(new Dimension(0, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblName, BorderLayout.WEST);
        top.add(lblHp,   BorderLayout.EAST);

        card.add(top,   BorderLayout.CENTER);
        card.add(hpBar, BorderLayout.SOUTH);

        if (isPlayer) { playerHpBar = hpBar; lblPlayerHp = lblHp; lblPlayerName = lblName; }
        else          { enemyHpBar  = hpBar; lblEnemyHp  = lblHp; lblEnemyName  = lblName; }

        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footer.setBackground(new Color(8, 6, 16));
        footer.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT));

        JLabel lbl = new JLabel("Ronde: 1  |  Pilih aksimu di atas");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_DIM);
        footer.add(lbl);
        return footer;
    }

    // ══════════════════════════════════════════════════════════════
    //  COMBAT FLOW
    // ══════════════════════════════════════════════════════════════

    private void showEnemyIntro() {
        log("══════════════════════════════════════════");
        log("         ⚔  MUSUH MUNCUL!");
        log("══════════════════════════════════════════");
        log("  " + enemy.getName() + " menghadangmu!");
        log("  HP: " + enemy.getMaxHp() + "  |  ATK: " + enemy.getMaxHp() / 5 + "-" + enemy.getMaxHp() / 3);
        log("");

        decisionPanel.showContinue(
            enemy.getName() + " telah muncul!\n\n" +
            "HP: " + enemy.getMaxHp() + "\n\n" +
            "Bersiaplah untuk bertarung!",
            this::startRound
        );
    }

    private void startRound() {
        if (combatOver) return;
        if (round > 20) {
            log("\nPertarungan terlalu lama! " + enemy.getName() + " mundur...");
            endCombat(false);
            return;
        }

        log("\n─── Ronde " + round + " ───────────────────────────────");
        log("[ " + player.getName() + " ]  HP: " + player.getHp() + "/" + player.getMaxHp()
            + "  ATK: " + player.getAttack());
        log("[ " + enemy.getName() + " ]  HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
        log("");

        boolean hasSoldier = soldiers != null && soldiers.stream().anyMatch(Soldier::isRecruited);
        boolean hasPotion  = player.hasHealthItem();

        String statusText =
            "[ " + player.getName() + " ]\n" +
            "HP: " + player.getHp() + " / " + player.getMaxHp() +
            "  |  ATK: " + player.getAttack() +
            (hasPotion  ? "  |  🧪 Punya Potion"   : "") +
            (hasSoldier ? "  |  ⚔ Soldier Siap"    : "") + "\n\n" +
            "[ " + enemy.getName() + " ]\n" +
            "HP: " + enemy.getHp() + " / " + enemy.getMaxHp();

        decisionPanel.showCombatActions(statusText, hasPotion, hasSoldier, this::processPlayerAction);
    }

    private void processPlayerAction(int actionIndex) {
        log(">>> Giliran " + player.getName() + ":");

        switch (actionIndex) {
            case 0 -> { // Serang Normal
                int dmg = player.getAttack() + random.nextInt(11);
                enemy.takeDamage(dmg);
                log("  🗡  Kamu menyerang " + enemy.getName() + "! (-" + dmg + " HP)");
            }
            case 1 -> { // Serang Keras
                if (random.nextInt(100) < 60) {
                    int dmg = (player.getAttack() + random.nextInt(11)) * 2;
                    enemy.takeDamage(dmg);
                    log("  💥  Serangan keras mengenai! (-" + dmg + " HP)");
                } else {
                    log("  💨  Serangan keras meleset!");
                }
            }
            case 2 -> { // Bertahan
                player.setDefending(true);
                log("  🛡  Kamu bersiap bertahan. Damage berkurang 50% ronde ini.");
            }
            case 3 -> { // Potion
                if (player.hasHealthItem()) {
                    int healed = player.useHealthItem();
                    log("  🧪  Kamu menggunakan potion! (+" + healed + " HP)");
                } else {
                    log("  ⚠  Tidak ada potion! Giliran terbuang.");
                }
            }
            case 4 -> { // Soldier
                boolean any = soldiers != null && soldiers.stream().anyMatch(Soldier::isRecruited);
                if (any) {
                    for (Soldier s : soldiers) {
                        if (s.isRecruited()) {
                            int dmg = s.fightWith(enemy);
                            log("  ⚔  " + s.getName() + " menyerang! (-" + dmg + " HP)");
                        }
                    }
                } else {
                    log("  ⚠  Tidak ada soldier. Giliran terbuang.");
                }
            }
            case 5 -> { // Darurat
                int dmg = Soldier.emergencyCall(player, enemy);
                if (dmg > 0) log("  📯  Prajurit darurat datang! (-" + dmg + " HP musuh)");
                else log("  📯  Prajurit darurat terlambat!");
            }
        }

        updateHpBars();

        if (!enemy.isAlive()) {
            endCombat(true);
            return;
        }

        // Enemy turn setelah delay kecil
        Timer timer = new Timer(600, e -> enemyTurn());
        timer.setRepeats(false);
        timer.start();
    }

    private void enemyTurn() {
        if (combatOver || !player.isAlive()) return;

        log("\n>>> Giliran " + enemy.getName() + ":");
        int baseDmg = enemy.rollAttack();

        // Soldier blok
        int blocked = 0;
        if (soldiers != null) {
            for (Soldier s : soldiers) {
                if (s.isRecruited()) {
                    int def = s.defend();
                    blocked += def;
                    log("  🛡  " + s.getName() + " memblok " + def + " damage!");
                }
            }
        }

        if (player.isDefending()) {
            baseDmg = baseDmg / 2;
            player.setDefending(false);
            log("  🛡  Pertahananmu mengurangi damage!");
        }

        int finalDmg = Math.max(1, baseDmg - blocked);
        player.takeDamage(finalDmg);
        log("  💢  " + enemy.getName() + " menyerang! (-" + finalDmg + " HP)");

        // Efek khusus
        switch (enemy.getType()) {
            case "preman" -> {
                if (random.nextInt(100) < 20 && player.getGold() > 0) {
                    int stolen = Math.min(15, player.getGold());
                    player.spendGoldSilent(stolen);
                    log("  💰  Preman mencuri " + stolen + " gold!");
                }
            }
            case "ksatria_jahat" -> {
                if (random.nextInt(100) < 25) {
                    int bonus = enemy.rollAttack() / 2;
                    player.takeDamage(bonus);
                    log("  ⚔  Serangan ganda ksatria! (-" + bonus + " HP bonus)");
                }
            }
            case "naga" -> {
                if (random.nextInt(100) < 30) {
                    int fire = 15 + random.nextInt(20);
                    player.takeDamage(fire);
                    log("  🔥  Naga menyemburkan api! (-" + fire + " HP)");
                }
            }
        }

        updateHpBars();
        round++;

        if (!player.isAlive()) {
            endCombat(false);
        } else {
            startRound();
        }
    }

    private void endCombat(boolean playerWon) {
        combatOver = true;

        if (playerWon) {
            log("\n══════════════════════════════════════════");
            log("          🏆  KAMU MENANG!");
            log("══════════════════════════════════════════");
            log(enemy.getName() + " berhasil dikalahkan!");
            log("Reward: +" + enemy.getGoldReward() + " Gold  |  +" + enemy.getExpReward()
                + " EXP  |  +" + enemy.getRepReward() + " Reputasi");
            player.gainGold(enemy.getGoldReward());
            player.gainExp(enemy.getExpReward());
            player.gainReputation(enemy.getRepReward());

            decisionPanel.showContinue(
                "🏆  Kamu menang!\n\n" +
                enemy.getName() + " berhasil dikalahkan!\n\n" +
                "Reward:\n" +
                "  +  " + enemy.getGoldReward() + " Gold\n" +
                "  +  " + enemy.getExpReward()  + " EXP\n" +
                "  +  " + enemy.getRepReward()  + " Reputasi",
                this::dispose
            );
        } else {
            log("\n══════════════════════════════════════════");
            log("          💀  KAMU KALAH...");
            log("══════════════════════════════════════════");
            log(enemy.getName() + " mengalahkanmu.");
            int goldLost = Math.min(50, player.getGold());
            player.spendGoldSilent(goldLost);
            player.loseReputation(8);
            log("Toko mengalami kerusakan. Kehilangan " + goldLost + " gold dan 8 reputasi.");

            decisionPanel.showContinue(
                "💀  Kamu kalah...\n\n" +
                enemy.getName() + " mengalahkanmu.\n\n" +
                "Konsekuensi:\n" +
                "  -  " + goldLost + " Gold\n" +
                "  -  8 Reputasi\n\n" +
                "Toko mengalami kerusakan.",
                this::dispose
            );
        }

        updateHpBars();
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════
    private void updateHpBars() {
        SwingUtilities.invokeLater(() -> {
            // Player
            playerHpBar.setMaximum(player.getMaxHp());
            playerHpBar.setValue(Math.max(0, player.getHp()));
            lblPlayerHp.setText(player.getHp() + " / " + player.getMaxHp());
            int pct = (int)((double)player.getHp() / player.getMaxHp() * 100);
            if (pct > 60)      playerHpBar.setForeground(GREEN_COLOR);
            else if (pct > 30) playerHpBar.setForeground(new Color(220, 180, 40));
            else               playerHpBar.setForeground(RED_COLOR);

            // Enemy
            enemyHpBar.setMaximum(enemy.getMaxHp());
            enemyHpBar.setValue(Math.max(0, enemy.getHp()));
            lblEnemyHp.setText(enemy.getHp() + " / " + enemy.getMaxHp());
        });
    }

    private void log(String text) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(text + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
}
