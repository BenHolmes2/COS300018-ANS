import java.util.List;

//this is the enumerator for the attirbute type
enum AttributeType {
    Quality,
    Category
}

//This is the general class for the attribute used in all files.
public class Attribute {
    String name;
    AttributeType type;
    boolean mandatory;
    List<String> domain;
    boolean greaterIsBetter;
}
