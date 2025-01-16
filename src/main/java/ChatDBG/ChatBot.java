package ChatDBG;

import java.io.*;
import java.net.Socket;

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
        // TODO: Add function calling ability of the LLM
        //  How to implement:
        //  1. the answer by gpt has "tool_calls" field, which is a list of function calls
        //  2. refer to the implementation in ChatDBG to see how to give the result of function calling back to LLM
        // TODO: Use prompt to replace question when everything ready
        // debug
        String prompt = Prompt.getInstance().getPrompt(question);
        System.out.println(prompt);
        // debug
        String[] command = {"cmd.exe", "/c", "python", "src/main/python/chat.py"};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            pb.start();
            return askServer(question);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String askServer(String question) {
        clientSocket = null;
        initialAsk(question);
        String response = readMessage();
        closeSocket();
        return response;
    }

    private void initialAsk(String question){
        for(int attempt = 1; attempt <= 5; attempt++){
            try{
                clientSocket = new Socket("localhost", 6789);
                sendMessage(question);
                return;
            }
            catch (java.net.ConnectException e){
                sleepAWhile();
            }
            catch (Exception e){
                System.out.println("Error when attempting to send command: " + e);
            }
        }
    }

    private void sendMessage(String question) throws Exception {
        OutputStream out = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);
        writer.println(question);
    }

    private String readMessage(){
        try{
            InputStream in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            return response.toString();
        }
        catch (Exception e){
            return "Error: " + e.getMessage();
        }
    }

    private void sleepAWhile() {
        int retryInterval = 200;
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error when closing socket: " + e);
            }
        }
    }

    private Socket clientSocket = null;
}
