import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Main {
    public static void main (String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create("testJS");
        ActorRef rootActor = actorSystem.actorOf(Props.create(RootActor.class), "rootActor");

        Http http = Http.get(actorSystem);

        final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
        HttpRouter httpRouter = new HttpRouter();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                httpRouter.createRoute(rootActor).flow(actorSystem, materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );

        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> actorSystem.terminate());
    }
}
