import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
        int expiry = -1;
        //ATTRIBUTES
        List<Attribute> attributes = null;
        List<String> attributeDomain = null;

        sender = "SENDER";
        orderType = OrderType.Buy;
        itemType = "Used Car";
        expiry = 100;
        attributeDomain = Arrays.asList("Toy_Camry", "Toy_RAV4", "Toy_Corolla");
        Attribute attribute1 = new Attribute("Make_model", AttributeType.Categorical, true, attributeDomain, false);
        attributes = Arrays.asList(attribute1);

        int iterator = 0;

        while (!p.isClosed()) {
            System.out.println("[DESERIALIZE] " + iterator);
            JsonToken token = p.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                System.out.println("[DESERIALIZE] Currently parsing: " + fieldName);

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
                if ("EXPIRY".equals(fieldName)) {
                    expiry = p.getValueAsInt();
                }
                if ("ITEM_TYPE".equals(fieldName)) {
                    itemType = p.getValueAsString();
                }
                if ("ATTRIBUTES".equals(fieldName)) {

                }
            }
        }

        Item item = new Item(itemType, attributes);
        Order order = new Order(sender, orderType, item, expiry);
        return order;
    }
}
