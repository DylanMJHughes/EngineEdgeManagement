package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class ExhaustPipe extends Body {
    public ExhaustPipe(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.ExhaustPipe;
    }
}

