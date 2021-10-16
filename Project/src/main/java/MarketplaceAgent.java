import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Agent
@Service
@ProvidedServices(@ProvidedService(type = IMarketService.class))
@RequiredServices(@RequiredService(name = "clockservice", type = IClockService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketplaceAgent implements IMarketService {
    //TODO: Add the path to the catalogue source file.
    protected Catalogue catalogue = new Catalogue("PathToCatalogueSourceFile");
    protected LinkedHashMap<Order, String> buyOrders = new LinkedHashMap<>();                                           // Orders and timestamps of when they were received.
    protected LinkedHashMap<Order, String> sellOrders = new LinkedHashMap<>();
    protected Set<SubscriptionIntermediateFuture<String>> subscriptions = new LinkedHashSet<>();

    private DateFormat format;
    private IClockService clock;

    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;

    /**
     * Allows subscription and subscription termination to this agent/service.
     */
    public ISubscriptionIntermediateFuture<String> subscribe() {
        // Add the subscription to the set of subscriptions
        SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
        subscriptions.add(ret);
        ret.setTerminationCommand(new TerminationCommand() {
            @Override
            public void terminated(Exception reason) {
                System.out.println("removed subscriber due to: " + reason);
                subscriptions.remove(ret);
            }
        });
        return ret;
    }

    public IFuture<String> getCatalogue() {
        System.out.print("\nGET CATALOGUE CALLED\n");
        String catalogueString = null;
        try {
            DEBUG_Catalogue();
            catalogueString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Future<>(catalogueString);
    }

    public IFuture<String> addOrders(String[] orders) {
        try {
            //  Deserialise array of order Strings into separate Order Objects
            for (int i = 0; i < orders.length; i++) {
                Order order = new ObjectMapper().readValue(orders[i], Order.class);
                if (order.getOrderType() == OrderType.Buy) {
                    buyOrders.put(order, format.format(clock.getTime()));
                    // buyOrders.add(order);
                } else {
                    sellOrders.put(order, format.format(clock.getTime()));
                }
                System.out.println("[MarketplaceAgent.java] " + (order.getOrderType() == OrderType.Buy ? "BUY ORDER" : "SELL ORDER") + " FROM [" + order.getSender() + "] RECEIVED[" + i + "] FOR " + order.getItemType());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new Future<>("accepted");
    }

    /* --------------- AGENT LIFE CYCLE ---------- */
    @ServiceStart
    public IFuture<Void> startService() {
        format = new SimpleDateFormat("hh:mm:ss");
        final Future<Void> ret = new Future<>();
        IFuture<IClockService> fut = requiredServicesFeature.getRequiredService("clockservice");
        fut.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret) {
            @Override
            public void customResultAvailable(IClockService result) throws Exception {
                clock = result;
                ret.setResult(null);
            }
        });
        return ret;
    }

    /**
     * Due to annotation, called once after agent is initialized.
     * The internal access parameter is optional and is injected automatically.
     * Every 10 seconds,
     * 1) Check expired orders,
     * 2) Match received orders,
     * 3) Send negotiation invites,
     * 4) Send settlement details.
     */
    @AgentBody
    public void body(IInternalAccess ia) {
        IExecutionFeature exe = ia.getComponentFeature(IExecutionFeature.class); // Execution feature provides methods for controlling the execution of the agent.
        System.out.println("Marketplace Agent Started.");
        System.out.println("[Marketplace Agent, Time ]:  " + format.format(clock.getTime()));
        exe.repeatStep(10000 - System.currentTimeMillis() % 10000, 10000, ia1 -> {
            // CheckExpiredOrders();
            SettleOrders();
            // Send settlement details to all subscribers
            for (SubscriptionIntermediateFuture<String> subscriber : subscriptions) {
                //TODO: Send settlement details + negotiation invites( + catalogue?) to every subscriber
                subscriber.addIntermediateResultIfUndone("Wao!");   // IfUndone is used to ignore errors, when subscription was cancelled during.
            }
            return IFuture.DONE;
        });
    }

    /* --------------- HELPER METHODS ---------- */

    private void DEBUG_Catalogue() throws IOException {
        List<String> attr1_domain = Arrays.asList("App_iPhone11", "App_iPhone12", "SS_Galaxy12", "SS_Note12");
        List<String> attr2_domain = Arrays.asList("0", "5000");
        CatalogueAttribute attr1 = new CatalogueAttribute("Make_Model", AttributeType.Categorical, true, attr1_domain, false);
        CatalogueAttribute attr2 = new CatalogueAttribute("Battery_Capacity", AttributeType.Quality, false, attr2_domain, true);
        CatalogueItem phoneItem = new CatalogueItem("Phone", Arrays.asList(attr1, attr2));

        List<String> attr4_domain = Arrays.asList(
                "Toy_Camry", "Toy_RAV4", "Toy_Corolla",
                "Maz_CX5", "Maz_6", "Maz_3", "Maz_CX9",
                "Sub_Outback", "Sub_Forester");
        List<String> attr5_domain = Arrays.asList("1990", "2021");
        CatalogueAttribute attr4 = new CatalogueAttribute("Make_Model", AttributeType.Categorical, true, attr4_domain, false);
        CatalogueAttribute attr5 = new CatalogueAttribute("Year", AttributeType.Quality, false, attr5_domain, true);
        CatalogueItem carItem = new CatalogueItem("Used_Car", Arrays.asList(attr4, attr5));
/*
        System.out.println("-------------------------MARKETPLACE AGENT-------------------------");
        String phoneItemJSON = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(phoneItem);
        System.out.println(phoneItemJSON);
        CatalogueItem phoneItemDeserialized = new ObjectMapper().readValue(phoneItemJSON, CatalogueItem.class);
        System.out.println(phoneItemDeserialized.ToPrettyString());
        System.out.println("---------------------END MARKETPLACE AGENT-------------------------");
*/
        catalogue.AddItem(phoneItem);
        catalogue.AddItem(carItem);

        String catalogueToJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogue);
        // System.out.println(catalogueToJson);
        Catalogue catalogueFromJSon = new ObjectMapper().readValue(catalogueToJson, Catalogue.class);
        List<CatalogueItem> items = catalogueFromJSon.GetCatalogue();
        // items.forEach(catalogueItem -> System.out.println(catalogueItem.ToPrettyString()));
    }

    private void CheckExpiredOrders() {

    }

    // (Expired orders should have already been taken care of and removed from buyOrders and sellOrders before this method is run.)
    private ArrayList<String> SettleOrders() {
        System.out.println("SettleOrders called.");
        System.out.println(buyOrders);
        System.out.println(sellOrders);
        ArrayList<String> settlements = new ArrayList<>();
        for(Order bOrder : buyOrders.keySet()) {
            for(Order sOrder : sellOrders.keySet()) {
                // Orders are from the same sender, skip this pair of orders.
                if (bOrder.getSender().equals(sOrder.getSender())) {
                    continue;
                }
                System.out.println("(x, y) : ( [" + bOrder.getSender() + "] " + bOrder.getItemType() + ", [" + sOrder.getSender() +  "] " + sOrder.getItemType() + ")");
                if (bOrder.getItemType().equals(sOrder.getItemType())) {
                    // Orders are a perfect match, add settlement details and move onto next set of orders.
                    if (bOrder.getAttributes().equals(sOrder.getAttributes()) && bOrder.getPrice() == sOrder.getPrice()) {
                        settlements.add(SettleOrder(bOrder, sOrder));
                        continue;
                    }
                    // Otherwise, match mandatory attributes.
                    CatalogueItem catItem = catalogue.FindItem(bOrder.getItemType());
                    if (MandatoryAttributesMatch(bOrder, sOrder, catItem)) {
                        // Send negotiation invites.
                        System.out.println("Mandatory attributes match!");
                    }
                }
            }
        }
        return settlements;
        // CLEAR SETTLED ORDERS FROM BUY AND SELL ORDERS
    }

    private boolean MandatoryAttributesMatch(Order bOrder, Order sOrder, CatalogueItem catItem) {
        List<String> mAttrNames = new ArrayList<>();
        for (CatalogueAttribute at : catItem.getMandatoryAttributes()) {
            mAttrNames.add(at.getName());
        }
        if (mAttrNames.isEmpty()) {
            return false;
        }                                                                                  // Item has no mandatory attributes, cannot be matched, return false.
        if (mAttrNames.size() == 1) {
            String mAttrName = mAttrNames.get(0);
            if (bOrder.getAttributes().containsKey(mAttrName) && sOrder.getAttributes().containsKey(mAttrName)) {
                return bOrder.getAttributes().get(mAttrName).equals(sOrder.getAttributes().get(mAttrName));
            }
        }                                                                                // Item has only one mandatory attribute. Compare values quicker than looping.

        int m = 0;                                                                                                      // let m be the amount of matching mandatory attributes (default 0)
        for (Map.Entry<String, String> at : bOrder.getAttributes().entrySet()) {                                        // for each attribute in the bOrder
            if (!mAttrNames.contains(at.getKey())) {
                continue;
            }                                                                // If this attribute isn't mandatory, skip this attribute
            if (sOrder.getAttributes().containsKey(at.getKey()) && at.getValue().equals(sOrder.getAttributes().get(at.getKey()))) {
                m += 1;                                                                                                 // If sOrder also has this attribute and their value is the same, increment m by one.
            }
        }
        System.out.println("[MarketplaceAgent.MandatoryAttributesMatch]: m = " + m + " mAttrNames.Size() = " + mAttrNames.size());
        // mod m by total count of mandatory attributes
        // if there is no remainder, match is valid
        return m % mAttrNames.size() == 0;
    }

    private String SettleOrder(Order buyOrder, Order sellOrder) {
        // Concatenate buyOrder and sellOrder senders
        // Concatenate item details
        // Concatenate price
        // Concatenate commission?
        // Concatenate time of settlement
        System.out.println("[MarketplaceAgent.SettleOrder]: " + buyOrder.getItemType() + " AND " + sellOrder.getItemType() + " SETTLEMENT.");
        return null;
    }

    /**
     * Start a JadeX platform and add just this MarketplaceAgent.
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setNetworkName("102326287");
        config.setNetworkPass("102326287");
        config.addComponent(MarketplaceAgent.class);
        config.setAwareness(true);
        Starter.createPlatform(config).get();
    }
}
