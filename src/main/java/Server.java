import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import scala.Int;

import javax.management.Query;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
    }
}
