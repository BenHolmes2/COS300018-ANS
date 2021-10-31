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

    public String GetType() {
        return item_type;
    }

    public List<CatalogueAttribute> GetAttributes() {
        return catalogueAttributes;
    }

    public CatalogueItem(String t, List<CatalogueAttribute> attr) {
        item_type = t;
        catalogueAttributes = attr;
    }

    public List<CatalogueAttribute> GetMandatoryAttributes() {
        List<CatalogueAttribute> mandatoryAttributes = new ArrayList<>();
        for (CatalogueAttribute attr : catalogueAttributes) {
            if (attr.IsMandatory())
                mandatoryAttributes.add(attr);
        }
        if (mandatoryAttributes.size() != 0)
            return mandatoryAttributes;
        return null;
    }

    public String PrettyPrint() {
        return "\n ------ CATALOGUE_ITEM ------ " + "\nITEM_TYPE: " + item_type + "\nATTRIBUTES : " + catalogueAttributes.toString() + "\n ------ END CATALOGUE_ITEM ------ \n";
    }
}
