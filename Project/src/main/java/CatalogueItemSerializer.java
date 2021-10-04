import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Deserialises a Catalogue Item object into JSON string.
 *
 * @author Peamawat Muenjohn
 */
public class CatalogueItemSerializer extends StdSerializer<CatalogueItem> {
    public CatalogueItemSerializer() {
        this(null);
    }

    public CatalogueItemSerializer(Class<CatalogueItem> t) {
        super(t);
    }

    @Override
    public void serialize(CatalogueItem catalogueItem, JsonGenerator g, SerializerProvider serializerProvider) throws IOException {
        g.writeStartObject();
        g.writeStringField("ITEM_TYPE", catalogueItem.GetType());
        g.writeFieldName("ATTRIBUTES");
        g.writeStartArray();
        for (CatalogueAttribute attr : catalogueItem.getAttributes()) {
            g.writeStartObject();
            g.writeStringField("NAME", attr.getName());
            g.writeStringField("ATTRIBUTE_TYPE", attr.getType().name());
            g.writeStringField("MANDATORY", String.valueOf(attr.isMandatory()));
            if(attr.getValue() != null)
                g.writeStringField("VALUE", String.valueOf(attr.getValue()));
            else
                g.writeStringField("VALUE", "null");
            g.writeFieldName("DOMAIN");
            if(attr.getDomain() != null) {
                System.out.println("[ItemSerializer] Domain not null, writing to array");
                String[] domain = attr.getDomain().toArray(new String[0]);
                g.writeArray(domain, 0, domain.length);
            } else {
                g.writeRawValue("null");
            }
            g.writeStringField("GREATER_IS_BETTER", String.valueOf(attr.isGreaterIsBetter()));
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
    }
}