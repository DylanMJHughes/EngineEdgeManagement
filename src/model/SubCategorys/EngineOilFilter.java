//EngineOilFilter
package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class EngineOilFilter extends Body {
    public EngineOilFilter(String name,
                               double costPrice,
                               double retailPrice,
                               int quantity,
                               String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.EngineOilFilter;
    }
}
