import model.*;
import model.Categorys.*;
import utils.InventoryFileHandler;
import utils.OrderFileHandler;

import java.util.*;

public class Main {
    private static List<User> users = new ArrayList<>();
    private static List<User> pendingUsers = new ArrayList<>();
    private static List<Order> orders = new ArrayList<>();

    static {
        users.add(new User("admin", "admin", "admin"));
        users.add(new User("user",  "user",  "standard"));
    }

    public static void main(String[] args) {
        Inventory inv = new Inventory();
        InventoryFileHandler.loadInventory(inv);
        orders = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        boolean running = true;

        while (running) {
            User currentUser = null;
            while (currentUser == null && running) {
                System.out.println("1) Login");
                System.out.println("2) Register");
                System.out.println("3) Quit");
                System.out.print("> ");
                String mode = scan.nextLine().trim();
                switch (mode) {
                    case "1": currentUser = login(scan); break;
                    case "2": registerNewUser(scan); break;
                    case "3": running = false; break;
                    default:  System.out.println("Invalid choice.\n");
                }
            }
            if (!running) break;

            if ("admin".equals(currentUser.getRole())) {
                adminMenu(inv, scan, currentUser);
            } else {
                standardMenu(inv, scan);
            }
        }

        InventoryFileHandler.saveInventory(inv);
        OrderFileHandler.saveOrders(orders);
        scan.close();
        System.out.println("Inventory and orders saved. Goodbye!");
    }

    private static User login(Scanner scan) {
        User currentUser = null;
        while (currentUser == null) {
            System.out.print("Username: ");
            String u = scan.nextLine().trim();
            System.out.print("Password: ");
            String p = scan.nextLine().trim();
            for (User usr : users) {
                if (usr.getUsername().equals(u) && usr.getPassword().equals(p)) {
                    currentUser = usr;
                    break;
                }
            }
            if (currentUser == null) {
                System.out.println("✖ Invalid credentials, try again.\n");
            }
        }
        System.out.println("✔ Welcome " + currentUser.getUsername() + " (" + currentUser.getRole() + ")\n");
        return currentUser;
    }

    private static void registerNewUser(Scanner scan) {
        System.out.print("Choose a username: ");
        String u = scan.nextLine().trim();
        if (u.isEmpty()) return;
        for (User existing : users) {
            if (existing.getUsername().equals(u)) {
                System.out.println("✖ Username taken. Try again later.\n");
                return;
            }
        }
        for (User existing : pendingUsers) {
            if (existing.getUsername().equals(u)) {
                System.out.println("✖ Registration already pending.\n");
                return;
            }
        }
        System.out.print("Choose a password: ");
        String p = scan.nextLine().trim();
        pendingUsers.add(new User(u, p, "standard"));
        System.out.println("✔ Registration submitted. Wait for admin approval.\n");
    }

    private static void adminMenu(Inventory inv, Scanner scan, User me) {
        while (true) {
            System.out.println(
                    "== Admin Menu ==\n" +
                            "1. Add Product\n" +
                            "2. Restock Product\n" +
                            "3. Show Inventory\n" +
                            "4. Sell Product\n" +
                            "5. Search by Name\n" +
                            "6. Search by Category\n" +
                            "7. Search by Sub-category\n" +
                            "8. Export Product Report\n" +
                            "9. Export Full Inventory Report\n" +
                            "10. Settings\n" +
                            "11. Log out"
            );
            System.out.print("> ");
            String choice = scan.nextLine().trim();
            switch (choice) {
                case "1": addProduct(inv, scan); break;
                case "2": restockProduct(inv, scan); break;
                case "3": inv.showInventory(); break;
                case "4": sellProduct(inv, scan); break;
                case "5": searchByName(inv, scan); break;
                case "6": searchByCategory(inv, scan); break;
                case "7": searchBySubCategory(inv, scan); break;
                case "8":
                    System.out.print("Enter product name: ");
                    InventoryFileHandler.exportProductReport(inv, scan.nextLine().trim());
                    break;
                case "9":
                    InventoryFileHandler.exportFullInventoryReport(inv);
                    break;
                case "10": settingsMenu(scan, me); break;
                case "11": System.out.println("Logging out...\n"); return;
                default:  System.out.println("Invalid choice, try again.\n");
            }
        }
    }

