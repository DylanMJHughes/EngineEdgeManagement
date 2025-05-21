package utils;

import java.io.*;

import model.Inventory;
import model.Product;
import model.CategoryType;
import model.SubCategoryType;

// import each of your concrete product classes
import model.Categorys.Engine;
import model.Categorys.Brakes;
import model.Categorys.Cooling;
import model.Categorys.Electrical;
import model.Categorys.Exhaust;
import model.Categorys.Fuel;
import model.Categorys.Suspension;
import model.Categorys.Transmission;
import model.Categorys.Body;

public class InventoryFileHandler {
    private static final String FILE_NAME = "inventory.csv";

    /** Loads CSV rows into your Inventory */
    public static void loadInventory(Inventory inv) {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = in.readLine()) != null) {
                // CSV format: name,subCategory,category,cost,price,qty,imagePath
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;

                String name    = parts[0].trim();
                String subStr  = parts[1].trim().toUpperCase();
                String catStr  = parts[2].trim().toUpperCase();
                double cost    = Double.parseDouble(parts[3]);
                double price   = Double.parseDouble(parts[4]);
                String[] qtyAndPath = parts[5].split(",", 2);
                int    qty     = Integer.parseInt(qtyAndPath[0].trim());
                String imgPath = (qtyAndPath.length > 1) ? qtyAndPath[1].trim() : "";

                // parse enums
                CategoryType    ct;
                SubCategoryType subType;
                try {
                    ct      = CategoryType.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown category in CSV: " + catStr);
                    continue;
                }
                try {
                    subType = SubCategoryType.valueOf(subStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown sub-category in CSV: " + subStr);
                    continue;
                }

                Product p;
                switch (ct) {
                    case Engine:
                        p = new Engine(name, subType, cost, price, qty, imgPath);
                        break;
                    case Brakes:
                        p = new Brakes(name, subType, cost, price, qty, imgPath);
                        break;
                    case Cooling:
                        p = new Cooling(name, subType, cost, price, qty, imgPath);
                        break;
                    case Electrical:
                        p = new Electrical(name, subType, cost, price, qty, imgPath);
                        break;
                    case Exhaust:
                        p = new Exhaust(name, subType, cost, price, qty, imgPath);
                        break;
                    case Fuel:
                        p = new Fuel(name, subType, cost, price, qty, imgPath);
                        break;
                    case Suspension:
                        p = new Suspension(name, subType, cost, price, qty, imgPath);
                        break;
                    case Transmission:
                        p = new Transmission(name, subType, cost, price, qty, imgPath);
                        break;
                    case Body:
                        p = new Body(name, subType, cost, price, qty, imgPath);
                        break;
                    default:
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
                        p.getSubCategoryType().name(),
                        p.getCategoryType().name(),
                        p.getCostPrice(),
                        p.getRetailPrice(),
                        p.getQuantity(),
                        p.getImagePath()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    /** Exports a single product report to a csv*/
    public static void exportProductReport(Inventory inv, String productName) {
        Product p = inv.searchByName(productName);
        if (p == null) {
            System.err.println("Product not found: " + productName);
            return;
        }
        String fileName = productName + "_report.csv";
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("Product Report for: " + productName);
            out.printf("Category: %s%n", p.getCategoryType());
            out.printf("Sub-Category: %s%n", p.getSubCategoryType());
            out.printf("Cost Price: %.2f%n", p.getCostPrice());
            out.printf("Retail Price: %.2f%n", p.getRetailPrice());
            out.printf("Quantity: %d%n", p.getQuantity());

            System.out.println("Exported product report to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting product report: " + e.getMessage());
        }
    }

    /** Exports a full inventory report to a text file */
    public static void exportFullInventoryReport(Inventory inv) {
        String fileName = "full_inventory_report.csv";
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("Name,SubCategory,Category,Cost,Price,Quantity");

            double totalCost = 0;
            double totalRetail = 0;

            for (Product p : inv.getProducts()) {
                out.printf(
                        "%s,%s,%s,%.2f,%.2f,%d%n",
                        p.getName(),
                        p.getSubCategoryType().name(),
                        p.getCategoryType().name(),
                        p.getCostPrice(),
                        p.getRetailPrice(),
                        p.getQuantity()
                );
                totalCost += p.getCostPrice() * p.getQuantity();
                totalRetail += p.getRetailPrice() * p.getQuantity();
            }

            // Now the totals row only needs 6 columns
            out.printf("Totals,,,,%.2f,%.2f%n", totalCost, totalRetail);

            System.out.println("Exported full inventory report to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting full inventory report: " + e.getMessage());
        }
    }

}
