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

    public String GetItemType() {
        return itemType;
    }

    public void SetItemType(String itemType) {
        this.itemType = itemType;
    }

    public HashMap<String, String> GetAttributes() {
        return attributes;
    }

    public void SetAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void AddAttribute(String attributeType, String attributeValue) {
        attributes.put(attributeType, attributeValue);
    }


}
