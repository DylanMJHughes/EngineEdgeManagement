package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.InventoryFileHandler;
import javafx.stage.FileChooser;
import java.io.File;
import model.*;
import model.Categorys.*;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Optional;

import model.CategoryType;
import model.Categorys.Transmission;
import model.Categorys.Suspension;
import model.SubCategoryType;
import javafx.scene.control.ChoiceDialog;
import javafx.beans.property.SimpleStringProperty;


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
            status.setText("Invalid credentials");
        } else {
            status.setText("Welcome, " + current.getUsername());
            if ("admin".equals(current.getRole())) {
                primaryStage.setScene(buildAdminScene(current));
            } else {
                primaryStage.setScene(buildUserScene(current));
            }
        }
    }

    private void handleRegister() {
        TextInputDialog dlgU = new TextInputDialog();
        styleDialog(dlgU);
        dlgU.setHeaderText("Choose a username:");
        Optional<String> uOpt = dlgU.showAndWait();
        if (!uOpt.isPresent()) return;
        String nu = uOpt.get().trim();
        if (nu.isEmpty()) return;
        for (User ex : users) if (ex.getUsername().equals(nu)) { alert("Username already taken."); return; }
        for (User ex : pendingUsers) if (ex.getUsername().equals(nu)) { alert("Registration pending."); return; }
        TextInputDialog dlgP = new TextInputDialog();
        styleDialog(dlgP);
        dlgP.setHeaderText("Choose a password:");
        Optional<String> pOpt = dlgP.showAndWait();
        if (!pOpt.isPresent()) return;
        String np = pOpt.get().trim();
        if (np.isEmpty()) return;
        pendingUsers.add(new User(nu, np, "standard"));
        alert("✔ Registration submitted. Awaiting admin approval.");
    }

    private Scene buildUserScene(User user) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding:20; -fx-background-color:#2b2b2b;");

        Label title = new Label("User Menu: " + user.getUsername());
        title.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        Button[] buttons = {
                createButton("Sell Product",                e -> sellProduct()),
                createButton("Restock Product",             e -> restockProduct()),
                createButton("Show Inventory",              e -> showInventoryTable()),
                createButton("Search by Name",              e -> promptAndSearchName()),
                createButton("Search by Category",          e -> promptAndSearchCategory()),
                createButton("Search by Sub-category",      e -> promptAndSearchSub()),

                // inline‐prompt + static call to exportProductReport:
                createButton("Export Product Report",       e -> {
                    String prod = prompt("Enter product for report:");
                    if (!prod.isEmpty()) {
                        InventoryFileHandler.exportProductReport(inventory, prod);
                    }
                }),

                // direct static call to exportFullInventoryReport:
                createButton("Export Full Inventory Report",e -> InventoryFileHandler.exportFullInventoryReport(inventory)),

                createButton("Log out",                     e -> primaryStage.setScene(buildLoginScene()))
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

        Button[] buttons = {
                createButton("Add Product", e -> addProduct()),
                createButton("Restock Product", e -> restockProduct()),
                createButton("Show Inventory", e -> showInventoryTable()),
                createButton("Sell Product", e -> sellProduct()),
                createButton("Search by Name", e -> promptAndSearchName()),
                createButton("Search by Category", e -> promptAndSearchCategory()),
                createButton("Search by Sub-category", e -> promptAndSearchSub()),
                createButton("Export Product Report", e -> exportSingleReport()),
                createButton("Export Full Inventory Report", e -> exportFullInventoryWithPopup()),
                createButton("Settings", e -> primaryStage.setScene(buildSettingsScene(user))),
                createButton("Log out", e -> primaryStage.setScene(buildLoginScene()))
        };

        root.getChildren().add(title);
        root.getChildren().addAll(buttons);
        Scene scene = new Scene(root, 360, 600);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        return scene;
    }

    private void exportFullInventoryWithPopup() {
        InventoryFileHandler.exportFullInventoryReport(inventory);
        // Show confirmation popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(alert); 
        alert.setTitle("Export Complete");
        alert.setHeaderText(null);
        alert.setContentText("Full inventory report exported to\nfull_inventory_report.csv");
        alert.showAndWait();
    }

    private Scene buildSettingsScene(User admin) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding:20; -fx-background-color:#2b2b2b;");

        Label title = new Label("Admin Settings");
        title.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        // ListView of pending registrations
        ListView<String> pendingView = new ListView<>();
        pendingView.setItems(FXCollections.observableArrayList(
                pendingUsers.stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList())
        ));
        pendingView.setPrefHeight(200);

        // Approve button
        Button approveBtn = createButton("Approve", e -> {
            String sel = pendingView.getSelectionModel().getSelectedItem();
            if (sel == null) {
                alert("Select a user to approve.");
                return;
            }
            pendingUsers.stream()
                    .filter(u -> u.getUsername().equals(sel))
                    .findFirst()
                    .ifPresent(u -> {
                        pendingUsers.remove(u);
                        users.add(u);
                        alert("Approved: " + u.getUsername());
                        pendingView.getItems().remove(sel);
                    });
        });

        // Disapprove button
        Button disapproveBtn = createButton("Disapprove", e -> {
            String sel = pendingView.getSelectionModel().getSelectedItem();
            if (sel == null) {
                alert("Select a user to disapprove.");
                return;
            }
            pendingUsers.stream()
                    .filter(u -> u.getUsername().equals(sel))
                    .findFirst()
                    .ifPresent(u -> {
                        pendingUsers.remove(u);
                        alert("Disapproved: " + u.getUsername());
                        pendingView.getItems().remove(sel);
                    });
        });

        // List all users
        Button listUsersBtn = createButton("List Users", e -> {
            String all = users.stream()
                    .map(User::getUsername)
                    .collect(Collectors.joining("\n"));
            alert("Current Users:\n" + (all.isEmpty() ? "(none)" : all));
        });

        // Add a new user
        Button addUserBtn = createButton("Add User", e -> {
            TextInputDialog uDlg = new TextInputDialog();
            styleDialog(uDlg);
            uDlg.setHeaderText("Enter new username:");
            Optional<String> uOpt = uDlg.showAndWait();
            if (!uOpt.isPresent() || uOpt.get().trim().isEmpty()) return;

            TextInputDialog pDlg = new TextInputDialog();
            styleDialog(pDlg);
            pDlg.setHeaderText("Enter new password:");
            Optional<String> pOpt = pDlg.showAndWait();
            if (!pOpt.isPresent() || pOpt.get().trim().isEmpty()) return;

            users.add(new User(uOpt.get().trim(), pOpt.get().trim(), "standard"));
            alert("User added: " + uOpt.get().trim());
        });

        // Delete an existing user
        Button deleteUserBtn = createButton("Delete User", e -> {
            TextInputDialog dDlg = new TextInputDialog();
            styleDialog(dDlg);
            dDlg.setHeaderText("Enter username to delete:");
            Optional<String> dOpt = dDlg.showAndWait();
            if (!dOpt.isPresent()) return;

            String name = dOpt.get().trim();
            Optional<User> toDel = users.stream()
                    .filter(u -> u.getUsername().equals(name))
                    .findFirst();
            if (toDel.isPresent()) {
                users.remove(toDel.get());
                alert("Deleted user: " + name);
            } else {
                alert("User not found: " + name);
            }
        });

        // Back button
        Button backBtn = createButton("Back", e ->
                primaryStage.setScene(buildAdminScene(admin))
        );

        // Layout all buttons in a couple of HBoxes
        HBox row1 = new HBox(10, approveBtn, disapproveBtn);
        VBox row2 = new VBox(10, listUsersBtn, addUserBtn, deleteUserBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                title,
                new Label("Pending Registrations:") {{ setStyle("-fx-text-fill:white;"); }},
                pendingView,
                row1,
                row2,
                backBtn
        );

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(
                getClass().getResource("dark-theme.css").toExternalForm()
        );
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

    private void addProduct() {
        // 1) Let user pick one of your enum categories
        ChoiceDialog<CategoryType> catDlg = new ChoiceDialog<>(
                CategoryType.Engine,
                CategoryType.values()
        );
        catDlg.setHeaderText("Select Category:");
        catDlg.initOwner(primaryStage);
        styleDialog(catDlg);
        Optional<CategoryType> catOpt = catDlg.showAndWait();
        if (catOpt.isEmpty()) return;      // user cancelled
        CategoryType catType = catOpt.get();

        // 2) Let user pick one of your enum sub-categories
        ChoiceDialog<SubCategoryType> subDlg = new ChoiceDialog<>(
                SubCategoryType.values()[0],
                SubCategoryType.values()
        );
        subDlg.setHeaderText("Select Sub-category:");
        subDlg.initOwner(primaryStage);
        styleDialog(subDlg);
        Optional<SubCategoryType> subOpt = subDlg.showAndWait();
        if (subOpt.isEmpty()) return;      // user cancelled
        SubCategoryType subType = subOpt.get();

        // 3) Gather the rest of the info
        String name  = prompt("Product name:");
        double cost  = Double.parseDouble(prompt("Cost Price:"));
        double price = Double.parseDouble(prompt("Retail Price:"));
        int qty      = Integer.parseInt(prompt("Quantity:"));

        // 4) Let user pick an image file (optional)
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Product Image (optional)");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = chooser.showOpenDialog(primaryStage);
        String imgPath = (file != null) ? file.toURI().toString() : null;

        // 5) Instantiate the right subclass using the enum subType
        Product p;
        switch (catType) {
            case Engine:
                p = new Engine(name, subType, cost, price, qty, imgPath);
                break;
            case Transmission:
                p = new Transmission(name, subType, cost, price, qty, imgPath);
                break;
            case Suspension:
                p = new Suspension(name, subType, cost, price, qty, imgPath);
                break;
            case Brakes:
                p = new Brakes(name, subType, cost, price, qty, imgPath);
                break;
            case Electrical:
                p = new Electrical(name, subType, cost, price, qty, imgPath);
                break;
            case Cooling:
                p = new Cooling(name, subType, cost, price, qty, imgPath);
                break;
            case Exhaust:
                p = new Exhaust(name, subType, cost, price, qty, imgPath);
                break;
            case Fuel:
                p = new Fuel(name, subType, cost, price, qty, imgPath);
                break;
            case Body:
                p = new Body(name, subType, cost, price, qty, imgPath);
                break;
            default:
                throw new IllegalStateException("Unhandled category: " + catType);
        }

        // 6) Add and notify
        inventory.addProduct(p);
        alert("✔ Product added: " + catType + " – " + name);
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
        List<Product> products = inventory.getProducts();
        if (products.isEmpty()) { alert("No products available."); return; }
        List<String> names = products.stream().map(Product::getName).collect(Collectors.toList());
        ChoiceDialog<String> choice = new ChoiceDialog<>(names.get(0), names);
        styleDialog(choice);
        choice.setTitle("Sell Product");
        choice.setHeaderText("Select product to sell:");
        choice.showAndWait().ifPresent(selectedName -> {
            Product p = inventory.searchByName(selectedName);
            if (p == null) { alert("Product not found."); return; }
            TextInputDialog qtyDlg = new TextInputDialog();
            styleDialog(qtyDlg);
            qtyDlg.setTitle("Quantity");
            qtyDlg.setHeaderText("Quantity to sell for \"" + p.getName() + "\":");
            qtyDlg.showAndWait().ifPresent(qtyText -> {
                try {
                    int qty = Integer.parseInt(qtyText.trim());
                    if (qty <= 0) alert("Quantity must be positive.");
                    else if (qty > p.getQuantity()) alert("Not enough stock.");
                    else {
                        p.decreaseStock(qty);
                        OrderItem item = new OrderItem(p.getName(), qty, p.getRetailPrice());
                        Order ord = new Order(Collections.singletonList(item));
                        orders.add(ord);
                        alert(String.format("Sold %d x %s\nOrder Total: %.2f", qty, p.getName(), ord.getTotal()));
                    }
                } catch (NumberFormatException ex) {
                    alert("Please enter a valid number.");
                }
            });
        });
    }

    private void showInventoryTable() {
        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product,String> imgCol = new TableColumn<>("Image");
        imgCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imgCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView view = new ImageView();
            {
                view.setFitWidth(50);
                view.setFitHeight(50);
                view.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        view.setImage(new Image(path, 50, 50, true, true));
                        setGraphic(view);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCategoryType().name()
                )
        );




        //Sub category column
        TableColumn<Product, String> subCol = new TableColumn<>("Sub-category");
        subCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getSubCategoryType()
                                .name()
                )
        );

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("costPrice"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));

        table.getColumns().setAll(imgCol, nameCol, catCol, subCol, qtyCol, costCol, priceCol);
        table.setItems(FXCollections.observableArrayList(inventory.getProducts()));

        VBox box = new VBox(table);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#2b2b2b;");
        Scene scene = new Scene(box, 700, 400);
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Inventory");
        dialog.setScene(scene);
        dialog.show();
    }

    private String prompt(String msg) {
        TextInputDialog dlg = new TextInputDialog();
        styleDialog(dlg);
        dlg.setHeaderText(msg);
        Optional<String> res = dlg.showAndWait();
        return res.orElse("").trim();
    }

    private String formatProduct(Product p) {
        return String.format(
                "Name: %s%n" +
                        "Category: %s%n" +
                        "Sub-category: %s%n" +
                        "Cost Price: %.2f%n" +
                        "Retail Price: %.2f%n" +
                        "Quantity: %d",
                p.getName(),
                p.getCategoryType(),
                p.getSubCategoryType(),
                p.getCostPrice(),
                p.getRetailPrice(),
                p.getQuantity()
        );
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
