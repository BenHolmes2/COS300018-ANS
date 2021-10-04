import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Deserialises an Item object into JSON string.
 *
 * @author Peamawat Muenjohn
 */
public class ItemSerializer extends StdSerializer<Item> {
    public ItemSerializer() {
        this(null);
    }

    public ItemSerializer(Class<Item> t) {
        super(t);
    }

    @Override
    public void serialize(Item item, JsonGenerator g, SerializerProvider serializerProvider) throws IOException {
        g.writeStartObject();
        g.writeStringField("ITEM_TYPE", item.GetType());
        g.writeFieldName("ATTRIBUTES");
        g.writeStartArray();
        for (Attribute attr : item.getAttributes()) {
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
    }
}