package Demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

public class PythonTest {
    /* Run api.py and get returned value */
    public static void main(String[] args) {
        try {
            String[] command = {"cmd.exe", "/c", "python", "src/main/python/api.py"};
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }

            int exitCode = p.waitFor();
            System.out.println("Python process exited with code " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
