package ChatDBG;

import java.io.File;

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
            // For Windows
            String[] command = {"cmd.exe", "/c", "ant", "-f", xmlPath, "run.with.jdb"};
            ProcessBuilder pb = new ProcessBuilder(command);
            return pb.start();
            // For Linux
            // String command = "ant -f " + xmlPath + " run.with.jdb";
            // return Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in ChatDBG.Debugger.run: " + e.getMessage());
            return null;
        }
    }

    private String getXMLPath() {
        String rootPath = System.getProperty("user.dir");
        File file = new File(rootPath, "defects4j.build.xml");
        return file.getAbsolutePath();
    }
}
