import java.util.HashMap;

/** Item class for use in Orders and UserInventories
 *  Contains the item name, and attributes the item possesses
 */
public class Item {
    private String itemType;
    private HashMap<String, String> attributes; // Key = AttributeType, Value = Attribute Value

    public Item(String itemType, HashMap<String, String> attributes) {
        this.itemType = itemType;
        if(attributes == null) {
            this.attributes = new HashMap<>();
        } else {
            this.attributes = attributes;
        }
    }

    public String getItemType() {
        return itemType;
    }

    public void setName(String itemType) {
        this.itemType = itemType;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attributeType, String attributeValue) {
        attributes.put(attributeType, attributeValue);
    }


}
