import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Deserialises a JSON object into an Order object.
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
                if ("EXPIRY".equals(fieldName)) {
                    expiry = p.getValueAsInt();
                }
            }
        }


        itemAttributes.put("DEBUG_KEY", "DEBUG_VALUE");
        itemAttributes.put("DEBUG_KEY", "DEBUG_VALUE");
        itemAttributes.put("DEBUG_KEY", "DEBUG_VALUE");
        Order order = new Order(sender, orderType, itemType, itemAttributes, expiry);
        return order;
    }
}
