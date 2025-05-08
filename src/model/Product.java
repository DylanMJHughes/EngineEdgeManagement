package model;

public class Product {
    private String name, category, subCategory;
    private double costPrice, retailPrice;
    private int quantity;

    public Product(String name, String category, String subCategory,
                   double costPrice, double retailPrice, int quantity) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.costPrice = costPrice;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }

    public void increaseStock(int amt) { quantity += amt; }
    public void decreaseStock(int amt) { quantity -= amt; }

    public void printDetails() {
        System.out.printf("%s | %s > %s | Qty: %d | Cost: %.2f | Price: %.2f%n",
                name, category, subCategory, quantity, costPrice, retailPrice);
    }
    /** go getters **/
    public String getName()     {return name; }
    public String getCategory()     {return category; }
    public String getSubCategory() { return subCategory; }
    public double getCostPrice()   { return costPrice; }
    public double getRetailPrice() { return retailPrice; }
    public int getQuantity()       { return quantity; }
}


