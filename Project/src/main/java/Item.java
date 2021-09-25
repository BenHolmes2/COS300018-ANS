import java.util.List;

//this is the general class for items used in all files
public class Item {

    private String item_type;
    private List<Attribute> attributes;

    String GetType(){
        return item_type;
    }

    public Item(String t, List<Attribute> attr){
        item_type = t;
        attributes = attr;
    }

    @Override
    public String toString(){
        String attributesString = String.join("\n", attributes.toString());
        return item_type + "\n ATTRIBUTES : " + attributesString;
    }
}
