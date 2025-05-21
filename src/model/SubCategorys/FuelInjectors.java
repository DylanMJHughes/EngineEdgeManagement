package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class FuelInjectors extends Body {
    public FuelInjectors(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.FuelInjectors;
    }
}
