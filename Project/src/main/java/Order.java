import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;

/**
 * Order class for use in both MarketplaceAgent and MarketUserAgent.
 */
@JsonSerialize(using = OrderSerializer.class)
@JsonDeserialize(using = OrderDeserializer.class)
public class Order {
    private String sender;
    private OrderType orderType;
    private String itemType;
    private HashMap<String, String> attributes; // Key = AttributeType, Value = Attribute Value
    private float price;
    private int expiry;

//TODO: Add Time of Sending, to check on Marketplace main 10s cycle
    public Order(String sender, OrderType orderType, String itemType, HashMap<String, String> attributes, float price, int expiry) {
        this.sender = sender;
        this.orderType = orderType;
        this.itemType = itemType;
        if (attributes == null) {
            this.attributes = new HashMap<>();
        } else {
            this.attributes = attributes;
        }
        this.expiry = expiry;
    }

    public String GetSender() {
        return sender;
    }

    public void SetSender(String sender) {
        this.sender = sender;
    }

    public OrderType GetOrderType() {
        return orderType;
    }

    public int GetExpiry() {
        return expiry;
    }

    public String GetItemType() {
        return itemType;
    }

    public void SetItemType(String itemType) {
        this.itemType = itemType;
    }

    public float GetPrice() {
        return price;
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

    public String ToPrettyString() {
        return "\n ------ ORDER ------" + "\n SENDER: " + sender + "\n TYPE: " + orderType + "\n ITEM_TYPE: " + itemType + "\n ATTRIBUTES: " + AttributesToString() + "\n PRICE: " + price + "\n EXPIRY: " + expiry + "\n ------ END ORDER ------ \n";
    }

    public String AttributesToString() {
        StringBuilder builder = new StringBuilder();
        for (String key : attributes.keySet()) {
            String entry = key + " : " + attributes.get(key);
            builder.append("\n " + entry);
        }
        return "\n {" + builder.toString() + "\n }";
    }
}

/**
 * Enumerator for order type.
 */
enum OrderType {
    Buy,
    Sell
}
