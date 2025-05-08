package model;

public class OrderItem {
    private String productName;
    private int quantity;
    private double unitPrice;

    public OrderItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
    }

    public String getProductName() { return productName; }
    public int    getQuantity()    { return quantity;    }
    public double getUnitPrice()   { return unitPrice;   }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}
