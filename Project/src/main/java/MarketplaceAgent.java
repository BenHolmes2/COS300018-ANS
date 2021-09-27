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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Agent
@Service
@ProvidedServices(@ProvidedService(type = IMarketService.class))
public class MarketplaceAgent implements IMarketService {
    //TODO: Add the path to the catalogue source file.
    protected Catalogue catalogue = new Catalogue("PathToCatalogueSourceFile");

    protected Set<SubscriptionIntermediateFuture<String>> subscriptions = new LinkedHashSet<SubscriptionIntermediateFuture<String>>();

    public Catalogue GetCatalogue() {
        return null;
    }

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

    public IFuture<String> addOrders(String orders) {
//        System.out.println(orders.size());
//        String sender = orders.get(0).getSender();
//        System.out.println(sender);
        System.out.println(orders);
        return new Future<>("accepted");
    }

    /* --------------- AGENT LIFE CYCLE ---------- */

    /**
     * Due to annotation, called once after agent is initialized.
     * The internal access parameter is optional and is injected automatically.
     * Every 10 seconds,
     * 1) Match received orders,
     * 2) Check expired orders,
     * 3) Send settlement details,
     * 4) Send negotiation invites.
     */
    @AgentBody
    public void body(IInternalAccess ia) {
        IExecutionFeature exe = ia.getComponentFeature(IExecutionFeature.class); // Execution feature provides methods for controlling the execution of the agent.
        System.out.println("Marketplace Agent Started.");
        exe.repeatStep(10000 - System.currentTimeMillis() % 10000, 10000, ia1 -> {
            // Notify all subscribers
            for (SubscriptionIntermediateFuture<String> subscriber : subscriptions) {
//TODO: Send settlement details + negotiation details( + catalogue?) to every subscriber
                subscriber.addIntermediateResultIfUndone("Wao!");   // IfUndone is used to ignore errors, when subscription was cancelled during.
            }
            return IFuture.DONE;
        });
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
