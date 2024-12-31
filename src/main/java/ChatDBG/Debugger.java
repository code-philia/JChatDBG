package ChatDBG;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

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
    public void run() {
        // Run JDB in a seperate thread to avoid blocking the main thread
        Thread jdbThread = new Thread(() -> {
            try {
                String xmlPath = getXMLPath();
                String[] command = {"cmd.exe", "/c", "ant", "-f", xmlPath, "run.with.jdb"};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("JDB process exited with code " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        jdbThread.start();
    }

    private String getXMLPath() {
        String rootPath = System.getProperty("user.dir");
        File file = new File(rootPath, "defects4j.build.xml");
        return file.getAbsolutePath();
    }
}
