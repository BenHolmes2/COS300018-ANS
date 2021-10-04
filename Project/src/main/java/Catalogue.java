import java.util.List;

/** Catalogue class for use in MarketplaceAgent and MarketUserAgent
 */
public class Catalogue {

    //list of items in the catalogue
    private List<CatalogueItem> catalogueItems;

    //constructor
    Catalogue(String src) {
        // using src as a filepath, get catalogue from file
    }

    //finds item within the list of items
    CatalogueItem FindItem(String itemType) {
        if (catalogueItems.contains(itemType)) {
            for (CatalogueItem catalogueItem : catalogueItems) {
                if (catalogueItem.GetType().equals(itemType)) {
                    return catalogueItem;
                }
            }
        } else {
            // Add item to catalogue?
            // return new Item();
        }
        return null;
    }

    //finds index of item within list of items
    int FindIndexOfItem(String itemType) {
        if (catalogueItems.indexOf(itemType) != -1) {
            return catalogueItems.indexOf(itemType);
        } else {
            return 0;
        }
    }

    //saves catalogue
    private void SaveCatalogue() {
    }
}
