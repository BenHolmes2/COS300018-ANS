//this is the enumerator for the order type
enum OrderType{
    Buy,
    Sell
}

//this is the general class for the orders used in all files
public class Order {

    OrderType type;
    Item item;
    int expiry;
    //UserAgent owner;
}
