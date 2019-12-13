import akka.actor.ActorRef;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        ActorRef system = ActorRef.create("ping");
    }
}
