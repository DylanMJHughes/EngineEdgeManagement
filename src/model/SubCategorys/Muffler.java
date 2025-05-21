package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Muffler extends Body {
    public Muffler(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Muffler;
    }
}

