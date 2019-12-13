import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Server {
    private AsyncHttpClient httpClient = Dsl.asyncHttpClient();
    private ActorRef cacheActor;

    Server(ActorRef system){
        cacheActor = system.actorOf(Props.create(CacheActor.class));
    }

    Flow<HttpRequest, HttpResponse, NotUsed> getHttpFlow(ActorMaterializer materializer){
        return Flow
                .of(HttpRequest.class)
                .map((request) -> {
                    Query requestQuery = request.getUri().query();
                    String testUrl = requestQuery.gerOrElse("testUrl", "");
                    int count = Integer.parseInt(requestQuery.getOrElse("count", "-1"));

                    return new Request(testUrl, count);
                })
                .mapAsync(6, (pingRequest) -> Patterns.ask(cacheActor, pingRequest, 3000)
                    .thenCompose((result) -> {
                        Result cachePingResult = (Result) result;

                        return cachePingResult.getAvgResponseTime() == -1 ?
                                pingExecute(piActorRefngRequest, materializer)
                                : CompletableFuture.completedFuture(cachePingResult);
                    }))
                .map(result -> {
                    cacheActor.tell(result, ActorRef.noSender());

                    return HttpResponse
                            .create()
                            .withStatus(StatusCodes.OK)
                            .withEntity(
                                    HttpEntities.create(
                                            result.testUrl + " " + result.avgResponseTime
                                    )
                            );
                });
    }
}
