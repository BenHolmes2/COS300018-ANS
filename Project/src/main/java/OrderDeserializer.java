import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Deserialises a JSON object into an Order object.
 * Usage Example:
 * Order order = new ObjectMapper().readValue([JSON string], Order.class);
 *
 * @author Peamawat Muenjohn
 */
public class OrderDeserializer extends StdDeserializer<Order> {
    public OrderDeserializer() {
        this(null);
    }

    public OrderDeserializer(Class<Order> t) {
        super(t);
    }

    @Override
    public Order deserialize(JsonParser p, DeserializationContext cx) throws IOException, JsonProcessingException {

        // ORDER
        String sender = null;
        OrderType orderType = null;
        //ITEM
        String itemType = null;
        HashMap<String, String> itemAttributes = new HashMap<>();
        float price = -1;
        int expiry = -1;

        while (!p.isClosed()) {
            JsonToken token = p.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                token = p.nextToken();

                if ("SENDER".equals(fieldName)) {
                    sender = p.getValueAsString();
                }
                if ("ORDER_TYPE".equals(fieldName)) {
                    String ot = p.getValueAsString();
                    if ("Buy".equals(ot))
                        orderType = OrderType.Buy;
                    else if ("Sell".equals(ot))
                        orderType = OrderType.Sell;
                }
                if ("ITEM_TYPE".equals(fieldName)) {
                    itemType = p.getValueAsString();
                }
                if ("ATTRIBUTES".equals(fieldName)) {
                    itemAttributes = p.readValueAs(new TypeReference<HashMap<String, String>>() {
                    });
                }
                if ("PRICE".equals(fieldName)) {
                    price = (float)p.getValueAsLong();
                }
                if ("EXPIRY".equals(fieldName)) {
                    expiry = p.getValueAsInt();
                }
            }
        }

        Order order = new Order(sender, orderType, itemType, itemAttributes, price, expiry);
        return order;
    }
}
