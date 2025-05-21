package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Fuses extends Body {
    public Fuses(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Fuses;
    }
}
