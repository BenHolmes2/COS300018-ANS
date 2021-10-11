import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.*;

/**
 * Deserialises a JSON object into a Catalogue object.
 *
 * @author Peamawat Muenjohn
 */
public class CatalogueDeserializer extends StdDeserializer<Catalogue> {
    public CatalogueDeserializer() {
        this(null);
    }

    public CatalogueDeserializer(Class<Catalogue> t) {
        super(t);
    }

    @Override
    public Catalogue deserialize(JsonParser p, DeserializationContext cx) throws IOException {
        List<CatalogueItem> catalogueItems = new ArrayList<>();
        while (!p.isClosed()) {
            JsonToken token = p.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                if ("ITEMS".equals(fieldName)) {
                    p.nextToken();
                    catalogueItems = ArrayTokenToList(p);
                }
            }
        }
        return new Catalogue(catalogueItems);
    }

    private List<CatalogueItem> ArrayTokenToList(JsonParser p) throws IOException {
        ArrayNode node = new ObjectMapper().readTree(p);
        Iterator<JsonNode> it = node.elements();
        CatalogueItem[] items = new CatalogueItem[node.size()];
        for (int i = 0; i < node.size(); i++) {
            String itemString = it.next().toString();
            items[i] = new ObjectMapper().readValue(itemString, CatalogueItem.class);
        }
        return Arrays.asList(items);
    }
}
