import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Description("This MarketUserAgent requires the marketplace service.")
@Agent
@RequiredServices(
        @RequiredService(name = "marketservices", type = IMarketService.class, multiple = true, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;
    private DateFormat format;
    private IClockService clock;

    private String agentName;
    private Catalogue catalogue = null;
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Order> currentOrders = new ArrayList<>();

    AgentCreateGUI gui;

    protected String orderItemType;
    protected String ordersFilePath;

    @AgentCreated
    public void created(IInternalAccess agent) {
        agentName = agent.getComponentIdentifier().getName();
    }

    /**
     * Agent's main body, will execute when agent's life begins.
     *
     */
    @AgentBody
    public void body(IInternalAccess agent) throws JsonProcessingException {
        // Create GUI
        final IExternalAccess exta = agent.getExternalAccess();
        gui = new AgentCreateGUI(exta, this);


        // Write object
        // Catalogue request function called within GUI action                                                          (RequestCatalogue())
        // Send order function called within GUI action                                                                 (SendOrders(String[] orders))

//TODO: BEGIN Debug code for before File I/O added, Replace later with proper File I/O implementation.
        HashMap<String, String> phoneAttributes = new HashMap<>();
        phoneAttributes.put("make_model", "App_iPhone11");
        phoneAttributes.put("battery_capacity", "2500");
//        phoneAttributes.put("colour", "Red");
        HashMap<String, String> item2Attributes = new HashMap<>();
        item2Attributes.put("make_model", "Toy_Camry");
        item2Attributes.put("Year", "1999");
//        item2Attributes.put("Km", "150000");


        Order order1 = new Order(agentName, OrderType.Buy, "Phone", phoneAttributes, 100);
        Order order2 = new Order(agentName, OrderType.Sell, "Used_car", item2Attributes, 50);

        String orderJsonString1 = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order1);
        String orderJsonString2 = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(order2);
        System.out.println(orderJsonString2);
        String[] orders = new String[]{orderJsonString1, orderJsonString2};
        //System.out.println(orderJsonString1);
        //System.out.println(orderJsonString2);
//TODO: END Debug code for before File I/O added, Replace later with proper File I/O implementation.

    }

    /**
     * Subscribes to the MarketService "mailing list"
     * so it will receive settlement details every 10s
     */
    @AgentService
    public void addMarketService(IMarketService marketService) {
        ISubscriptionIntermediateFuture<List<List<String>>> subscription = marketService.subscribe();
        while (subscription.hasNextIntermediateResult()) {
            List<List<String>> message = subscription.getNextIntermediateResult();
            System.out.println(agentName + " | [MarketUserAgent.java] " + message);
        }
    }

    @AgentKilled
    public void agentKilled() {
// TODO: KILL GUI
        // SwingUtilities.invokeLater(() -> gui.dispose());
        SwingUtilities.invokeLater(() -> gui.dispose());
    }

    /* --------------- HELPER METHODS ---------- */

    public void RequestCatalogue() {
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        /* Requests a catalogue String from MarketplaceAgent, and wait for result.
         * returns Json structured String representation of catalogue object(catalogueResult),
         * send result to CatalogueReceived(catalogueResult),
         * which will Deserialise it into a Catalogue Object. */
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.getCatalogue().addResultListener(catalogueResult -> {
                    CatalogueReceived(catalogueResult);
                });
            }
        });
    }
    /* Send orders[] (array of json structured strings) to MarketService agent, and wait for result.
     * returns "accepted" when order is accepted,
     * send result to OrderConfirmation(result).
     * */
    public void SendOrder(String[] orders) {
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.addOrders(orders).addResultListener(orderResult -> {
                    OrderConfirmation(orderResult);
                });
            }
        });
    }

    private void CatalogueReceived(String catalogueResult) {
        if (catalogueResult == null) {
            System.out.println("[MarketUserAgent] Catalogue Result received from MarketplaceAgent is null!");
            return;
        }
        try {
            Catalogue cat = new ObjectMapper().readValue(catalogueResult, Catalogue.class);
            if (cat.GetCatalogue() == null) {
                System.out.println("[MarketUserAgent] Catalogue is null!");
                return;
            }
            List<CatalogueItem> catalogueItems = cat.GetCatalogue();
            if (catalogueItems.size() == 0) {
                System.out.println("Catalogue list is empty!");
                return;
            }

            System.out.println("---------- " + agentName + " RECEIVED CATALOGUE----------");
            catalogue = cat;
          for (CatalogueItem item : catalogueItems) { System.out.println(item.PrettyPrint()); }
            System.out.println("-------END " + agentName + " RECEIVED CATALOGUE---------");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void WriteInventory() {

    }

    private void WriteOrders() {

    }

    private ArrayList<Order> ReadOrders(String filePath) throws IOException {
        ArrayList<Order> orders = new ArrayList<>(Arrays.asList(new ObjectMapper().readValue(Paths.get(filePath).toFile(), Order[].class)));
        for(Order o : orders) {
            o.setSender(agentName);
        }
        return orders;
    }

    //TODO: Add logic after confirmation, or confirmation check if necessary.
    private void OrderConfirmation(String conf) {
        System.out.println("Order " + conf);
    }

}
