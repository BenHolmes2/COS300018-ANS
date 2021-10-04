import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

@Description("This MarketUserAgent requires the marketplace service.")
@Agent
@RequiredServices(@RequiredService(name = "marketservices", type = IMarketService.class, multiple = true,
        binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    private String agentName;
    private ArrayList<Item> inventory = new ArrayList<>();
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
        Order order = new Order(agentName, OrderType.Buy, "Description of item!", 100);
        String orderJsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order);
        currentOrders.add(order);
//TODO: END Debug code for before File I/O added, Replace later with proper File I/O implementation.

        /* Sends order as semi-structured plaintext to MarketService agent, and wait for result.
         * returns "accepted" when order is accepted,
         * send result to OrderConfirmation(result). */
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.addOrders(orderJsonString).addResultListener(result -> {
                    OrderConfirmation(result);
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


    /**
     * Start a Jadex platform and the UserAgent.
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setNetworkName("102326287");
        config.setNetworkPass("102326287");
        config.addComponent(MarketUserAgent.class);
        config.setAwareness(true);
        config.setGui(false);
        Starter.createPlatform(config).get();
    }
}
