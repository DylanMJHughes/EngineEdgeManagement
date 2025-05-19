package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class InventoryApp extends Application {
    private Stage primaryStage;
    private List<User> users = new ArrayList<>();
    private List<User> pendingUsers = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Inventory inventory = new Inventory();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        InventoryFileHandler.loadInventory(inventory);
        users.add(new User("admin", "admin", "admin"));
        users.add(new User("user", "user", "standard"));
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(buildLoginScene());
        primaryStage.show();
    }

    private Scene buildLoginScene() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding:20; -fx-background-color:#2b2b2b;");

        Label title = new Label("Login");
        title.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setStyle("-fx-control-inner-background:#3c3f41; -fx-text-fill:white;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle("-fx-control-inner-background:#3c3f41; -fx-text-fill:white;");

        Label status = new Label();
        status.setStyle("-fx-text-fill:white;");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color:#555; -fx-text-fill:white;");
        loginBtn.setOnAction(e -> handleLogin(userField.getText().trim(), passField.getText().trim(), status));

        Button regBtn = new Button("Register");
        regBtn.setStyle("-fx-background-color:#555; -fx-text-fill:white;");
        regBtn.setOnAction(e -> handleRegister());

        HBox btnRow = new HBox(10, loginBtn, regBtn);
        btnRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, userField, passField, btnRow, status);
        Scene scene = new Scene(root, 320, 300);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        return scene;
    }

    private void handleLogin(String u, String p, Label status) {
        status.setText("");
        User current = null;
        for (User usr : users) {
            if (usr.getUsername().equals(u) && usr.getPassword().equals(p)) {
                current = usr;
                break;
            }
        }
        if (current == null) {
            status.setText("❌ Invalid credentials");
        } else {
            status.setText("✅ Welcome, " + current.getUsername());
            if ("admin".equals(current.getRole())) {
                primaryStage.setScene(buildAdminScene(current));
            } else {
                primaryStage.setScene(buildUserScene(current));
            }
        }
    }

    private void handleRegister() {
        TextInputDialog dlgU = new TextInputDialog(); styleDialog(dlgU);
        dlgU.setHeaderText("Choose a username:"); dlgU.showAndWait();
        String nu = dlgU.getEditor().getText().trim(); if (nu.isEmpty()) return;
        for (User ex : users) if (ex.getUsername().equals(nu)) { alert("❌ Username already taken."); return; }
        for (User ex : pendingUsers) if (ex.getUsername().equals(nu)) { alert("❌ Registration pending."); return; }
        TextInputDialog dlgP = new TextInputDialog(); styleDialog(dlgP);
        dlgP.setHeaderText("Choose a password:"); dlgP.showAndWait();
        String np = dlgP.getEditor().getText().trim(); if (np.isEmpty()) return;
        pendingUsers.add(new User(nu, np, "standard"));
        alert("✔ Registration submitted. Awaiting admin approval.");
    }

    private Scene buildUserScene(User user) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding:20; -fx-background-color:#2b2b2b;");

        Label title = new Label("User Menu: " + user.getUsername());
        title.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        Button[] buttons = new Button[]{
                createButton("Sell Product", e -> sellProduct()),
                createButton("Restock Product", e -> restockProduct()),
                createButton("Show Inventory", e -> showInventoryTable()),
                createButton("Search by Name", e -> promptAndSearchName()),
                createButton("Search by Category", e -> promptAndSearchCategory()),
                createButton("Search by Sub-category", e -> promptAndSearchSub()),
                createButton("Export Product Report", e -> exportSingleReport()),
                createButton("Export Full Inventory Report", e -> InventoryFileHandler.exportFullInventoryReport(inventory)),
                createButton("Log out", e -> primaryStage.setScene(buildLoginScene()))
        };

        root.getChildren().add(title);
        root.getChildren().addAll(buttons);
        Scene scene = new Scene(root, 360, 500);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        return scene;
    }

    private Scene buildAdminScene(User user) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding:20; -fx-background-color:#2b2b2b;");

        Label title = new Label("Admin Menu: " + user.getUsername());
        title.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        Button[] buttons = new Button[]{
                createButton("Add Product", e -> addProduct()),
                createButton("Restock Product", e -> restockProduct()),
                createButton("Show Inventory", e -> showInventoryTable()),
                createButton("Sell Product", e -> sellProduct()),
                createButton("Search by Name", e -> promptAndSearchName()),
                createButton("Search by Category", e -> promptAndSearchCategory()),
                createButton("Search by Sub-category", e -> promptAndSearchSub()),
                createButton("Export Product Report", e -> exportSingleReport()),
                createButton("Export Full Inventory Report", e -> InventoryFileHandler.exportFullInventoryReport(inventory)),
                createButton("Settings", e -> showSettings()),
                createButton("Log out", e -> primaryStage.setScene(buildLoginScene()))
        };

        root.getChildren().add(title);
        root.getChildren().addAll(buttons);
        Scene scene = new Scene(root, 360, 600);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        return scene;
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button b = new Button(text);
        b.setOnAction(handler);
        b.setStyle("-fx-background-color:#555; -fx-text-fill:white; -fx-min-width:200px;");
        return b;
    }

    private void styleDialog(Dialog<?> dlg) {
        dlg.getDialogPane().getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
    }

    private void promptAndSearchName() {
        String n = prompt("Enter product name:");
        Product p = inventory.searchByName(n);
        alert(p != null ? formatProduct(p) : "No product found.");
    }

    private void promptAndSearchCategory() {
        String c = prompt("Enter category:");
        inventory.searchByCategory(c);
    }

    private void promptAndSearchSub() {
        String s = prompt("Enter sub-category:");
        inventory.searchBySubCategory(s);
    }

    private void exportSingleReport() {
        String n = prompt("Enter product for report:");
        InventoryFileHandler.exportProductReport(inventory, n);
    }

    private void showSettings() {
        if (pendingUsers.isEmpty()) {
            alert("No pending registrations.");
            return;
        }
        StringBuilder sb = new StringBuilder("Pending registrations:\n");
        pendingUsers.forEach(u -> sb.append(u.getUsername()).append("\n"));
        alert(sb.toString());
    }

    private void addProduct() {
        String name = prompt("Product name:");
        String cat = prompt("Category:");
        String sub = prompt("Sub-category:");
        double cost = Double.parseDouble(prompt("Cost Price:"));
        double price = Double.parseDouble(prompt("Retail Price:"));
        int qty = Integer.parseInt(prompt("Quantity:"));
        inventory.addProduct(new Product(name, cat, sub, cost, price, qty));
        alert("Product added successfully!");
    }

    private void restockProduct() {
        String name = prompt("Product to restock:");
        Product p = inventory.searchByName(name);
        if (p == null) { alert("No such product."); return; }
        int amt = Integer.parseInt(prompt("Quantity to add:"));
        p.increaseStock(amt);
        alert("Restocked. New Qty: " + p.getQuantity());
    }

    private void sellProduct() {
        // 1) Gather current products
        List<Product> products = inventory.getProducts();
        if (products.isEmpty()) {
            alert("No products available.");
            return;
        }
        List<String> names = products.stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        // 2) Show ChoiceDialog to pick a product
        ChoiceDialog<String> choice = new ChoiceDialog<>(names.get(0), names);
        styleDialog(choice);
        choice.setTitle("Sell Product");
        choice.setHeaderText("Select product to sell:");
        choice.showAndWait().ifPresent(selectedName -> {
            Product p = inventory.searchByName(selectedName);
            if (p == null) {
                alert("Product not found.");
                return;
            }

            // 3) Prompt for quantity
            TextInputDialog qtyDlg = new TextInputDialog();
            styleDialog(qtyDlg);
            qtyDlg.setTitle("Quantity");
            qtyDlg.setHeaderText("Quantity to sell for \"" + p.getName() + "\":");
            qtyDlg.showAndWait().ifPresent(qtyText -> {
                int qty;
                try {
                    qty = Integer.parseInt(qtyText.trim());
                } catch (NumberFormatException ex) {
                    alert("Please enter a valid number.");
                    return;
                }
                if (qty <= 0) {
                    alert("Quantity must be positive.");
                } else if (qty > p.getQuantity()) {
                    alert("Not enough stock.");
                } else {
                    // 4) Perform the sale
                    p.decreaseStock(qty);
                    OrderItem item = new OrderItem(p.getName(), qty, p.getRetailPrice());
                    Order ord = new Order(Collections.singletonList(item));
                    orders.add(ord);

                    alert(String.format(
                            "Sold %d x %s\nOrder Total: %.2f",
                            qty, p.getName(), ord.getTotal()
                    ));
                }
            });
        });
    }

    private void showInventoryTable() {
        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Product, String> subCol = new TableColumn<>("Sub-category");
        subCol.setCellValueFactory(new PropertyValueFactory<>("subCategory"));
        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Product, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));

        table.getColumns().setAll(nameCol, catCol, subCol, qtyCol, costCol, priceCol);
        table.setItems(FXCollections.observableArrayList(inventory.getProducts()));

        VBox box = new VBox(table);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #2b2b2b;");

        Scene scene = new Scene(box, 700, 400);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Inventory");
        dialog.setScene(scene);
        dialog.show();
    }

    private String formatProduct(Product p) {
        return String.format("%s | %s > %s | Qty:%d | Cost:%.2f | Price:%.2f",
                p.getName(), p.getCategory(), p.getSubCategory(),
                p.getQuantity(), p.getCostPrice(), p.getRetailPrice());
    }

    private String prompt(String msg) {
        TextInputDialog dlg = new TextInputDialog();
        styleDialog(dlg);
        dlg.setHeaderText(msg);
        dlg.showAndWait();
        return dlg.getEditor().getText().trim();
    }

    private void alert(String msg) {
        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(dlg);
        dlg.setHeaderText(null);
        dlg.setContentText(msg);
        dlg.showAndWait();
    }

    @Override
    public void stop() {
        InventoryFileHandler.saveInventory(inventory);
        OrderFileHandler.saveOrders(orders);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
