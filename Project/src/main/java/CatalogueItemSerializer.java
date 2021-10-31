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
        for (CatalogueAttribute attr : catalogueItem.GetAttributes()) {
            g.writeStartObject();
            g.writeStringField("NAME", attr.GetName());
            g.writeStringField("ATTRIBUTE_TYPE", attr.GetType().name());
            g.writeStringField("MANDATORY", String.valueOf(attr.IsMandatory()));
            g.writeFieldName("DOMAIN");
            if(attr.GetDomain() != null) {
                String[] domain = attr.GetDomain().toArray(new String[0]);
                g.writeArray(domain, 0, domain.length);
            } else {
                g.writeRawValue("null");
            }
            g.writeStringField("GREATER_IS_BETTER", String.valueOf(attr.IsGreaterIsBetter()));
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
    }
}