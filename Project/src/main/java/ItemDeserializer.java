import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.*;

/**
 * Deserialises a JSON object into an Item object.
 *
 * @author Peamawat Muenjohn
 */
public class ItemDeserializer extends StdDeserializer<Item> {
    public ItemDeserializer() {
        this(null);
    }

    public ItemDeserializer(Class<Item> t) {
        super(t);
    }

    @Override
    public Item deserialize(JsonParser p, DeserializationContext cx) throws IOException, JsonProcessingException {

        //ITEM
        String itemType = null;
        List<Attribute> attributes = new ArrayList<>();

        int i = 0;
        while (!p.isClosed()) {
            System.out.println("[DESERIALIZE] " + i);
            JsonToken token = p.nextToken();
            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = p.getCurrentName();
                System.out.println("[DESERIALIZE] Currently parsing line: " + fieldName);
                token = p.nextToken();
                if ("ITEM_TYPE".equals(fieldName)) {
                    itemType = p.getValueAsString();
                    System.out.print("ITEM TYPE : " + itemType);
                }
                if("NAME".equals(fieldName)) {
                    String name = p.getValueAsString();
                    System.out.println("NAME ITERATED: " + name);
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
                    attributes.add(new Attribute(name, attribute_type, mandatory, domain, greater_is_better));
                }
            }
            i += 1;
        }
        return new Item(itemType, attributes);
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
