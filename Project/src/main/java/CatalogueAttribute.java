import java.util.List;

/**
 * Attribute class for use in Catalogue object exclusively?
 */
public class CatalogueAttribute {
    private String name;
    private AttributeType attributeType;
    private boolean mandatory;
    private List<String> domain;
    private boolean greaterIsBetter;

    public CatalogueAttribute(String name, AttributeType attributeType, boolean mandatory, List<String> domain, boolean greaterIsBetter) {
        this.name = name;
        this.attributeType = attributeType;
        this.mandatory = mandatory;
        this.domain = domain;
        this.greaterIsBetter = greaterIsBetter;
    }

    public String GetName() {
        return name;
    }

    public AttributeType GetType() {
        return attributeType;
    }

    public boolean IsMandatory() {
        return mandatory;
    }

    public List<String> GetDomain() {
        return domain;
    }

    public boolean IsGreaterIsBetter() {
        return greaterIsBetter;
    }



    @Override
    public String toString() {
        String domainString = String.join(", ", domain);
        return (name + ", " + attributeType.toString() + ", " + mandatory + ", { " + domainString + " }, " + greaterIsBetter);
    }
}

//this is the enumerator for the attribute type
enum AttributeType {
    Quality,
    Categorical
}
