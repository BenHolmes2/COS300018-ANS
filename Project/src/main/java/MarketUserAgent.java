
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Description("This MarketUserAgent requires the marketplace service.")
@Agent
@RequiredServices(@RequiredService(name="marketservices", type = IMarketService.class, multiple = true,
        binding = @Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent
{
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    private String hasad;
    private ArrayList<Item> inventory = new ArrayList<Item>();
    private ArrayList<Order> currentOrders = new ArrayList<Order>();
/*
TODO : Add method to populate ArrayList<Item> inventory from some unique inventory/profile file on disk, load on startup (@AgentCreated)?,
 Maybe "Profiles" under : https://www.activecomponents.org/download/docs/releases/jadex-3.0.76/jadex-mkdocs/tutorials/ac/05%20Provided%20Services/
*/

    /**
     * Agent's main body, will execute when agent's life begins.
     *
     */
    @AgentBody
    public void body(IInternalAccess agent) {


//TODO: BEGIN Debug objects for before File I/O added, Replace later.

        hasad = agent.getComponentIdentifier().getName();

        Attribute attribute = new Attribute("Make_model", AttributeType.Categorical, true, Arrays.asList("asd"), false);
        Item item = new Item("Used Car", Arrays.asList(attribute));
        Order order = new Order(agent.getComponentIdentifier().getName(), OrderType.Buy, item, 100);
        String orderToString = order.toString();
        //System.out.println(orderToString);
        currentOrders.add(order);

//TODO: END Debug objects for before File I/O added, Replace later.

        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        // Use a listener for the service result.
        fut.addResultListener(new DefaultResultListener<IMarketService>(){
            @Override
            public void resultAvailable(IMarketService iMarketService){
                iMarketService.addOrders(orderToString).addResultListener(result -> {OrderConfirmation(result);});
                // Send currentOrder w/o recipients to MarketService agent
                // MarketService resolves orders, and sends back office circular (bulletin board) style
                // iMarketService.AddOrder(currentOrder).addResultListener(orderList -> ResolveOrder(orderList));
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

// Add logic after confirmation, or confirmation check if necessary.
    private void OrderConfirmation(String conf){
        System.out.println("Order " + conf);
    }

    /**
     *  Start a Jadex platform and the UserAgent.
     */
    public static void  main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setNetworkName("102326287");
        config.setNetworkPass("102326287");
        config.addComponent(MarketUserAgent.class);
        config.setAwareness(true);
        config.setGui(false);
        Starter.createPlatform(config).get();
    }
}
