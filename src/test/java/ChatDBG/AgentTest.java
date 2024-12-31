package ChatDBG;

import java.io.File;

public class AgentTest {
    public static void main(String[] args) {
        Agent agent = new Agent();
        agent.run();
        System.out.println("Agent finish running.");
        System.exit(0);
    }
}
