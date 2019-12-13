import

import akka.stream.StreamRefMessages;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        StreamRefMessages.ActorRef system = StreamRefMessages.ActorRef.create("ping");
    }
}
