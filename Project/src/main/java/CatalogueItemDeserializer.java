import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.*;

/**
 * Deserialises a JSON object into a CatalogueItem object.
 *
 * @author Peamawat Muenjohn
 */
public class CatalogueItemDeserializer extends StdDeserializer<CatalogueItem> {
    public CatalogueItemDeserializer() {
        this(null);
    }

    public CatalogueItemDeserializer(Class<CatalogueItem> t) {
        super(t);
    }

    @Override
    public CatalogueItem deserialize(JsonParser p, DeserializationContext cx) throws IOException, JsonProcessingException {

        //ITEM
        String itemType = null;
        List<CatalogueAttribute> catalogueAttributes = new ArrayList<>();

        while (!p.isClosed()) {
            JsonToken token = p.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                token = p.nextToken();
                if ("ITEM_TYPE".equals(fieldName)) {
                    itemType = p.getValueAsString();
                }
                if ("NAME".equals(fieldName)) {
                    String name = p.getValueAsString();
                    p.nextToken();
                    p.nextToken();
                    AttributeType attribute_type = AttributeType.valueOf(p.getValueAsString());
                    p.nextToken();
                    p.nextToken();
                    boolean mandatory = Boolean.parseBoolean(p.getValueAsString());
                    p.nextToken();
                    p.nextToken();
                    List<String> domain = ArrayTokenToList(p);
                    p.nextToken();
                    p.nextToken();
                    boolean greater_is_better = Boolean.parseBoolean(p.getValueAsString());
                    catalogueAttributes.add(new CatalogueAttribute(name, attribute_type, mandatory, domain, greater_is_better));
                }
            }
        }
        return new CatalogueItem(itemType, catalogueAttributes);
    }

    private List<String> ArrayTokenToList(JsonParser p) throws IOException {
        ArrayNode node = new ObjectMapper().readTree(p);
        Iterator<JsonNode> it = node.elements();
        String[] domain_array = new String[node.size()];
        for (int i = 0; i < node.size(); i++) {
            if (it.hasNext()) {
                domain_array[i] = it.next().asText();
            }
        }
        return Arrays.asList(domain_array);
    }
}
