import java.util.List;

//This is the general class for the attribute used in all files.
public class Attribute {
    private String name;
    private AttributeType type;
    private boolean mandatory;
    List<String> domain;
    private boolean greaterIsBetter;

    String getName(){
        return name;
    }

    AttributeType getType() {
        return type;
    }

    boolean isMandatory() {
        return mandatory;
    }

    boolean isGreaterIsBetter() {
        return greaterIsBetter;
    }
}

//this is the enumerator for the attribute type
enum AttributeType {
    Quality,
    Category
}
