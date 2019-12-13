import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.*;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
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
                                pingExecute(pingRequest, materializer)
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

    private CompletionStage<Result> pingExecute(Request request, ActorMaterializer materializer) {
        return Source
                .from(Collections.singletonList(request))
                .toMat(pingSink(), Keep.right())
                .run(materializer)
                .thenApply((sumTime) -> new Result(
                        request.testUrl,
                        sumTime / request.count / 3000)
                );
    }

    private Sink<Request, CompletionStage<Long>> pingSink() {
        return Flow.<Request>create()
                .mapConcat((pingRequest) -> Collections.nCopies(pingRequest.count, pingRequest.testUrl))
                .mapAsync(6, (url) -> {
                    long startTime = System.nanoTime();

                    return httpClient
                            .prepareGet(url)
                            .execute()
                            .toCompletableFuture()
                            .thenApply((response) -> System.nanoTime() - startTime);
                })
                .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

}
