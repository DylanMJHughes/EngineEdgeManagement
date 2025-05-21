package model;

import java.util.List;
import java.util.ArrayList;

public class Inventory {
    private List<Product> products = new ArrayList<>();

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

    // Getter for UI and other consumers
    public List<Product> getProducts() {
        return products;
    }

    // Search by name (returns first match)
    public Product searchByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name.trim())) {
                return p;
            }
        }
        return null;
    }

    // Search and print all in a category
    public void searchByCategory(String category) {
        boolean found = false;
        for (Product p : products) {
            if (p.getCategoryType().name().equalsIgnoreCase(category.trim())) {
                p.printDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products in that category.");
        }
    }

    // Search and print all in a sub-category
    public void searchBySubCategory(String subCategory) {
        boolean found = false;
        for (Product p : products) {
            if (p.getSubCategoryType().name().equalsIgnoreCase(subCategory.trim())) {
                p.printDetails();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products in that sub-category.");
        }
    }
}
