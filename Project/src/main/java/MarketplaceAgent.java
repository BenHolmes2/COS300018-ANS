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
    protected List<Order> buyOrders;
    protected List<Order> sellOrders;
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

    public IFuture<Catalogue> getCatalogue() {
        return new Future<>(catalogue);
    }

    public IFuture<String> addOrders(String[] orders) {
        try {
            //  Deserialise Json orderString into Order Object
            for (int i = 0; i < orders.length; i++) {
                Order orderTemp = new ObjectMapper().readValue(orders[i], Order.class);
                //System.out.println("[MarketplaceAgent.java] ORDER OBJECT [" + i + "]: " + orderTemp.Print());
                //  Serialise Order object into Json orderString
                String orderJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(orderTemp);
                //System.out.println("[MarketplaceAgent.java] ORDER JSON [" + i + "]: " + orderJsonString);
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
        /*try {
            DEBUG_Catalogue();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
        CatalogueAttribute attr1 = new CatalogueAttribute("Make_Model", AttributeType.Categorical, true, null, attr1_domain, false);
        CatalogueAttribute attr2 = new CatalogueAttribute("Battery_Capacity", AttributeType.Quality, false, null, attr2_domain, true);
        CatalogueAttribute attr3 = new CatalogueAttribute("ORDER STYLE ATTRIBUTE", AttributeType.Quality, false, "[MarketUserAgent.java] VALUE_VALUE_VALUE", null, true);
        CatalogueItem catalogueItem = new CatalogueItem("Phone", Arrays.asList(attr1, attr2, attr3));

        String itemJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(catalogueItem);
        System.out.println(itemJsonString);
        CatalogueItem tempCatalogueItem = new ObjectMapper().readValue(itemJsonString, CatalogueItem.class);
        System.out.println(tempCatalogueItem.PrettyPrint());
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
