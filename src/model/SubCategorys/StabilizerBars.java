package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class StabilizerBars extends Body {
    public StabilizerBars (String name,
                                double costPrice,
                                double retailPrice,
                                int quantity,
                                String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.StabilizerBars;
    }
}
