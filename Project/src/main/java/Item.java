import java.util.List;

/** Item class for use in Catalogue and Order? objects.
 */
public class Item {

    private String item_type;
    private List<Attribute> attributes;

    public String GetType() {
        return item_type;
    }
    public List<Attribute> getAttributes() { return attributes; }

    public Item(String t, List<Attribute> attr) {
        item_type = t;
        attributes = attr;
    }

    @Override
    public String toString() {
        String attributesString = String.join(", \n", attributes.toString());
        return item_type + "\n ATTRIBUTES : " + attributesString;
    }
}
