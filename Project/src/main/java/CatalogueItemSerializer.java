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
        g.writeStringField("ITEM_TYPE", catalogueItem.getType());
        g.writeFieldName("ATTRIBUTES");
        g.writeStartArray();
        for (CatalogueAttribute attr : catalogueItem.getAttributes()) {
            g.writeStartObject();
            g.writeStringField("NAME", attr.getName());
            g.writeStringField("ATTRIBUTE_TYPE", attr.getType().name());
            g.writeStringField("MANDATORY", String.valueOf(attr.isMandatory()));
            g.writeFieldName("DOMAIN");
            if(attr.getDomain() != null) {
                String[] domain = attr.getDomain().toArray(new String[0]);
                g.writeArray(domain, 0, domain.length);
            } else {
                g.writeRawValue("null");
            }
            g.writeStringField("GREATER_IS_BETTER", String.valueOf(attr.greaterIsBetter()));
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
    }
}