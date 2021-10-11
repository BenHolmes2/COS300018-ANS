import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serialises whole Catalogue object into JSON string.
 * Usage Example :
 * Catalogue catalogueObject;
 * String catalogueJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogueObject);
 *
 * @author Peamawat Muenjohn
 */
public class CatalogueSerializer extends StdSerializer<Catalogue> {
    public CatalogueSerializer() {
        this(null);
    }

    public CatalogueSerializer(Class<Catalogue> t) {
        super(t);
    }

    @Override
    public void serialize(Catalogue catalogue, JsonGenerator g, SerializerProvider serializerProvider) throws IOException {
        g.writeStartObject();
        g.writeFieldName("ITEMS");
        g.writeStartArray();
        for (CatalogueItem item : catalogue.GetCatalogue()) {
            g.writeObject(item);
        }
        g.writeEndArray();
        g.writeEndObject();
    }
}