package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String dateTime;
    private List<OrderItem> items;
    private double total;

    public Order(List<OrderItem> items) {
        this.orderId  = UUID.randomUUID().toString();
        this.dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.items    = items;
        this.total    = calculateTotal();
    }

    private double calculateTotal() {
        double sum = 0;
        for (OrderItem it : items) {
            sum += it.getTotalPrice();
        }
        return sum;
    }

    public String           getOrderId()  { return orderId;  }
    public String           getDateTime() { return dateTime; }
    public List<OrderItem>  getItems()    { return items;    }
    public double           getTotal()    { return total;    }

    public void printOrder() {
        System.out.println("Order ID: " + orderId + " | Date: " + dateTime);
        for (OrderItem it : items) {
            System.out.printf("  - %s x%d @ %.2f = %.2f%n",
                    it.getProductName(),
                    it.getQuantity(),
                    it.getUnitPrice(),
                    it.getTotalPrice());
        }
        System.out.printf("Total: %.2f%n", total);
    }
}

