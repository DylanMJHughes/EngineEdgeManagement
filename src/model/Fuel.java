package model;

public class Fuel extends Product {
    public Fuel(String name,
                String subCategory,
                double costPrice,
                double retailPrice,
                int quantity,
                String imagePath) {
        super(name, subCategory, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.Fuel;
    }
}

