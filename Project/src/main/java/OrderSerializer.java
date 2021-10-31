import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serialises an Order object into JSON string.
 *  Usage Example :
 *  String orderJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(orderTemp);
 * @author Peamawat Muenjohn
 */
public class OrderSerializer extends StdSerializer<Order> {
    public OrderSerializer() {
        this(null);
    }

    public OrderSerializer(Class<Order> t) {
        super(t);
    }

    @Override
    public void serialize(Order order, JsonGenerator g, SerializerProvider serializerProvider) throws IOException {
        g.writeStartObject();
        g.writeStringField("SENDER", order.GetSender());
        g.writeStringField("ORDER_TYPE", order.GetOrderType().name());
        g.writeStringField("ITEM_TYPE", order.GetItemType());
        g.writeObjectField("ATTRIBUTES", order.GetAttributes());
        g.writeStringField("PRICE", String.valueOf(order.GetPrice()));
        g.writeStringField("EXPIRY", String.valueOf(order.GetExpiry()));
        g.writeEndObject();
    }
}