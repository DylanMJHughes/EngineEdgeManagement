package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Thermostat extends Body {
    public Thermostat(String name,
                      double costPrice,
                      double retailPrice,
                      int quantity,
                      String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Thermostat;
    }
}
