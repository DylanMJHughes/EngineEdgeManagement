//CrankShaftCamshafts
package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class CrankshaftCamshafts extends Body {
    public CrankshaftCamshafts(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.CrankshaftCamshafts;
    }
}