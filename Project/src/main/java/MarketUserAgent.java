import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent with a GUI which uses MarketService.
 * Allows loading and sending of Orders to a MarketService agent.
 */
@Description("This MarketUserAgent requires the marketplace service.")
@Agent
@RequiredServices(
        @RequiredService(name = "marketservices", type = IMarketService.class, multiple = true, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    private String agentName;
    private Catalogue catalogue = null;                                                                                 // Locally stored copy of MarketplaceAgent's catalogue
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Order> currentOrders = new ArrayList<>();

    private MarketUserAgentGUI gui;                                                                                     // GUI to be created on startup

    /**
     * Will run once on creation of the agent/component (thru JCC or code).
     * Starts up GUI
     */
    @AgentCreated
    public void created(IInternalAccess agent) {
        agentName = agent.getComponentIdentifier().getName();
        // Create GUI
        final IExternalAccess exta = agent.getExternalAccess();
        gui = new MarketUserAgentGUI(exta, this);
    }

    /**
     * Subscribes to the MarketService "mailing list"
     * so it will receive settlement details every 10s
     */
    @AgentService
    public void AddMarketService(IMarketService marketService) {
        ISubscriptionIntermediateFuture<List<List<String>>> subscription = marketService.subscribe();
        while (subscription.hasNextIntermediateResult()) {
            List<List<String>> settlementsMessage = subscription.getNextIntermediateResult();
            parseSettlements(settlementsMessage);
            //List<List<String>> message = subscription.getNextIntermediateResult();
            //System.out.println(agentName + " | [MarketUserAgent.java] Settlement Details : " + message);
        }
    }

    /**
     * Runs on agent quit/crash, disposes of GUI window
     */
    @AgentKilled
    public void agentKilled() {
        SwingUtilities.invokeLater(() -> gui.dispose());
    }

    /* --------------- HELPER METHODS ---------- */

    private void parseSettlements(List<List<String>> settlements) {
        for(List<String> msg : settlements) {
            String msgType = msg.get(0);            // [SETTLEMENT] or [NEGOTIATION_INVITE]
            String buyerName = msg.get(1);
            String sellerName = msg.get(2);
            if(buyerName.equals(agentName) || sellerName.equals(agentName)) {
                if(msgType.equals("[SETTLEMENT]")) {
                    System.out.println(agentName + " | [MarketUserAgent.java] Settlement received : " + msg);
                } else if (msgType.equals("[NEGOTIATION_INVITE]")) {
                    System.out.println(agentName + " | [MarketUserAgent.java] Negotiation invite received : " + msg);
                }
                gui.addSettlement(new ArrayList<>(msg));
            }
        }
    }

    /**
     * Requests catalogue from MarketplaceAgent/service and waits for result.
     * Sends result to catalogueReceived.
     */
    public void requestCatalogue() {
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        /* Requests a catalogue String from MarketplaceAgent, and wait for result.
         * returns Json structured String representation of catalogue object(catalogueResult),
         * send result to catalogueReceived(catalogueResult),
         * which will Deserialise it into a Catalogue Object. */
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.getCatalogue().addResultListener(catalogueResult -> {
                    catalogueReceived(catalogueResult);
                });
            }
        });
    }

    /**
     * Receives catalogue from MarketplaceAgent/service
     * Assigns into locally stored catalogue.
     * @param catalogueResult JSON string format of Catalogue from MarketplaceAgent.
     */
    private void catalogueReceived(String catalogueResult) {
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
            gui.refreshCatalogue(catalogue, this);
            System.out.println(agentName + " | [MarketUserAgent.java] Catalogue Received.");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send orders[] (array of json structured strings) to MarketService agent, and wait for result.
     * Send result to orderConfirmation(result).
     * @param orders Array of Strings, where each string is a JSON formatted Order object.
     */
    public void sendOrder(String[] orders) {
        IFuture<IMarketService> fut = requiredServicesFeature.getRequiredService("marketservices");
        fut.addResultListener(new DefaultResultListener<IMarketService>() {
            @Override
            public void resultAvailable(IMarketService iMarketService) {
                iMarketService.addOrders(orders).addResultListener(orderResult -> {
                    orderConfirmation(orderResult);
                });
            }
        });
    }

    /**
     * Reads array of orders from a file
     * @param filePath Array of Strings, where each string is a JSON formatted Order object.
     * @return Array of Strings, where each string is a JSON formatted Order object.
     */
    public String[] readOrders(String filePath) throws IOException {
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
    /**
     * Method runs once MarketplaceAgent/service confirms that an order has been received successfully
     * @param conf Confirmation message from MarketplaceAgent.
     */
    private void orderConfirmation(String conf) {
        System.out.println(agentName + " | [MarketUserAgent.java] Order(s) ? " + conf);
    }

    public Catalogue getCatalogue() {
        return this.catalogue;
    }
}
