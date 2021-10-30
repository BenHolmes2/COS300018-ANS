import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
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
        // Create GUI
        final IExternalAccess exta = agent.getExternalAccess();
        gui = new AgentCreateGUI(exta, this);
    }

    /**
     * Agent's main body, will execute when agent's life begins.
     */
    @AgentBody
    public void body(IInternalAccess agent) {

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
            System.out.println(agentName + " | [MarketUserAgent.java] Settlement Details : " + message);
        }
    }

    @AgentKilled
    public void agentKilled() {
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

    private void CatalogueReceived(String catalogueResult) {
        if (catalogueResult == null) {
            System.out.println(agentName + " | [MarketUserAgent.java] Catalogue Result received from MarketplaceAgent is null!");
            return;
        }
        try {
            Catalogue cat = new ObjectMapper().readValue(catalogueResult, Catalogue.class);
            if (cat.GetCatalogue() == null) {
                System.out.println(agentName + " | [MarketUserAgent.java] Catalogue is null!");
                return;
            }
            List<CatalogueItem> catalogueItems = cat.GetCatalogue();
            if (catalogueItems.size() == 0) {
                System.out.println(agentName + " | [MarketUserAgent.java] Catalogue list is empty!");
                return;
            }

            catalogue = cat;
            System.out.println(agentName + " | [MarketUserAgent.java] Catalogue Received.");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send orders[] (array of json structured strings) to MarketService agent, and wait for result.
     * send result to OrderConfirmation(result).
     */
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

    public String[] ReadOrders(String filePath) throws IOException {
        final ObjectMapper om = new ObjectMapper();
        ArrayList<String> oStrings = new ArrayList<>();
        JsonNode arrayNode = om.readTree(Paths.get(filePath).toFile());
        if (arrayNode.isArray()) {
            for (JsonNode node : arrayNode) {
                Order o = new ObjectMapper().readValue(node.toPrettyString(), Order.class);
                o.setSender(agentName);
                oStrings.add(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o));
            }
            return oStrings.toArray(new String[0]);
        }
        return null;
    }

    // TODO: Add logic after confirmation, or confirmation check if necessary.
    private void OrderConfirmation(String conf) {
        System.out.println(agentName + " | [MarketUserAgent.java] Order(s) ? " + conf);
    }
}
