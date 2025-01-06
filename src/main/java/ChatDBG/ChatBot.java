package ChatDBG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Used to handle the communication between the ChatDBG and LLM.
 */
public class ChatBot {
    private static ChatBot instance;

    public static ChatBot getInstance() {
        if (instance == null) {
            instance = new ChatBot();
        }
        return instance;
    }

    private ChatBot() {}

    public String getResponse(String question) {
        // TODO: Build a complete prompt based on the question, refer to the implementation of ChatDBG
        String[] command = {"cmd.exe", "/c", "python", pythonScriptPath};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), "GBK"));
            writer.write(question);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
            String line;
            String response = "";
            while ((line = reader.readLine()) != null) {
                response += line;
                response += "\n";
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private final String pythonScriptPath = "src/main/python/chat.py";
}
