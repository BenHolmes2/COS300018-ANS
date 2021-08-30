import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import java.util.Date;

/**
 *  Micro agent that gets uses the ClockService.
 */
@Description("This agent declares a required clock service.")
@Agent
@RequiredServices(@RequiredService(name="clockservice", type= IClockService.class,
        binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class F1Agent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;
    /**
     * Get the clock service from the required services feature and print out
     * the time. This
     * @param agent
     */
    @AgentBody
    public void body (IInternalAccess agent) {
        IFuture<IClockService> fut = requiredServicesFeature.getRequiredService("clockservice");
        // -- Using a listener for the service result.
        fut.addResultListener(new DefaultResultListener<IClockService>() {
            public void resultAvailable(IClockService cs) {
                System.out.println("Time for a chat, buddy: " + new Date(cs.getTime()));
            }
        });
        /* As an alternative to the above where a listener is added you can
        wait for the invoked service to return the result */
//        IClockService service = fut.get();
//        System.out.println("Time for a chat, buddy: " + new Date(service.getTime()));
    }

    /**
     * A simple JadeX platform to run out agent.
     * @param args Not used.
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.addComponent(F1Agent.class);
        Starter.createPlatform(config).get();
    }
}