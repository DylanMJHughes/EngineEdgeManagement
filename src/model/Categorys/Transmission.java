package model.Categorys;

import model.CategoryType;
import model.Product;
import model.SubCategoryType;

public class Transmission extends Product {
    private final SubCategoryType subCategoryType;

    public Transmission(String name,
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
        return CategoryType.Transmission;
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return subCategoryType;
    }
}


