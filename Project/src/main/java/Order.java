//this is the general class for the orders used in all files
public class Order {
//TODO: Differentiate between
// ORDER -> ITEM -> ATTRIBUTE (Make_model: Toy_Camry, 1990: Year,)
// and
// CATALOGUE -> ITEM -> ATTRIBUTE domain ("Make_model", AttributeType, isMandatory, ["Toy_Camry", "Toy_RAV4"...], isGreaterIsBetter)

    private String sender;
    private OrderType order_type;
    private Item item;
    private int expiry;
//TODO: Add Time of Sending, to check on Marketplace main 10s cycle

    OrderType getOrderType() {
        return order_type;
    }

    Item getItem() {
        return item;
    }

    int getExpiry() {
        return expiry;
    }

    public String getSender() {
        return sender;
    }

    public Order(String s, OrderType t, Item it, int exp) {
        sender = s;
        order_type = t;
        item = it;
        expiry = exp;
    }

    /**
     * {ORDER:{SENDER:"sender name",}}
     */
    @Override
    public String toString() {
        return "\n ------ ORDER ------" + "\n SENDER: " + sender + "\n TYPE: " + order_type + "\n ITEM: " + item.toString() + "\n EXPIRY: " + expiry + "\n ------ END ORDER ------ \n";
    }
}

//this is the enumerator for the order type
enum OrderType {
    Buy,
    Sell
}
