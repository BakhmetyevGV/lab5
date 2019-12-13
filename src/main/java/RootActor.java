import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;


public class RootActor extends AbstractActor {

    public ActorRef storeActor = getContext().actorOf(Props.create(StoreActor.class), "storeActor");

    public ActorRef invokeActor = getContext().actorOf(
            new RoundRobinPool(5)
                    .props(Props.create(ScriptRunnerActor.class))
    );

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(TestMessage.class, testMessage -> {
                    for (Test test : testMessage.tests){
                        invokeActor.tell(new TestMessage(
                                    testMessage.packageId,
                                    testMessage.jsScript,
                                    testMessage.functionName,
                                    new Test[]{test}),
                                self()
                        );
                    }
                })
                .match(GetResultMessage.class, message -> {
                    storeActor.tell(message, sender());
                })
                .build();
    }
}
