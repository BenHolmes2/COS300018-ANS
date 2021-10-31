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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Agent which provides a MarketService,
 * and contains settlement/negotiation functionality for UserAgents.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type = IMarketService.class))
@RequiredServices(@RequiredService(name = "clockservice", type = IClockService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketplaceAgent implements IMarketService {
    //TODO: Add file I/O for catalogue source file.
    protected Catalogue catalogue = new Catalogue("");
    protected LinkedHashMap<Order, String> buyOrders = new LinkedHashMap<>();                                           // Orders and their timestamp (upon receipt)
    protected LinkedHashMap<Order, String> sellOrders = new LinkedHashMap<>();                                          // Orders and their timestamp (upon receipt)
    protected ArrayList<Order> reservedOrders = new ArrayList<>();                                                      // Temporary List for orders that have been matched/neg, reset after each cycle
    protected Set<SubscriptionIntermediateFuture<List<List<String>>>> subscriptions = new LinkedHashSet<>();

    private DateFormat format;
    private IClockService clock;

    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;

    /**
     * Allows subscription and subscription termination to this agent/service.
     */
    public ISubscriptionIntermediateFuture<List<List<String>>> subscribe() {
        // Add the subscription to the set of subscriptions
        SubscriptionIntermediateFuture<List<List<String>>> ret = new SubscriptionIntermediateFuture<List<List<String>>>();
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

    /**
     * Shares locally stored Catalogue with UserAgents which require this service.
     * @return String formatted in JSON of this object's catalogue
     */
    public IFuture<String> getCatalogue() {
        System.out.print("\n[MarketplaceAgent.java] GET CATALOGUE CALLED\n");
        String catalogueString = null;
        try {

            catalogueString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Future<>(catalogueString);
    }

    /**
     * Adds orders from UserAgents which require this service,
     * and sorts/adds the orders into locally stored buyOrders/sellOrders
     * @param orders Array of Strings, where each string is a JSON formatted Order object.
     * @return String which confirms that the orders have been received.
     */
    public IFuture<String> addOrders(String[] orders) {
        try {
            //  Deserialise array of order Strings into separate Order Objects
            for (int i = 0; i < orders.length; i++) {
                Order order = new ObjectMapper().readValue(orders[i], Order.class);
                String timestamp = format.format(clock.getTime());
                if (order.getOrderType() == OrderType.Buy) {
                    buyOrders.put(order, timestamp);
                } else {
                    sellOrders.put(order, timestamp);
                }
                System.out.println("[MarketplaceAgent.java] " + (order.getOrderType() == OrderType.Buy ? "BUY ORDER" : "SELL ORDER") + " FROM [" + order.getSender() + "] RECEIVED[" + i + "] FOR " + order.getItemType() + " AT " + timestamp);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new Future<>("confirmation");
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
        try {
            DEBUG_Catalogue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Every 10 seconds,
     * 1) Check expired orders,
     * 2) Match received orders,
     * 3) Send settlement details + negotiation invites,
     * 4) Remove settled orders from buyOrder and sellOrder lists.
     */
    @AgentBody
    public void body(IInternalAccess ia) {
        IExecutionFeature exe = ia.getComponentFeature(IExecutionFeature.class);                                        // Execution feature provides methods for controlling the execution of the agent.
        System.out.println("Marketplace Agent Started." + format.format(clock.getTime()));
        exe.repeatStep(10000 - System.currentTimeMillis() % 10000, 10000, ia1 -> {
            List<List<String>> settlements = new ArrayList<>();
            settlements.addAll(settleExpiredOrders());                                                                  // Settle all expired orders
            settlements.addAll(settleOrders());                                                                         // Settle remaining valid orders.
            for (SubscriptionIntermediateFuture<List<List<String>>> subscriber : subscriptions) {
                // Send settlement details + negotiation invites( + catalogue?) to every subscriber
                subscriber.addIntermediateResultIfUndone(settlements);                                                  // IfUndone is used to ignore errors, when subscription was cancelled during.
            }

            // Remove settledOrders from buyOrders and sellOrders.
            for (Order ro : reservedOrders) {
                if (buyOrders.containsKey(ro)) buyOrders.remove(ro);
                if (sellOrders.containsKey(ro)) sellOrders.remove(ro);
            }
            // Clear reserved orders list for next update tick
            reservedOrders.clear();
            return IFuture.DONE;
        });
    }

    /* --------------- HELPER METHODS ---------- */

    /**
     * Dummy catalogue for testing before proper catalogue file I/O implemented
     */
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

    /**
     * Iterates through class-members LinkedHashMap<Order, String> buyOrders and sellOrders
     * Collects expired orders,
     * Writes negotiation invite for itemType matching orders
     * And clears expired orders.
     * @return settlements - A 2D List of strings, X contains whole negotiation invite, Y are negotiation invite elements.
     */
    private List<List<String>> settleExpiredOrders() {
        List<List<String>> settlements = new ArrayList<List<String>>();
        List<Order> bExpired = new ArrayList<>();
        List<Order> sExpired = new ArrayList<>();
        for (Order b : buyOrders.keySet()) {
            if (reservedOrders.contains(b)) continue;                                                                   // If the buy order has been previously reserved, skip this element
            if (!orderExpired(b, buyOrders.get(b))) continue;                                                           // If the buy order has not yet expired, skip this element
            bExpired.add(b);
            for (Order s : sellOrders.keySet()) {
                if (reservedOrders.contains(s)) continue;                                                               // If the sell order has been previously reserved, skip this element
                if (!orderExpired(s, sellOrders.get(s))) continue;                                                      // If the sell order has not yet expired, skip this element
                sExpired.add(s);
                if (b.getItemType().equals(s.getItemType())) {
                    reservedOrders.add(b);
                    reservedOrders.add(s);
                    settlements.add(negotiationInvite(b, s));                                                           // Send negotiation invites to expired orders whose itemTypes match.
                }
            }
        }
        buyOrders.keySet().removeAll(bExpired);
        sellOrders.keySet().removeAll(sExpired);
        return settlements;
    }

    /**
     * Iterates through class-members LinkedHashMap<Order, String> buyOrders and sellOrders
     * (Expired orders should have already been taken care of and removed from buyOrders and sellOrders before this method is run.)
     * Writes settlement details for perfectly matching orders
     * Otherwise it will write a negotiation invite
     * @return settlements - A 2D List of strings, X contains whole settlements, Y are settlement elements.
     */
    private List<List<String>> settleOrders() {
        System.out.println("[MarketplaceAgent.java] Current buy orders" + buyOrders);
        System.out.println("[MarketplaceAgent.java] Current sell orders" + sellOrders);
        List<List<String>> settlements = new ArrayList<List<String>>();
        for (Order b : buyOrders.keySet()) {
            if (reservedOrders.contains(b))
                continue;                                                                                               // Order has a settlement pair. Move onto next buy order.
            for (Order s : sellOrders.keySet()) {
                if (reservedOrders.contains(s))
                    continue;                                                                                           // Order has a settlement pair. Move onto next set of orders.
                if (b.getSender().equals(s.getSender()))
                    continue;                                                                                           // Orders are from the same sender, skip this pair of orders.
                if (b.getItemType().equals(s.getItemType())) {
                    if (ordersPerfectlyMatch(b, s)) {
                        reservedOrders.add(b);
                        reservedOrders.add(s);
                        settlements.add(settlementNotification(b, s));
                        continue;
                    }
                    // Otherwise, match mandatory attributes.
                    CatalogueItem catItem = catalogue.FindItem(b.getItemType());
                    if (mandatoryAttributesMatch(b, s, catItem)) {
                        // Add to negotiation list.
                        settlements.add(negotiationInvite(b, s));
                        reservedOrders.add(b);
                        reservedOrders.add(s);
                        System.out.println("Mandatory attributes match!");
                    }
                }
            }
        }
        return settlements;
    }

    /**
     * Assumes orders' itemTypes has been matched prior
     * @param bOrder Buy order of type Order
     * @param sOrder Sell order of type Order
     * @return True if all attributes and attribute values + price match perfectly
     */
    private boolean ordersPerfectlyMatch(Order bOrder, Order sOrder) {
        return (bOrder.getAttributes().equals(sOrder.getAttributes()) && bOrder.getPrice() == sOrder.getPrice());
    }

    /**
     * Assumes orders' itemTypes has been matched prior
     * @param bOrder Buy order of type Order
     * @param sOrder Sell order of type Order
     * @param catItem Reference to an Item entry in Catalogue that is equivalent to the itemType of input orders
     * @return True if mandatory attributes match
     */
    private boolean mandatoryAttributesMatch(Order bOrder, Order sOrder, CatalogueItem catItem) {
        List<String> mAttrNames = new ArrayList<>();
        if(catItem == null) {
            System.out.println("[MarketplaceAgent.Java] " + bOrder.getItemType() + " Has no equivalent Catalogue Entry, cannot match Mandatory Attributes.");
            return false;
        }
        for (CatalogueAttribute at : catItem.getMandatoryAttributes()) {
            mAttrNames.add(at.getName());
        }
        if (mAttrNames.isEmpty()) { return false; }                                                                     // Item has no mandatory attributes, cannot be matched, return false.
        if (mAttrNames.size() == 1) {
            String mAttrName = mAttrNames.get(0);
            if (bOrder.getAttributes().containsKey(mAttrName) && sOrder.getAttributes().containsKey(mAttrName)) {
                return bOrder.getAttributes().get(mAttrName).equals(sOrder.getAttributes().get(mAttrName));
            }
        }                                                                               // Item has only one mandatory attribute. Compare values quicker than looping.

        int m = 0;                                                                                                      // let m be the amount of matching mandatory attributes (default 0)
        for (Map.Entry<String, String> at : bOrder.getAttributes().entrySet()) {                                        // for each attribute in the bOrder
            if (!mAttrNames.contains(at.getKey())) {
                continue;
            }                                                                // If this attribute isn't mandatory, skip this attribute
            if (sOrder.getAttributes().containsKey(at.getKey()) && at.getValue().equals(sOrder.getAttributes().get(at.getKey()))) {
                m += 1;                                                                                                 // If sOrder also has this attribute and their value is the same, increment m by one.
            }
        }
        System.out.println("[MarketplaceAgent.mandatoryAttributesMatch]: m = " + m + " mAttrNames.Size() = " + mAttrNames.size());
        // mod m by total count of mandatory attributes
        // if there is no remainder, match is valid
        return m % mAttrNames.size() == 0;
    }

    /**
     * Assumes prerequisites to settlement (matching or agreement) have been met.
     * @param bOrder Buy order of type Order
     * @param sOrder Sell order of type Order
     * @return A list of parsable strings to be broadcast to each agent.
     * Follows structure of ["[SETTLEMENT]", buyer, sender, order details (in JSON format), timestamp]
     */
    private List<String> settlementNotification(Order bOrder, Order sOrder) {
        // [Settlement]
        //  buyOrder and sellOrder senders
        //  item details
        //  price
        //  commission?
        //  time of settlement
        String ord = null;
        try {
            ord = new ObjectMapper().writeValueAsString(bOrder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Arrays.asList(
                "[SETTLEMENT]",
                bOrder.getSender(),
                sOrder.getSender(),
                ord,
                format.format(clock.getTime()));
    }

    /**
     * Assumes prerequisites to negotiation have been met.
     * @param bOrder Buy order of type Order
     * @param sOrder Sell order of type Order
     * @return A list of parsable strings to be broadcast to each agent.
     * Follows structure of ["[NEGOTIATION_INVITE]", buyer, sender, buy order(JSON), sell order(JSON), timestamp, current iteration of negotiation (for use in negotiation deadlines)]
     */
    private List<String> negotiationInvite(Order bOrder, Order sOrder) {
        String bOrdJSON = null;
        String sOrdJSON = null;
        try {
            bOrdJSON = new ObjectMapper().writeValueAsString(bOrder);
            sOrdJSON = new ObjectMapper().writeValueAsString(sOrder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Arrays.asList(
            "[NEGOTIATION_INVITE]",
            bOrder.getSender(),             // Buyer name
            sOrder.getSender(),             // Seller name
            bOrdJSON,                       // Buy offer
            sOrdJSON,                       // Sell offer
            format.format(clock.getTime()), // Timestamp
            "0"                             // Negotiation iteration (For use in deadline)
        );
    }

    /**
     * Checks to see if an order has expired before the current cycle has started.
     * @param order Buy order of type Order
     * @param orderedDate Timestamp of when the order was received (linked with Value of LinkedHashMap buy or sell orders)
     * @return True if the orderedDate timestamp has already passed.
     */
    private boolean orderExpired(Order order, String orderedDate) {
        boolean expired = true;
        Date now = null;
        Date expiry = null;
        try {
            now = format.parse(format.format(clock.getTime()));
            expiry = format.parse(orderedDate);
            expiry.setTime(expiry.getTime() + order.getExpiry());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        expired = expiry.before(now);
        if(expired)
            System.out.println("[MarketplaceAgent] orderExpired : [" + order.getSender() +"] " + order.getItemType());
        return expired;
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
