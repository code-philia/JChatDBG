package ChatDBG;

import java.io.File;

/**
 * Starts JDB to debug the target program.
 * Handles the communication between JDB and agent.
 */
public class Debugger {
    private static Debugger instance;

    public static Debugger getInstance() {
        if (instance == null) {
            instance = new Debugger();
        }
        return instance;
    }

    private Debugger() {}

    /**
     * Start JDB to debug the target program.
     */
    public Process run() {
        try{
            String xmlPath = getXMLPath();
            String[] command = {"cmd.exe", "/c", "ant", "-f", xmlPath, "run.with.jdb"};
            ProcessBuilder pb = new ProcessBuilder(command);
            return pb.start();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getXMLPath() {
        String rootPath = System.getProperty("user.dir");
        File file = new File(rootPath, "defects4j.build.xml");
        return file.getAbsolutePath();
    }
}
