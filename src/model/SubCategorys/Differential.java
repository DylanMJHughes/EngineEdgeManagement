//Differential

package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class Differential extends Body {
    public Differential (String name,
                                double costPrice,
                                double retailPrice,
                                int quantity,
                                String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.Differential;
    }
}
