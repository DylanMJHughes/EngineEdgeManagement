
package utils;

import model.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
                // now expecting exactly 7 columns
                if (parts.length != 7) continue;

                String name    = parts[0];
                String catStr  = parts[1].trim().toUpperCase();
                String sub     = parts[2];
                double cost    = Double.parseDouble(parts[3]);
                double price   = Double.parseDouble(parts[4]);
                int qty        = Integer.parseInt(parts[5]);
                String imgPath = parts[6].isEmpty() ? null : parts[6];

                // parse enum
                CategoryType ct;
                try {
                    ct = CategoryType.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown category in CSV: " + catStr);
                    continue;
                }


                Product p;
                switch (ct) {
                    case Engine:       p = new Engine(name, sub, cost, price, qty, imgPath);       break;
                    case Transmission: p = new Transmission(name, sub, cost, price, qty, imgPath); break;
                    case Suspension:   p = new Suspension(name, sub, cost, price, qty, imgPath);   break;
                    case Brakes:       p = new Brakes(name, sub, cost, price, qty, imgPath);       break;
                    case Electrical:   p = new Electrical(name, sub, cost, price, qty, imgPath);   break;
                    case Cooling:      p = new Cooling(name, sub, cost, price, qty, imgPath);      break;
                    case Exhaust:      p = new Exhaust(name, sub, cost, price, qty, imgPath);      break;
                    case Fuel:         p = new Fuel(name, sub, cost, price, qty, imgPath);         break;
                    case Body:         p = new Body(name, sub, cost, price, qty, imgPath);         break;
                    default:
                        // shouldn't happen
                        continue;
                }

                inv.addProduct(p);
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    /** Saves all products from Inventory into the CSV */
    public static void saveInventory(Inventory inv) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Product p : inv.getProducts()) {
                out.printf(
                        "%s,%s,%s,%.2f,%.2f,%d,%s%n",
                        p.getName(),
                        p.getCategoryType().name(),      // write the enum name
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


    public static void exportProductReport(Inventory inv, String productName) {
        Product p = inv.searchByName(productName);
        if (p == null) {
            System.out.println("No such product: " + productName);
            return;
        }

        String fileName = "report_" + productName.replaceAll("\\s+", "_") + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("Product Report for: " + productName);
            out.println("-----------------------------");
            out.println("Name:        " + p.getName());
            out.println("Category:    " + p.getCategoryType().name());
            out.println("Sub-category: " + p.getSubCategory());
            out.printf("Cost Price:  %.2f%n", p.getCostPrice());
            out.printf("Retail Price: %.2f%n", p.getRetailPrice());
            out.println("Quantity:    " + p.getQuantity());
            System.out.println("Exported product report to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting product report: " + e.getMessage());
        }
    }
/** Export full inventory report to a text file */
    public static void exportFullInventoryReport(Inventory inv) {
        String fileName = "full_inventory_report.txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("Full Inventory Report");
            out.println("---------------------");

            double totalCostValue   = 0;
            double totalRetailValue = 0;

            for (Product p : inv.getProducts()) {
                out.printf(
                        "%s (%s > %s): Qty=%d, Cost=%.2f, Price=%.2f%n",
                        p.getName(),
                        p.getCategoryType().name(),
                        p.getSubCategory(),
                        p.getQuantity(),
                        p.getCostPrice(),
                        p.getRetailPrice()
                );
                totalCostValue   += p.getCostPrice()   * p.getQuantity();
                totalRetailValue += p.getRetailPrice() * p.getQuantity();
            }

            out.println();
            out.printf("Total cost value:   %.2f%n", totalCostValue);
            out.printf("Total retail value: %.2f%n", totalRetailValue);

            System.out.println("Exported full inventory report to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting full inventory report: " + e.getMessage());
        }
    }
}