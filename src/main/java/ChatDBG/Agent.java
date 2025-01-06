package ChatDBG;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The main body of ChatDBG.
 */
public class Agent {

    public Agent(){
        constants = Constants.getInstance();
        debugger = Debugger.getInstance();
        chatbot = ChatBot.getInstance();
    }

    public void run(){
        debugger.run();
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
                if(sendCommand(scanner)==0){
                    break;
                }
                Thread.sleep(constants.interval);
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
        String command = "jdb -connect com.sun.jdi.SocketAttach:hostname=localhost,port=" + port;
        try {
            return Runtime.getRuntime().exec(command);
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
            return 0;
        }
        boolean isJDBCommand = false;
        String command = inputCommand.split(" ")[0];
        if(constants.commands.contains(command)){
            isJDBCommand = true;
        }
        inputCommand += "\n";
        if(isJDBCommand){
            printResponse(getResponse(inputCommand));
        }
        else{
            printResponse(chatbot.getResponse(inputCommand));
        }
        return 1;
    }

    private String getResponse(String inputCommand) throws Exception {
        jdbConnection.getOutputStream().write(inputCommand.getBytes());
        jdbConnection.getOutputStream().flush();

        InputStream inputStream = jdbConnection.getInputStream();
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

    private Constants constants;
    private Debugger debugger;
    private ChatBot chatbot;
    Process jdbConnection;
}