    private static void standardMenu(Inventory inv, Scanner scan) {
        while (true) {
            System.out.println(
                    "== User Menu ==\n" +
                            "1. Sell Product\n" +
                            "2. Restock Product\n" +
                            "3. Show Inventory\n" +
                            "4. Search by Name\n" +
                            "5. Search by Category\n" +
                            "6. Search by Sub-category\n" +
                            "7. Export Product Report\n" +
                            "8. Export Full Inventory Report\n" +
                            "9. Log out"
            );
            System.out.print("> ");
            String choice = scan.nextLine().trim();
            switch (choice) {
                case "1": sellProduct(inv, scan); break;
                case "2": restockProduct(inv, scan); break;
                case "3": inv.showInventory(); break;
                case "4": searchByName(inv, scan); break;
                case "5": searchByCategory(inv, scan); break;
                case "6": searchBySubCategory(inv, scan); break;
                case "7":
                    System.out.print("Enter product name: ");
                    InventoryFileHandler.exportProductReport(inv, scan.nextLine().trim());
                    break;
                case "8": InventoryFileHandler.exportFullInventoryReport(inv); break;
                case "9": System.out.println("Logging out...\n"); return;
                default:  System.out.println("Invalid choice, try again.\n");
            }
        }
    }

    private static void settingsMenu(Scanner scan, User currentUser) {
        while (true) {
            System.out.println(
                    "\n== Settings ==\n" +
                            "1. Add User\n" +
                            "2. Delete User\n" +
                            "3. List Users\n" +
                            "4. Approve Registrations\n" +
                            "5. Back to Main Menu"
            );
            System.out.print("> ");
            String choice = scan.nextLine().trim();
            switch (choice) {
                case "1": addUser(scan); break;
                case "2": deleteUser(scan, currentUser); break;
                case "3": listUsers(); break;
                case "4": approveRegistrations(scan); break;
                case "5": return;
                default:  System.out.println("Invalid choice.\n");
            }
        }
    }

    private static void approveRegistrations(Scanner scan) {
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending registrations.\n");
            return;
        }
        while (true) {
            System.out.println("Pending registrations:");
            for (int i = 0; i < pendingUsers.size(); i++) {
                System.out.printf("%d) %s\n", i+1, pendingUsers.get(i).getUsername());
            }
            System.out.println("A) Approve by number   B) Back");
            System.out.print("> ");
            String inp = scan.nextLine().trim();
            if (inp.equalsIgnoreCase("B")) { System.out.println(); return; }
            try {
                int idx = Integer.parseInt(inp) - 1;
                if (idx < 0 || idx >= pendingUsers.size()) System.out.println("Invalid number.\n");
                else {
                    User approved = pendingUsers.remove(idx);
                    users.add(approved);
                    System.out.println(" Approved: " + approved.getUsername() + "\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.\n");
            }
        }
    }

