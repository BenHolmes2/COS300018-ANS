import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogue class for use in MarketplaceAgent and MarketUserAgent
 */
@JsonSerialize(using = CatalogueSerializer.class)
@JsonDeserialize(using = CatalogueDeserializer.class)
public class Catalogue {

    //list of items in the catalogue
    private List<CatalogueItem> catalogueItems;

    //constructor
    public Catalogue(String src) {
        // using src as a filepath, get catalogue from file
    }

    public Catalogue (List<CatalogueItem> items) {
        catalogueItems = items;
    }

    //finds item within the list of items
    public CatalogueItem FindItem(String itemType) {
        for (CatalogueItem item : catalogueItems) {
            if (item.GetType().equals(itemType)) {
                return item;
            }
        }
        return null;
    }

    //finds index of item within list of items
    public int FindIndexOfItem(String itemType) {
        for(CatalogueItem item : catalogueItems) {
            if(item.GetType() == itemType)
                return catalogueItems.indexOf(item);
        }
        return -1;
    }

    public List<CatalogueItem> GetCatalogue() {
        return catalogueItems;
    }

    public void SetCatalogue(List<CatalogueItem> catalogueItems) {
        this.catalogueItems = catalogueItems;
    }

    public void AddItem(CatalogueItem item) {
        if(catalogueItems == null) {
            catalogueItems = new ArrayList<CatalogueItem>();
        }
        catalogueItems.add(item);
    }

    //saves catalogue
    private void SaveCatalogue() {
    }
}
