/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop.gui;

/**
 *
 * @author Asus
 */
import java.util.function.Consumer;

public interface GUIBridge {

    /**
     * Tampilkan pilihan decision dan tunggu callback.
     * @param icon      emoji icon
     * @param title     judul panel
     * @param narrative teks narasi/cerita
     * @param choices   label tombol pilihan
     * @param callback  dipanggil dengan index 0-based setelah player memilih
     */
    void showDecision(String icon, String title, String narrative,
                      String[] choices, Consumer<Integer> callback);

    /**
     * Tampilkan narasi story dan tunggu klik Lanjutkan.
     * @param narrative teks cerita
     * @param callback  dipanggil setelah player klik lanjut
     */
    void showContinue(String narrative, Runnable callback);

    /**
     * Tampilkan aksi combat dan tunggu callback.
     * @param statusText teks status player vs enemy
     * @param hasPotion  apakah player punya potion
     * @param hasSoldier apakah ada soldier yang direkrut
     * @param callback   dipanggil dengan index aksi 0-based
     */
    void showCombatActions(String statusText, boolean hasPotion,
                            boolean hasSoldier, Consumer<Integer> callback);

    /**
     * Tambahkan teks ke game log.
     */
    void appendLog(String text);

    /**
     * Update label status (gold, HP, dll.) di header.
     */
    void refreshStatus();
}
