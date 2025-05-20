package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Doors extends Body {
    public Doors(String name,
                   double costPrice,
                   double retailPrice,
                   int quantity,
                   String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Doors;
    }
}
