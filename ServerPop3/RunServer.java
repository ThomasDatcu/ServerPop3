package ServerPop3;

import java.io.IOException;

public class RunServer {
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.run();
    }

}
