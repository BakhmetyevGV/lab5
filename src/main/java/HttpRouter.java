import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import scala.concurrent.Future;

class HttpRouter extends AllDirectives {
    private static final String PARAMETER_PACKAGE_ID = "packageId";
    private static final int TIMEOUT = 5000;

    HttpRouter() {
    }

    Route createRoute(ActorRef rootActor) {
        return route(
                path("test", () ->
                        post(() ->
                                entity(Jackson.unmarshaller(TestMessage.class), msg -> {
                                    rootActor.tell(msg, ActorRef.noSender());
                                    return complete("listening to localhost:8080");
                                })
                        )
                ),
                path("result", () ->
                        get(() ->
                                parameter(PARAMETER_PACKAGE_ID, packageId -> {
                                    Future<Object> result = Patterns.ask(rootActor,
                                            new GetResultMessage(packageId),
                                            TIMEOUT);
                                    return completeOKWithFuture(result, Jackson.marshaller());
                                })
                        )
                )
        );
    }
}