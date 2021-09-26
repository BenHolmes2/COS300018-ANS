import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

//this is the general class for the orders used in all files
public class Order {
    private String sender;
    private OrderType order_type;
    private Item item;
    private int expiry;

    OrderType getOrderType() {
        return order_type;
    }

    Item getItem() {
        return item;
    }

    int getExpiry() {
        return expiry;
    }

    public String getSender() {
        return sender;
    }

    public Order(String s, OrderType t, Item it, int exp) {
        sender = s;
        order_type = t;
        item = it;
        expiry = exp;
    }

    /**
     * {ORDER:{SENDER:"sender name",}}
     */
    @Override
    public String toString() {
        return "\n ------ ORDER ------" + "\n SENDER: " + sender + "\n TYPE: " + order_type + "\n ITEM: " + item.toString() + "\n EXPIRY: " + expiry + "\n ------ END ORDER ------ \n";
    }
}

//this is the enumerator for the order type
enum OrderType {
    Buy,
    Sell
}

class OrderSerializer extends StdSerializer<Order> {
    public OrderSerializer() {
        this(null);
    }

    public OrderSerializer(Class<Order> t) {
        super(t);
    }

    @Override
    public void serialize(Order order, JsonGenerator g, SerializerProvider serializerProvider) throws IOException {
        Item item = order.getItem();

        g.writeStartObject();
        g.writeStringField("SENDER", order.getSender());
        g.writeStringField("ORDER_TYPE", order.getOrderType().name());
        g.writeStringField("EXPIRY", String.valueOf(order.getExpiry()));
        g.writeFieldName("ITEM");
        g.writeStartObject(item);    // g.writeStartObject();
            //g.writeFieldName("ITEM_TYPE_AAA");
            g.writeStringField("ITEM_TYPE", item.GetType());
            g.writeFieldName("ATTRIBUTES");
            g.writeStartArray();
            for(Attribute attr : item.getAttributes()) {
                g.writeStartObject();
                g.writeStringField("NAME", attr.getName());
                g.writeStringField("ATTRIBUTE_TYPE", attr.getType().name());
                g.writeStringField("MANDATORY", String.valueOf(attr.isMandatory()));
                // DOMAIN
                g.writeFieldName("DOMAIN");
                String[] domain = attr.getDomain().toArray(new String[0]);
                g.writeArray(domain, 0, domain.length);
                g.writeStringField("GREATER_IS_BETTER", String.valueOf(attr.isGreaterIsBetter()));
                g.writeEndObject();
            }
            g.writeEndArray();
        g.writeEndObject();
        g.writeEndObject();
    }
}
