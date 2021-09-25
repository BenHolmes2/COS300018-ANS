//this is the general class for the orders used in all files
public class Order {
    private String sender;
    private OrderType order_type;
    private Item item;
    private int expiry;

    OrderType getOrderType(){
        return order_type;
    }

    Item getItem(){
        return item;
    }

    int getExpiry(){
        return expiry;
    }

    public String getSender() {
        return sender;
    }

    public Order(String s, OrderType t, Item it, int exp){
        sender = s;
        order_type = t;
        item = it;
        expiry = exp;
    }

    @Override
    public String toString() {
        return "\n ------ ORDER ------" + "\n SENDER: " + sender + "\n TYPE: " + order_type + "\n ITEM: "+ item.toString() + "\n EXPIRY: " + expiry + "\n ------ END ORDER ------ \n";
    }
}

//this is the enumerator for the order type
enum OrderType{
    Buy,
    Sell
}