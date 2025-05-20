package model;

public class Electrical extends Product {
    public Electrical(String name,
                      String subCategory,
                      double costPrice,
                      double retailPrice,
                      int quantity,
                      String imagePath) {
        super(name, subCategory, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.Electrical;
    }
}

