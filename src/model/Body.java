package model;

import model.CategoryType;

/**
 * Abstract mid‐layer for all “body” sub‐categories.
 */
public abstract class Body extends Product {
    public Body(String name,
                double costPrice,
                double retailPrice,
                int quantity,
                String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.Body;
    }

    // we leave getSubCategoryType() abstract here—
    // each concrete subclass will supply its own sub‐category enum.
}