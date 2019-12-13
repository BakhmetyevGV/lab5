import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptRunnerActor extends AbstractActor {
    private String STORE_ACTOR_PATH = "/user/rootActor/storeActor";


    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(TestMessage.class, msg ->{
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                    Test test = msg.tests[0];
                    String result;

                    try{
                        engine.eval(msg.jsScript);
                        Invocable invocable = (Invocable) engine;
                        Object[] params = test.params;
                        result = invocable.invokeFunction(msg.functionName, params).toString();

                    } catch (Exception e){
                        System.out.println(e.getMessage());
                        result = "RUNTIME EXCEPTION";
                    }

                    getContext().actorSelection(STORE_ACTOR_PATH)
                            .tell(new TestResult(
                                    msg.packageId,
                                    test.testName,
                                    result.equals(test.expectedResult),
                                    result,
                                    test.expectedResult,
                                    test.params
                            ), self());

                })
                .build();
    }
}
