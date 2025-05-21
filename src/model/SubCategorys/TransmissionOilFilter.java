//      TransmissionOilFilter

package model.SubCategorys;

import model.Body;
import model.SubCategoryType;

public class TransmissionOilFilter extends Body {
    public TransmissionOilFilter (String name,
                                double costPrice,
                                double retailPrice,
                                int quantity,
                                String imagePath) {
        super(name, costPrice, retailPrice, quantity, imagePath);
    }

    @Override
    public SubCategoryType getSubCategoryType() {
        return SubCategoryType.TransmissionOilFilter;
    }
}
