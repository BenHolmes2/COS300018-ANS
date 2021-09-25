
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.ArrayList;

/**
 *  Simple service to publish the local system time.
 *  As the service does not change the local system
 *  and provides no sensitive information, no security
 *  restrictions are required.s
 *  TAKEN FROM TIME SERVICE EXAMPLE
 */
@Security(Security.UNRESTRICTED)
public interface IMarketService
{
    /*
        Subscribe to market service.
        Every 10 seconds, release settlement details for matching orders
    */
//TODO: Find out what future type to use to send messages. Use custom ACL based type?
    ISubscriptionIntermediateFuture<String> subscribe();

    IFuture<String> addOrders(String orders);

    /*
        Reference to catalogue of items to be shared by all agents using this service.
        For now, will not change unless service is reinitialised.
     */
    Catalogue GetCatalogue();
}
