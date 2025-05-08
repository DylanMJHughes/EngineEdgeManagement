package utils;

import model.Order;
import model.OrderItem;

import java.io.*;
import java.util.List;

public class OrderFileHandler {
    private static final String FILE_NAME = "orders.csv";

    /** Appends all of the orders to orders.csv */
    public static void saveOrders(List<Order> orders) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            for (Order order : orders) {
                for (OrderItem it : order.getItems()) {
                    // CSV fields: orderId,dateTime,productName,quantity,unitPrice
                    out.printf("%s,%s,%s,%d,%.2f%n",
                            order.getOrderId(),
                            order.getDateTime(),
                            it.getProductName(),
                            it.getQuantity(),
                            it.getUnitPrice()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }
}

