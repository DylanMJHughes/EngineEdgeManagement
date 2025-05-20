package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Fenders extends Body {
    public Fenders(String name,
                   double costPrice,
                   double retailPrice,
                   int quantity,
                   String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Fenders;
    }
}

