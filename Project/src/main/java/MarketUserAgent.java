
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.*;

@Description("This agent requires the marketplace service.")
@Agent
@RequiredServices(@RequiredService(name="marketservices", type = IMarketService.class, multiple = true,
        binding = @Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)))
public class MarketUserAgent
{
    @AgentService
    public void addMarketService(IMarketService marketService) {
        ISubscriptionIntermediateFuture<String> subscription = marketService.subscribe();
        while (subscription.hasNextIntermediateResult()) {
            String message = subscription.getNextIntermediateResult();
            System.out.println(message);
        }
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
