import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.List;

@Security(Security.UNRESTRICTED)
public interface IMarketService {
    /*
        Subscribe to market service.
        Every 10 seconds, release settlement details for matching orders
    */
//TODO: Find out what future type to use to send messages. Use custom ACL based type?
    ISubscriptionIntermediateFuture<List<List<String>>> subscribe();

    IFuture<String> addOrders(String[] orders);

    IFuture<String> getCatalogue();
}
