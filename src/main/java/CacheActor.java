import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class CacheActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(PingRequest.class, (pingRequest) -> {
                    Long result = cache.getOrDefault(pingRequest.getTestUrl(), -1L);
                }
    }
}
