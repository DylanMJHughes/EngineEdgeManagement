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
    //In-memory approved users
    private static List<User> users = new ArrayList<>();
    //In-memory pending registrations
    private static List<User> pendingUsers = new ArrayList<>();
    //In-memory orders
    private static List<Order> orders = new ArrayList<>();

    static {
        // input initial users
        users.add(new User("admin", "admin", "admin"));
        users.add(new User("user",  "user",  "standard"));
    }

    public static void main(String[] args) {
        Inventory inv = new Inventory();
        InventoryFileHandler.loadInventory(inv);
        orders = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        boolean running = true;

        // Top-level loop: Login/Register/Quit
        while (running) {
            User currentUser = null;
            while (currentUser == null && running) {
                System.out.println("1) Login");
                System.out.println("2) Register");
                System.out.println("3) Quit");
                System.out.print("> ");
                String mode = scan.nextLine();
                switch (mode) {
                    case "1":
                        currentUser = login(scan);
                        break;
                    case "2":
                        registerNewUser(scan);
                        break;
                    case "3":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.\n");
                }
            }
            if (!running) break;

            // User is logged in—show their menu
            if (currentUser.getRole().equals("admin")) {
                adminMenu(inv, scan, currentUser);
            } else {
                standardMenu(inv, scan);
            }
        }

        // Save on exit
        InventoryFileHandler.saveInventory(inv);
        OrderFileHandler.saveOrders(orders);
        scan.close();
        System.out.println("Inventory and orders saved. Goodbye!");
    }

    //Prompt for existing user login
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
                + " (" + currentUser.getRole() + ")\n");
        return currentUser;
    }

    //Self-service registration: adds user to pending list.
    private static void registerNewUser(Scanner scan) {
        System.out.print("Choose a username: ");
        String u = scan.nextLine();
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
        String p = scan.nextLine();
        pendingUsers.add(new User(u, p, "standard"));
        System.out.println("✔ Registration submitted. Wait for admin approval.\n");
    }

    //Admin menu with a Log-out option.
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
            String choice = scan.nextLine();
            switch (choice) {
                case "1": addProduct(inv, scan);                   break;
                case "2": restockProduct(inv, scan);               break;
                case "3": inv.showInventory();                     break;
                case "4": sellProduct(inv, scan);                  break;
                case "5": searchByName(inv, scan);                 break;
                case "6": searchByCategory(inv, scan);             break;
                case "7": searchBySubCategory(inv, scan);          break;
                case "8":
                    System.out.print("Enter product name: ");
                    InventoryFileHandler.exportProductReport(inv, scan.nextLine());
                    break;
                case "9":
                    InventoryFileHandler.exportFullInventoryReport(inv);
                    break;
                case "10":
                    settingsMenu(scan, me);
                    break;
                case "11":
                    System.out.println("Logging out...\n");
                    return;
                default:
                    System.out.println("Invalid choice, try again.\n");
            }
        }
    }

    //Standard user menu with a Log-out option.
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
            String choice = scan.nextLine();
            switch (choice) {
                case "1": sellProduct(inv, scan);                  break;
                case "2": restockProduct(inv, scan);               break;
                case "3": inv.showInventory();                     break;
                case "4": searchByName(inv, scan);                 break;
                case "5": searchByCategory(inv, scan);             break;
                case "6": searchBySubCategory(inv, scan);          break;
                case "7":
                    System.out.print("Enter product name: ");
                    InventoryFileHandler.exportProductReport(inv, scan.nextLine());
                    break;
                case "8":
                    InventoryFileHandler.exportFullInventoryReport(inv);
                    break;
                case "9":
                    System.out.println("Logging out...\n");
                    return;
                default:
                    System.out.println("Invalid choice, try again.\n");
            }
        }
    }

    //Settings menu for admin (includes approving registrations).
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
            String choice = scan.nextLine();
            switch (choice) {
                case "1": addUser(scan);                 break;
                case "2": deleteUser(scan, currentUser); break;
                case "3": listUsers();                   break;
                case "4": approveRegistrations(scan);    break;
                case "5": return;
                default:  System.out.println("Invalid choice.\n");
            }
        }
    }

    //Admin helper: approve pending registrations.
    private static void approveRegistrations(Scanner scan) {
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending registrations.\n");
            return;
        }
        while (true) {
            System.out.println("Pending registrations:");
            for (int i = 0; i < pendingUsers.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, pendingUsers.get(i).getUsername());
            }
            System.out.println("A) Approve by number   B) Back");
            System.out.print("> ");
            String inp = scan.nextLine();
            if (inp.equalsIgnoreCase("B")) {
                System.out.println();
                return;
            }
            try {
                int idx = Integer.parseInt(inp) - 1;
                if (idx < 0 || idx >= pendingUsers.size()) {
                    System.out.println("Invalid number.\n");
                } else {
                    User approved = pendingUsers.remove(idx);
                    users.add(approved);
                    System.out.println(" Approved: " + approved.getUsername() + "\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.\n");
            }
        }
    }

    //Existing Helper Methods

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

        System.out.print("Image path (file://... or local absolute path, leave blank for none): ");
        String imgPath = scan.nextLine().trim();
        // pass null if user skipped
        Product p = new Product(name, cat, sub, cost, price, qty, imgPath.isEmpty() ? null : imgPath);
        inv.addProduct(p);
        System.out.println(" Product added!\n");
    }

    private static void restockProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name to restock: ");
        String name = scan.nextLine();
        Product p = inv.searchByName(name);
        if (p == null) {
            System.out.println("No such product.\n");
            return;
        }
        System.out.print("Quantity to add: ");
        int qty;
        try {
            qty = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.\n");
            return;
        }
        p.increaseStock(qty);
        System.out.println("Restocked. New quantity: " + p.getQuantity() + "\n");
    }

    private static void searchByName(Inventory inv, Scanner scan) {
        System.out.print("Enter Name: ");
        Product p = inv.searchByName(scan.nextLine());
        if (p != null) p.printDetails();
        else           System.out.println("No product found by that name.\n");
    }

    private static void searchByCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Category: ");
        inv.searchByCategory(scan.nextLine());
        System.out.println();
    }

    private static void searchBySubCategory(Inventory inv, Scanner scan) {
        System.out.print("Enter Sub-category: ");
        inv.searchBySubCategory(scan.nextLine());
        System.out.println();
    }

    private static void sellProduct(Inventory inv, Scanner scan) {
        System.out.print("Product name: ");
        String prodName = scan.nextLine();
        Product prod = inv.searchByName(prodName);
        if (prod == null) {
            System.out.println("No such product.\n");
            return;
        }
        System.out.print("Quantity to sell: ");
        int qty = Integer.parseInt(scan.nextLine());
        if (qty > prod.getQuantity()) {
            System.out.println("Not enough stock.\n");
            return;
        }
        prod.decreaseStock(qty);
        OrderItem item = new OrderItem(prod.getName(), qty, prod.getRetailPrice());
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        Order order = new Order(items);
        orders.add(order);
        System.out.println("Product sold:");
        order.printOrder();
        System.out.println();
    }

    private static void addUser(Scanner scan) {
        System.out.print("New username: ");
        String username = scan.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("User already exists.\n");
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
        System.out.println("User added: " + username + " (" + role + ")\n");
    }

    private static void deleteUser(Scanner scan, User currentUser) {
        System.out.print("Username to delete: ");
        String username = scan.nextLine();
        if (username.equals(currentUser.getUsername())) {
            System.out.println("Cannot delete yourself.\n");
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
            System.out.println("No such user.\n");
        } else {
            users.remove(toRemove);
            System.out.println("User deleted: " + username + "\n");
        }
    }

    private static void listUsers() {
        System.out.println("\nCurrent users:");
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " (" + u.getRole() + ")");
        }
        System.out.println();
    }
}
