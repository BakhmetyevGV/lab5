import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class CacheActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Request.class, (pingRequest) -> {
                    Long result = cache.getOrDefault(pingRequest.getTestUrl(), -1L);
                    sender().tell(new Result(pingRequest.getTestUrl(), result), self());
                })
                .match(Result.class, (pingResult) ->
                }
    }
}
