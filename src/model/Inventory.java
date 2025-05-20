package model;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Product> products = new ArrayList<>();

    public void addProduct(Product p) {
        products.add(p);
    }

    public void showInventory() {
        if (products.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            for (Product p : products) {
                p.printDetails();
            }
        }
    }

    /**go getters **/
    public ArrayList<Product> getProducts() {
        return products;
    }

    /** Search method **/
    public Product searchByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public void searchByCategory(String category) {
        boolean found = false;
        for (Product p : products) {
            // compare the enum name to the user’s string (case‐insensitive)
            if (p.getCategoryType().name().equalsIgnoreCase(category.trim())) {
                p.printDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products in that category.");
        }
    }

    public void searchBySubCategory(String subCategory) {
        boolean found = false;
        for (Product p : products) {
            if (p.getSubCategory().equalsIgnoreCase(subCategory)) {
                p.printDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products in that sub-category.");
        }
    }
}


