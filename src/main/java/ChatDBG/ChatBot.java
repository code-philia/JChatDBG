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
        String prompt = Prompt.getInstance().getPrompt(question);
        String[] command = {"cmd.exe", "/c", "python", "src/main/python/chat.py"};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            // TODO: Three problems still happen sometimes and need more testing:
            //  1. Connection reset
            //  2. Spend too much time on connecting to server
            //  3. Spend too much time on waiting for response
            pb.start();
            return askServer(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in ChatDBG.ChatBot.getResponse: " + e.getMessage();
        }
    }

    private String askServer(String question) {
        clientSocket = null;
        initialAsk(question);
        String response = keepReading();
        closeSocket();
        return response;
    }

    private void initialAsk(String question){
        System.out.println("Connecting to server...");
        for(int attempt = 1; attempt <= 10; attempt++){
            try{
                clientSocket = new Socket("localhost", 6789);
                sendMessage(question);
                return;
            }
            catch (java.net.ConnectException e){
                sleepAWhile();
            }
            catch (Exception e){
                System.out.println("Error in ChatDBG.ChatBot.initialAsk: " + e);
            }
        }
        System.out.println("Failed to connect to LLM server.");
    }

    private String keepReading(){
        while(true){
            String response = readMessage();
            String[] parts = response.split(" ");
            if(parts[0].equals("info") && parts.length == 3){
                handleInfo(parts[1], parts[2]);
            }
            else if(parts[0].equals("debug")){
                String command = "";
                if(parts.length > 2){
                    command = parts[1] + " " + parts[2];
                }
                else{
                    command = parts[1];
                }
                handleDebug(command);
            }
            else{
                return response;
            }
        }
    }

    private void handleInfo(String className, String methodName){
        System.out.println("(ChatDBG) "+"info "+className+" "+methodName);
        String info = Functions.getInstance().info(className, methodName);
        System.out.println(info);
        sendMessage(info);
    }

    private void handleDebug(String command){
        System.out.println("(ChatDBG) "+"debug "+command);
        String response = Functions.getInstance().debug(command);
        System.out.println(response);
        sendMessage(response);
    }

    private void sendMessage(String question){
        // System.out.println("Sending message to server...");
        question += "\nEND\n";
        try{
            OutputStream out = clientSocket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out, "GBK");
            PrintWriter printWriter = new PrintWriter(writer, true);
            printWriter.println(question);
        }
        catch (Exception e) {
            System.out.println("Error in ChatDBG.ChatBot.sendMessage: " + e);
        }
    }

    private String readMessage(){
        // System.out.println("Waiting for response...");
        try{
            InputStream in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("END")){
                    break;
                }
                response.append(line);
                response.append("\n");
            }
            if(response.length() > 0 && response.charAt(response.length()-1) == '\n'){
                response.deleteCharAt(response.length()-1);
            }
            return response.toString();
        }
        catch (Exception e){
            return "Error in ChatDBG.ChatBot.readMessage: " + e.getMessage();
        }
    }

    private void sleepAWhile() {
        int retryInterval = 200;
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error in ChatDBG.ChatBot.sleepAWhile: " + e);
        }
    }

    private void closeSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error in ChatDBG.ChatBot.closeSocket: " + e);
            }
        }
    }

    private Socket clientSocket = null;
}
