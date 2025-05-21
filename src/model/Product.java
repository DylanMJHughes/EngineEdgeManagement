package model;

import model.CategoryType;
import model.SubCategoryType;

public abstract class Product {

    private String name;
    private double costPrice;
    private double retailPrice;
    private int quantity;
    private String imagePath;

    // Constructor including image path
    public Product(String name,
                   double costPrice,
                   double retailPrice,
                   int quantity,
                   String imagePath) {
        this.name        = name;
        this.costPrice   = costPrice;
        this.retailPrice = retailPrice;
        this.quantity    = quantity;
        this.imagePath   = imagePath;
    }


     // Convenience constructor without image
    public Product(String name,
                   double costPrice,
                   double retailPrice,
                   int quantity) {
        this(name, costPrice, retailPrice, quantity, null);
    }

    // Each subclass defines its top-level category
    public abstract CategoryType getCategoryType();

    // Each subclass defines its sub-category
    public abstract SubCategoryType getSubCategoryType();

    //Increase stock by a given amount

    public void increaseStock(int amt) {
        this.quantity += amt;
    }

    // Decrease stock by a given amount
    public void decreaseStock(int amt) {
        this.quantity -= amt;
    }

    // Getters
    public String getName()           { return name; }
    public int    getQuantity()       { return quantity; }
    public double getCostPrice()      { return costPrice; }
    public double getRetailPrice()    { return retailPrice; }
    public String getImagePath()      { return imagePath; }

    // print
    public void printDetails() {
        System.out.printf(
                "%s [%s > %s] Qty: %d | Cost: %.2f | Price: %.2f%n",
                name,
                getCategoryType().name(),
                getSubCategoryType().name(),
                quantity,
                costPrice,
                retailPrice
        );
    }


}