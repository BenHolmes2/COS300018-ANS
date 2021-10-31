import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Item class for use in Catalogue entries
 */
@JsonSerialize(using = CatalogueItemSerializer.class)
@JsonDeserialize(using = CatalogueItemDeserializer.class)
public class CatalogueItem {

    private String item_type;
    private List<CatalogueAttribute> catalogueAttributes;

    public CatalogueItem(String t, List<CatalogueAttribute> attr) {
        item_type = t;
        catalogueAttributes = attr;
    }

    public String getType() {
        return item_type;
    }

    public List<CatalogueAttribute> getAttributes() {
        return catalogueAttributes;
    }



    public List<CatalogueAttribute> getMandatoryAttributes() {
        List<CatalogueAttribute> mandatoryAttributes = new ArrayList<>();
        for (CatalogueAttribute attr : catalogueAttributes) {
            if (attr.isMandatory())
                mandatoryAttributes.add(attr);
        }
        if (mandatoryAttributes.size() != 0)
            return mandatoryAttributes;
        return null;
    }

    public String prettyPrint() {
        return "\n ------ CATALOGUE_ITEM ------ " + "\nITEM_TYPE: " + item_type + "\nATTRIBUTES : " + catalogueAttributes.toString() + "\n ------ END CATALOGUE_ITEM ------ \n";
    }
}