    private static void addProduct(Inventory inv, Scanner scan) {
        System.out.println("Available categories: " + Arrays.toString(CategoryType.values()));
        System.out.print("Select category: ");
        String catStr = scan.nextLine().trim().toUpperCase();
        CategoryType catType;
        try { catType = CategoryType.valueOf(catStr); }
        catch (IllegalArgumentException e) { System.out.println("Invalid category.\n"); return; }

        System.out.print("Product name: ");
        String name = scan.nextLine().trim();
        System.out.println("Available sub-categories: " + Arrays.toString(SubCategoryType.values()));
        System.out.print("Select sub-category: ");
        String subStr = scan.nextLine().trim().toUpperCase();
        SubCategoryType subType;
        try { subType = SubCategoryType.valueOf(subStr); }
        catch (IllegalArgumentException e) { System.out.println("Invalid sub-category.\n"); return; }

        try {
            System.out.print("Cost Price: ");
            double cost  = Double.parseDouble(scan.nextLine().trim());
            System.out.print("Retail Price: ");
            double price = Double.parseDouble(scan.nextLine().trim());
            System.out.print("Quantity: ");
            int qty      = Integer.parseInt(scan.nextLine().trim());

            Product p;
            switch (catType) {
                case Engine:       p = new Engine(name, subType, cost, price, qty, null); break;
                case Transmission: p = new Transmission(name, subType, cost, price, qty, null); break;
                case Suspension:   p = new Suspension(name, subType, cost, price, qty, null); break;
                case Brakes:       p = new Brakes(name, subType, cost, price, qty, null); break;
                case Electrical:   p = new Electrical(name, subType, cost, price, qty, null); break;
                case Cooling:      p = new Cooling(name, subType, cost, price, qty, null); break;
                case Exhaust:      p = new Exhaust(name, subType, cost, price, qty, null); break;
                case Fuel:         p = new Fuel(name, subType, cost, price, qty, null); break;
                case Body:         p = new Body(name, subType, cost, price, qty, null); break;
                default:           throw new IllegalStateException("Unhandled category: " + catType);
            }
            inv.addProduct(p);
            System.out.println("✔ Product added: " + p.getCategoryType() + " – " + p.getName() + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number input.\n");
        }
    }

    private static void restockProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name to restock: ");
        String name = scan.nextLine().trim();
        Product p = inv.searchByName(name);
        if (p == null) { System.out.println("No such product.\n"); return; }
        try {
            System.out.print("Quantity to add: ");
            int qty = Integer.parseInt(scan.nextLine().trim());
            p.increaseStock(qty);
            System.out.println("Restocked. New quantity: " + p.getQuantity() + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.\n");
        }
    }

    private static void searchByName(Inventory inv, Scanner scan) {
        System.out.print("Enter Name: ");
        Product p = inv.searchByName(scan.nextLine().trim());
        if (p != null) p.printDetails(); else System.out.println("No product found.\n");
    }

    private static void searchByCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Category: ");
        inv.searchByCategory(scan.nextLine().trim());
        System.out.println();
    }

    private static void searchBySubCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Sub-category: ");
        inv.searchBySubCategory(scan.nextLine().trim());
        System.out.println();
    }

    private static void sellProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name: ");
        String prodName = scan.nextLine().trim();
        Product prod = inv.searchByName(prodName);
        if (prod == null) { System.out.println("No such product.\n"); return; }
        try {
            System.out.print("Quantity to sell: ");
            int qty = Integer.parseInt(scan.nextLine().trim());
            if (qty > prod.getQuantity()) {
                System.out.println("Not enough stock.\n");
                return;
            }
            prod.decreaseStock(qty);
            OrderItem item = new OrderItem(prod.getName(), qty, prod.getRetailPrice());
            orders.add(new Order(Collections.singletonList(item)));
            System.out.println("Sold " + qty + " x " + prod.getName() + ".\n");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.\n");
        }
    }

    private static void addUser(Scanner scan) {
        System.out.print("New username: ");
        String username = scan.nextLine().trim();
        if (username.isEmpty()) return;
        for (User u : users) if (u.getUsername().equals(username)) {
            System.out.println("User already exists.\n"); return; }
        System.out.print("New password: ");
        String password = scan.nextLine().trim();
        String role;
        while (true) {
            System.out.print("Role ('admin' or 'standard'): ");
            role = scan.nextLine().trim();
            if ("admin".equals(role) || "standard".equals(role)) break;
            System.out.println("Invalid role, try again.");
        }
        users.add(new User(username, password, role));
        System.out.println("User added: " + username + " (" + role + ")\n");
    }

    private static void deleteUser(Scanner scan, User currentUser) {
        System.out.print("Username to delete: ");
        String username = scan.nextLine().trim();
        if (username.equals(currentUser.getUsername())) {
            System.out.println("Cannot delete yourself.\n"); return;
        }
        for (Iterator<User> it = users.iterator(); it.hasNext(); ) {
            User u = it.next();
            if (u.getUsername().equals(username)) {
                it.remove();
                System.out.println("User deleted: " + username + "\n");
                return;
            }
        }
        System.out.println("No such user.\n");
    }

    private static void listUsers() {
        System.out.println("\nCurrent users:");
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " (" + u.getRole() + ")");
        }
        System.out.println();
    }
}
