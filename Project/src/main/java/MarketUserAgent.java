import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@Description("This MarketUserAgent requires the marketplace service.")
@Agent
@RequiredServices(@RequiredService(name = "marketservices", type = IMarketService.class, multiple = true,
        binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    private String agentName;
    private Catalogue catalogue;
    private ArrayList<CatalogueItem> inventory = new ArrayList<>();
    private ArrayList<Order> currentOrders = new ArrayList<>();

/*
TODO : Add method to populate ArrayList<Item> inventory from some unique inventory/profile file on disk, load on startup (@AgentCreated)?,
 Maybe "Profiles" under : https://www.activecomponents.org/download/docs/releases/jadex-3.0.76/jadex-mkdocs/tutorials/ac/05%20Provided%20Services/
*/

    /**
     * Agent's main body, will execute when agent's life begins.
     */
    @AgentBody
    public void body(IInternalAccess agent) throws JsonProcessingException {
//TODO: BEGIN Debug code for before File I/O added, Replace later with proper File I/O implementation.
        agentName = agent.getComponentIdentifier().getName();
        HashMap<String, String> phoneAttributes = new HashMap<>();
        phoneAttributes.put("make_model", "App_iPhone11");
        phoneAttributes.put("battery_capacity", "2500");
        phoneAttributes.put("colour", "Red");
        HashMap<String, String> item2Attributes = new HashMap<>();
        item2Attributes.put("make_model", "Toy_Camry");
        item2Attributes.put("Year", "1999");
        item2Attributes.put("Km", "150000");

        Order order1 = new Order(agentName, OrderType.Buy, "Phone", phoneAttributes, 100);
        Order order2 = new Order(agentName, OrderType.Sell, "Used_car", item2Attributes, 50);
        System.out.println(order1.PrettyPrint());
        String orderJsonString1 = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order1);
        String orderJsonString2 = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order2);
        String[] orders = new String[]{orderJsonString1, orderJsonString2};
        //System.out.println(orderJsonString1);
        //System.out.println(orderJsonString2);

        // currentOrders.add(order1);
        // currentOrders.add(order2);

//TODO: END Debug code for before File I/O added, Replace later with proper File I/O implementation.
        /* Sends orders[] (array of json structured strings) to MarketService agent, and wait for result.
         * returns "accepted" when order is accepted,
         * send result to OrderConfirmation(result). */
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.getCatalogue().addResultListener(catResult -> {catalogue = catResult;});
                iMarketService.addOrders(orders).addResultListener(orderResult -> {
                    OrderConfirmation(orderResult);
                });
            }
        });
    }

    /**
     * Subscribes to the MarketService "mailing list"
     * so it will receive settlement details every 10s
     */
    @AgentService
    public void addMarketService(IMarketService marketService) {
        ISubscriptionIntermediateFuture<String> subscription = marketService.subscribe();
        while (subscription.hasNextIntermediateResult()) {
            String message = subscription.getNextIntermediateResult();
            System.out.println(message);
        }
    }

    /* --------------- HELPER METHODS ---------- */

    //TODO: Add logic after confirmation, or confirmation check if necessary.
    private void OrderConfirmation(String conf) {
        System.out.println("Order " + conf);
    }

}
