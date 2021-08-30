import java.util.List;

//This is the general class for the catalogue used everywhere
public class Catalogue {

    //list of items in the catalogue
    List<Item> items;

    //constructor
    Catalogue(String catSrc){

    }

    //finds item within the list of items
    Item FindItem(String itemType){
        if (items.contains(itemType)) {
            for (Item item : items) {
                if (item.GetType().equals(itemType)) {
                    return item;
                }
            }
        }
        else {
            return new Item();
        }
        return null;
    }

    //finds index of item within list of items
    int FindIndexOfItem(String itemType){
        if(items.indexOf(itemType) != -1) {
            return items.indexOf(itemType);
        }
        else {
            return 0;
        }
    }

    //saves catalogue
    private void SaveCatalogue(){

    }
}
