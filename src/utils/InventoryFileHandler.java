package utils;

import model.Inventory;
import model.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.Scanner;

public class InventoryFileHandler {
    private static final String FILE_NAME = "inventory.csv";

    /** Loads products from CSV into the given Inventory */
    public static void loadInventory(Inventory inv) {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",", -1);
                if (parts.length != 6) continue;
                String name  = parts[0];
                String cat   = parts[1];
                String sub   = parts[2];
                double cost  = Double.parseDouble(parts[3]);
                double price = Double.parseDouble(parts[4]);
                int qty      = Integer.parseInt(parts[5]);
                String imgPath = parts[6].isEmpty() ? null : parts[6];
                inv.addProduct(new Product(name, cat, sub, cost, price, qty,imgPath));
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    /** Saves all products from Inventory into CSV */
    public static void saveInventory(Inventory inv) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Product p : inv.getProducts()) {
                out.printf("%s,%s,%s,%.2f,%.2f,%d%n",
                        p.getName(),
                        p.getCategory(),
                        p.getSubCategory(),
                        p.getCostPrice(),
                        p.getRetailPrice(),
                        p.getQuantity(),
                        p.getImagePath() != null ? p.getImagePath() : ""
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }


    // Writes a text report for one product, including details and stock count
    public static void exportProductReport(Inventory inv, String name) {
        Product p = inv.searchByName(name);
        if (p == null) {
            System.out.println("✖ No such product.");
            return;
        }

        String filename = name + "_report.txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("Product Report: " + name);
            out.printf("Category: %s > %s%n", p.getCategory(), p.getSubCategory());
            out.printf("Cost Price: %.2f%n", p.getCostPrice());
            out.printf("Retail Price: %.2f%n", p.getRetailPrice());
            out.printf("Quantity in Stock: %d%n", p.getQuantity());
            System.out.println("✔ Report written to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
        }
    }
    public static void exportFullInventoryReport(Inventory inv) {
        String filename = "full_inventory_report.txt";
        double totalCost   = 0;
        double totalRetail = 0;

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("Full Inventory Report");
            out.println("=====================\n");
            for (Product p : inv.getProducts()) {
                // Write each product line
                out.printf("%s | %s > %s | Qty: %d | Cost: %.2f | Price: %.2f%n",
                        p.getName(), p.getCategory(), p.getSubCategory(),
                        p.getQuantity(), p.getCostPrice(), p.getRetailPrice());

                // Accumulate totals
                totalCost   += p.getCostPrice()   * p.getQuantity();
                totalRetail += p.getRetailPrice() * p.getQuantity();
            }

            // Blank line before totals
            out.println();
            out.printf("TOTAL COST VALUE:   %.2f%n", totalCost);
            out.printf("TOTAL RETAIL VALUE: %.2f%n", totalRetail);

            System.out.println("✔ Full inventory report written to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing full report: " + e.getMessage());
        }
    }
}
