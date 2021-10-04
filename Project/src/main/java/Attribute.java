import java.util.List;

/**
 * Attribute class for use in Catalogue object exclusively?
 */
public class Attribute {
    private String name;
    private AttributeType attr_type;
    private boolean mandatory;
    private List<String> domain;
    private boolean greaterIsBetter;

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return attr_type;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<String> getDomain() {
        return domain;
    }

    public boolean isGreaterIsBetter() {
        return greaterIsBetter;
    }

    public Attribute(String n, AttributeType at, boolean m, List<String> dom, boolean gb) {
        name = n;
        attr_type = at;
        mandatory = m;
        domain = dom;
        greaterIsBetter = gb;
    }

    @Override
    public String toString() {
        String domainString = String.join(", ", domain);
        return (name + ", " + attr_type.toString() + ", " + mandatory + ", { " + domainString + " }, " + greaterIsBetter);
    }
}

//this is the enumerator for the attribute type
enum AttributeType {
    Quality,
    Categorical
}
