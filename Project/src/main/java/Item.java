import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/** Item class for use in Catalogue and Order? objects.
 */
@JsonSerialize(using = ItemSerializer.class)
@JsonDeserialize(using = ItemDeserializer.class)
public class Item {

    private String item_type;
    private List<Attribute> attributes;

    public String GetType() {
        return item_type;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Item(String t, List<Attribute> attr) {
        item_type = t;
        attributes = attr;
    }

    public String Print() {
        return "\n ------ ITEM ------ " + "\nITEM_TYPE: " + item_type + "\nATTRIBUTES : " + attributes.toString() + "\n ------ END ITEM ------ \n";
    }
}
