//this is the general class for the orders used in all files
public class Order {
    private OrderType type;
    private Item item;
    private int expiry;
    //UserAgent owner;

    OrderType getOrderType(){
        return type;
    }

    Item getItem(){
        return item;
    }
    int getExpiry(){
        return expiry;
    }
}

//this is the enumerator for the order type
enum OrderType{
    Buy,
    Sell
}