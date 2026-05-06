/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
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
    public String getInfoText() {
    return name + " | Beli: " + buyPrice + "g | Jual: " + sellPrice + "g\n"
         + "   - " + description;
}
    
    @Override
public String toString() {
    return name + " (Beli: " + buyPrice + "g, Jual: " + sellPrice + "g)";
}

    void showInfo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
