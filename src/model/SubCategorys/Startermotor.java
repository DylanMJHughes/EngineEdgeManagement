package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Startermotor extends Body {
    public Startermotor (String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.StarterMotor;
    }
}