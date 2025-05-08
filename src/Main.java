import model.Inventory;
import model.Product;
import model.User;
import model.Order;
import model.OrderItem;
import utils.InventoryFileHandler;
import utils.OrderFileHandler;


import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    /** In-memory users **/
    private static List<User> users = new ArrayList<>();
    // In-memory orders
    private static List<Order> orders = new ArrayList<>();

    static {
        users.add(new User("admin","admin","admin"));
        users.add(new User("user","user","standard"));
    }

    public static void main(String[] args) {
        Inventory inv = new Inventory();
        InventoryFileHandler.loadInventory(inv);
        // initialize orders list
        orders = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        User currentUser = login(scan);

        // Branch into the main loop
        if (currentUser.getRole().equals("admin")) {
            adminMenu(inv, scan, currentUser);
        } else {
            standardMenu(inv, scan);
        }

        // exit, save the inventory and orders

        InventoryFileHandler.saveInventory(inv);
        OrderFileHandler.saveOrders(orders);
        scan.close();
        System.out.println("Inventory and orders saved. Goodbye!");
    }

    private static User login(Scanner scan) {
        User currentUser = null;
        while (currentUser == null) {
            System.out.print("Username: ");
            String u = scan.nextLine();
            System.out.print("Password: ");
            String p = scan.nextLine();
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
        System.out.println("✔ Welcome "
                + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        return currentUser;
    }
// Admin Menu
    private static void adminMenu(Inventory inv, Scanner scan, User me) {
        while (true) {
            System.out.println(
                    "\n1. Add Product\n" +
                            "2. Restock Product\n" +
                            "3. Show Inventory\n" +
                            "4. Sell Product\n" +
                            "5. Search by Name\n" +
                            "6. Search by Category\n" +
                            "7. Search by Sub-category\n" +
                            "8. Export Product Report\n" +
                            "9. Settings\n" +
                            "10. Exit"
            );
            String choice = scan.nextLine();
            switch (choice) {
                case "1": addProduct(inv, scan); break;
                case "2": restockProduct(inv, scan); break;
                case "3": inv.showInventory(); break;
                case "4": sellProduct(inv, scan); break;
                case "5": searchByName(inv, scan); break;
                case "6": searchByCategory(inv, scan); break;
                case "7": searchBySubCategory(inv, scan); break;
                case "8":                         // ← new case
                    System.out.print("Enter product name: ");
                    String prodToReport = scan.nextLine();
                    InventoryFileHandler.exportProductReport(inv, prodToReport);
                    break;
                case "9": settingsMenu(scan, me); break;
                case "10": return;
                default:  System.out.println("Invalid choice, try again.");
            }
            }
        }
    }

// Standard menu

    private static void standardMenu(Inventory inv, Scanner scan) {
        while (true) {
            System.out.println(
                    "\n1. Sell Product\n" +
                            "2. Restock Product\n" +
                            "3. Show Inventory\n" +
                            "4. Search by Name\n" +
                            "5. Search by Category\n" +
                            "6. Search by Sub-category\n" +
                            "7. Export Product Report\n" +  // ← new
                            "8. Exit"
            );
            String choice = scan.nextLine();
            switch (choice) {
                case "1": sellProduct(inv, scan); break;
                case "2": restockProduct(inv, scan); break;
                case "3": inv.showInventory(); break;
                case "4": searchByName(inv, scan); break;
                case "5": searchByCategory(inv, scan); break;
                case "6": searchBySubCategory(inv, scan); break;
                case "7":                         // ← new case
                    System.out.print("Enter product name: ");
                    String prodToReport = scan.nextLine();
                    InventoryFileHandler.exportProductReport(inv, prodToReport);
                    break;
                case "8": return;
                default:  System.out.println("Invalid choice, try again.");
            }
        }
    }


    // helper methods

    private static void addProduct(Inventory inv, Scanner scan) {
        System.out.print("Name: ");
        String name = scan.nextLine();
        System.out.print("Category: ");
        String cat = scan.nextLine();
        System.out.print("Sub-category: ");
        String sub = scan.nextLine();
        System.out.print("Cost Price: ");
        double cost = Double.parseDouble(scan.nextLine());
        System.out.print("Retail Price: ");
        double price = Double.parseDouble(scan.nextLine());
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(scan.nextLine());

        inv.addProduct(new Product(name, cat, sub, cost, price, qty));
        System.out.println("→ Product added!");
    }

    private static void restockProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name to restock: ");
        String name = scan.nextLine();
        Product p = inv.searchByName(name);
        if (p == null) {
            System.out.println("✖ No such product.");
            return;
        }

        System.out.print("Quantity to add: ");
        int qty;
        try {
            qty = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("✖ Invalid number.");
            return;
        }

        p.increaseStock(qty);
        System.out.println("✔ Restocked. New quantity: " + p.getQuantity());
    }

    // Search Products
    private static void searchByName(Inventory inv, Scanner scan) {
        System.out.print("Enter Name: ");
        Product p = inv.searchByName(scan.nextLine());
        if (p != null) p.printDetails();
        else            System.out.println("No product found by that name.");
    }

    private static void searchByCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Category: ");
        inv.searchByCategory(scan.nextLine());
    }

    private static void searchBySubCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Sub-category: ");
        inv.searchBySubCategory(scan.nextLine());
    }

    /** Sell Product / Order Processing **/

    private static void sellProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name: ");
        String prodName = scan.nextLine();
        Product prod = inv.searchByName(prodName);
        if (prod == null) {
            System.out.println("✖ No such product.");
            return;
        }

        System.out.print("Quantity to sell: ");
        int qty = Integer.parseInt(scan.nextLine());
        if (qty > prod.getQuantity()) {
            System.out.println("✖ Not enough stock.");
            return;
        }

        prod.decreaseStock(qty);

        OrderItem item = new OrderItem(prod.getName(), qty, prod.getRetailPrice());
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        Order order = new Order(items);
        orders.add(order);

        System.out.println("✔ Product sold:");
        order.printOrder();
    }

    /** Settings menu & user management (admin only) **/

    private static void settingsMenu(Scanner scan, User currentUser) {
        while (true) {
            System.out.println(
                    "\n== Settings ==" +
                            "\n1. Add User" +
                            "\n2. Delete User" +
                            "\n3. List Users" +
                            "\n4. Back to Main Menu"
            );
            String choice = scan.nextLine();
            switch (choice) {
                case "1":
                    addUser(scan);
                    break;
                case "2":
                    deleteUser(scan, currentUser);
                    break;
                case "3":
                    listUsers();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addUser(Scanner scan) {
        System.out.print("New username: ");
        String username = scan.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("✖ User already exists.");
                return;
            }
        }
        System.out.print("New password: ");
        String password = scan.nextLine();
        String role;
        while (true) {
            System.out.print("Role ('admin' or 'standard'): ");
            role = scan.nextLine();
            if (role.equals("admin") || role.equals("standard")) break;
            System.out.println("Invalid role, try again.");
        }
        users.add(new User(username, password, role));
        System.out.println("✔ User added: " + username + " (" + role + ")");
    }

    private static void deleteUser(Scanner scan, User currentUser) {
        System.out.print("Username to delete: ");
        String username = scan.nextLine();
        if (username.equals(currentUser.getUsername())) {
            System.out.println("✖ Cannot delete yourself.");
            return;
        }
        User toRemove = null;
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                toRemove = u;
                break;
            }
        }
        if (toRemove == null) {
            System.out.println("✖ No such user.");
        } else {
            users.remove(toRemove);
            System.out.println("✔ User deleted: " + username);
        }
    }

    private static void listUsers() {
        System.out.println("\nCurrent users:");
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " (" + u.getRole() + ")");
        }
    }
}

