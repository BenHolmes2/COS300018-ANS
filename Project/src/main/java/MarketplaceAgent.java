import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.*;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.IOException;
import java.util.*;

@Agent
@Service
@ProvidedServices(@ProvidedService(type = IMarketService.class))
public class MarketplaceAgent implements IMarketService {
    //TODO: Add the path to the catalogue source file.
    protected Catalogue catalogue = new Catalogue("PathToCatalogueSourceFile");
    protected List<Order> buyOrders = new ArrayList<>();
    protected List<Order> sellOrders = new ArrayList<>();
    protected Set<SubscriptionIntermediateFuture<String>> subscriptions = new LinkedHashSet<SubscriptionIntermediateFuture<String>>();


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
                if(order.getOrderType() == OrderType.Buy) {
                    buyOrders.add(order);
                } else {
                    sellOrders.add(order);
                }
                System.out.println("[MarketplaceAgent.java] " + (order.getOrderType() == OrderType.Buy ? "BUY ORDER" : "SELL ORDER") + " FROM [" + order.getSender() + "] RECEIVED[" + i + "] FOR " + order.getItemType());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new Future<>("accepted");
    }

    /* --------------- AGENT LIFE CYCLE ---------- */

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

        exe.repeatStep(10000 - System.currentTimeMillis() % 10000, 10000, ia1 -> {
            CheckExpiredOrders();
            MatchOrders();
            // Send settlement details to all subscribers
            for (SubscriptionIntermediateFuture<String> subscriber : subscriptions) {
                //TODO: Send settlement details + negotiation invites( + catalogue?) to every subscriber
                subscriber.addIntermediateResultIfUndone("Wao!");   // IfUndone is used to ignore errors, when subscription was cancelled during.
            }
            return IFuture.DONE;
        });
    }

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
        System.out.println(phoneItemDeserialized.PrettyPrint());
        System.out.println("---------------------END MARKETPLACE AGENT-------------------------");
*/
        catalogue.AddItem(phoneItem);
        catalogue.AddItem(carItem);

        String catalogueToJson  = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogue);
        // System.out.println(catalogueToJson);
        Catalogue catalogueFromJSon = new ObjectMapper().readValue(catalogueToJson, Catalogue.class);
        List<CatalogueItem> items = catalogueFromJSon.GetCatalogue();
        // items.forEach(catalogueItem -> System.out.println(catalogueItem.PrettyPrint()));
    }

    private void CheckExpiredOrders() {

    }

    private void MatchOrders() {
        /*
            for (Order bOrder : buyOrders){
                boolean found = false;
                Order buy;
                Order sell;
                for (Order sOrder : sellOrders) {
                    if(bOrder == sOrder){
                        buy = bOrder;
                        sell = sOrder;
                        found= true;
                    }
                    if(found){break;}
                }
                if(found){
                    //pass data to relevant orders using buy and sell order variables
                }
            }*/
    }

    /* --------------- HELPER METHODS ---------- */

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
