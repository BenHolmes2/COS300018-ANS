import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Order class for use in both MarketplaceAgent and MarketUserAgent.
 */
@JsonSerialize(using = OrderSerializer.class)
@JsonDeserialize(using = OrderDeserializer.class)
public class Order {
    private String sender;
    private OrderType order_type;
    private String itemDescription;
    private int expiry;
//TODO: Add Time of Sending, to check on Marketplace main 10s cycle

    OrderType getOrderType() {
        return order_type;
    }

    String getItemDescription() {
        return itemDescription;
    }

    int getExpiry() {
        return expiry;
    }

    public String getSender() {
        return sender;
    }

    public Order(String s, OrderType t, String it, int exp) {
        sender = s;
        order_type = t;
        itemDescription = it;
        expiry = exp;
    }

    public String Print() {
        return "\n ------ ORDER ------" + "\n SENDER: " + sender + "\n TYPE: " + order_type + "\n ITEM: " + itemDescription + "\n EXPIRY: " + expiry + "\n ------ END ORDER ------ \n";
    }
}

//this is the enumerator for the order type
enum OrderType {
    Buy,
    Sell
}
