import java.util.List;

/**
 * Attribute class for use in Catalogue object exclusively?
 */
public class CatalogueAttribute {
    private String name;
    private AttributeType attributeType;
    private boolean mandatory;
    private String value;
    private List<String> domain;
    private boolean greaterIsBetter;

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return attributeType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getValue() {
        return value;
    };

    public List<String> getDomain() {
        return domain;
    }

    public boolean isGreaterIsBetter() {
        return greaterIsBetter;
    }

    public CatalogueAttribute(String name, AttributeType attributeType, boolean mandatory, String value, List<String> domain, boolean greaterIsBetter) {
        this.name = name;
        this.attributeType = attributeType;
        this.mandatory = mandatory;
        this.value = value;
        this.domain = domain;
        this.greaterIsBetter = greaterIsBetter;
    }

    @Override
    public String toString() {
        if(domain != null) {
            String domainString = String.join(", ", domain);
            return (name + ", " + attributeType.toString() + ", " + mandatory + ", " + "NULL VALUE" + ", { " + domainString + " }, " + greaterIsBetter);
        }
        return (name + ", " + attributeType.toString() + ", " + mandatory + ", " + value + ", { " + "NULL DOMAIN" + " }, " + greaterIsBetter);
    }
}

//this is the enumerator for the attribute type
enum AttributeType {
    Quality,
    Categorical
}
