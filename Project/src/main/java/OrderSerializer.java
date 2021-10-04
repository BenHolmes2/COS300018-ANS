import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
/** Deserialises an Order object into JSON string.
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
        g.writeStringField("SENDER", order.getSender());
        g.writeStringField("ORDER_TYPE", order.getOrderType().name());
        g.writeStringField("ITEM_DESCRIPTION", order.getItemDescription());
        g.writeStringField("EXPIRY", String.valueOf(order.getExpiry()));
        g.writeEndObject();
    }
}