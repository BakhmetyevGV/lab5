import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {


    public Map<String, Long> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Request.class, (pingRequest) -> {
                    Long result = cache.getOrDefault(pingRequest.testUrl, -1L);
                    sender().tell(new Result(pingRequest.testUrl, result), self());
                })
                .match(Result.class, (pingResult) ->
                        cache.put(pingResult.testUrl, Result.avgResponseTime)
                )
                .build();
    }
}