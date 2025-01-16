package ChatDBG;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The main body of ChatDBG.
 */
public class Agent {

    private static Agent instance;

    public static Agent getInstance() {
        if (instance == null) {
            instance = new Agent();
        }
        return instance;
    }

    public Agent(){
        constants = Constants.getInstance();
        debugger = Debugger.getInstance();
        chatbot = ChatBot.getInstance();
    }

    public void run(){
        jdbServer = debugger.run();
        try{
            Thread.sleep(4000); // Wait for JDB to start
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        runloop();
    }

    private void runloop() {
        printInstructions();
        jdbConnection = connectToJDB();
        Scanner scanner = new Scanner(System.in);
        while(true){
            try{
                if(sendCommand(scanner)==0) {
                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
        scanner.close();
    }

    private void printInstructions(){
        String border = "----------------------------------------";
        System.out.println(border);
        System.out.println("Welcome to JChatDBG!");
        System.out.println("You're now debugging "+constants.testEntryClass+"::"+constants.testEntryMethod+" in "+constants.repo+"\\"+constants.name+"\\"+constants.id);
        System.out.println("You can send debugging commands just as you do in JDB.");
        System.out.println("You can also ask any question about the debugging process.");
        System.out.println("Type 'exit' to close the connection.");
        System.out.println(border);
    }

    private Process connectToJDB(){
        int port = constants.port;
        String[] command = {"cmd.exe", "/c", "jdb", "-connect", "com.sun.jdi.SocketAttach:hostname=localhost,port="+port};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            readResponse(p.getInputStream()); // Don't need to print the response here, just read and discard it
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int sendCommand(Scanner scanner) throws Exception {
        System.out.print("Command or Question: ");
        String inputCommand = scanner.nextLine();
        if(inputCommand.equals("exit")){
            System.out.println("Connection closed.");
            jdbConnection.destroy();
            jdbServer.destroy();
            return 0;
        }
        boolean isJDBCommand = false;
        String command = inputCommand.split(" ")[0];
        if(constants.commands.contains(command)){
            isJDBCommand = true;
        }
        if(isJDBCommand){
            printResponse(getResponse(inputCommand));
        }
        else{
            printResponse(chatbot.getResponse(inputCommand));
            clearHistory();
        }
        return 1;
    }

    /**
     * Send the input command to JDB and get the response.
     * @param inputCommand the command to send
     * @return response from JDB
     * @throws Exception if an error occurs while sending the command
     */
    public String getResponse(String inputCommand) throws Exception {
        if(!inputCommand.endsWith("\n")){
            inputCommand += "\n";
        }
        jdbConnection.getOutputStream().write(inputCommand.getBytes());
        jdbConnection.getOutputStream().flush();
        String response = readResponse(jdbConnection.getInputStream());
        updateHistory(inputCommand, response);
        return response;
    }

    private String readResponse(InputStream inputStream) throws Exception {
        Thread.sleep(constants.interval);   // Wait for the response to be ready
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[constants.bufferSize];
        int length;
        String response = "";
        while(inputStream.available() > 0){
            length = inputStream.read(buffer);
            result.write(buffer, 0, length);
        }
        response = result.toString("GBK");
        return response;
    }

    private void printResponse(String response) throws Exception {
        String border = "----------------------------------------";
        System.out.println(response);
        System.out.println(border);
    }

    public String getHistory(){
        return history;
    }

    private void updateHistory(String command, String response){
        history += command + "\n" + response + "\n";
    }

    private void clearHistory(){
        history = "";
    }

    private Constants constants;
    private Debugger debugger;
    private ChatBot chatbot;
    private String history;
    Process jdbConnection;
    Process jdbServer;
}
