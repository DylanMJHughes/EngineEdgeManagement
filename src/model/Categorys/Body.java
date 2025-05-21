package model.Categorys;

import model.CategoryType;
import model.Product;
import model.SubCategoryType;

public class Body extends Product {
    private final SubCategoryType subCategoryType;

    public Body(String name,
                SubCategoryType subCategoryType,
                double costPrice,
                double retailPrice,
                int quantity,
                String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
        this.subCategoryType = subCategoryType;
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.Body;
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return subCategoryType;
    }
}

