import java.util.List;

//This is the general class for the attribute used in all files.
public class Attribute {
    String name;
    AttributeType type;
    boolean mandatory;
    List<String> domain;
    boolean greaterIsBetter;
}

//this is the enumerator for the attribute type
enum AttributeType {
    Quality,
    Category
}
