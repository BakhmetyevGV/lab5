import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    public Map<String, ArrayList<TestResult>>  storage = new HashMap<>();


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(TestResult.class, msg -> {
                    storage.computeIfAbsent(msg.packageId, k -> new ArrayList<>());
                    storage.get(msg.packageId).add(msg);
                })
                .match(GetResultMessage.class, msg ->{
                    ArrayList<TestResult> results = storage.get(msg.packageId);

                    if(results != null){
                        results.sort(Comparator.comparing(TestResult::getTestName));
                        ResponseMessage responseMessage = new ResponseMessage(
                                msg.packageId,
                                results.toArray(new TestResult[0]));

                        sender().tell(responseMessage, self());
                    } else{
                        sender().tell("no data", self());
                    }
                })
                .build();
    }
}
