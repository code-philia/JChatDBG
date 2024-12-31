package ChatDBG;

import java.io.InputStream;
import java.util.Scanner;

/**
 * The main body of ChatDBG.
 */
public class Agent {

    public Agent(){
        constants = Constants.getInstance();
        debugger = Debugger.getInstance();
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
                printResponse();
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
        // TODO: Add more instructions
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
        inputCommand += "\n";
        jdbConnection.getOutputStream().write(inputCommand.getBytes());
        jdbConnection.getOutputStream().flush();
        return 1;
    }

    private void printResponse() throws Exception {
        String border = "----------------------------------------";
        InputStream inputStream = jdbConnection.getInputStream();
        byte[] buffer = new byte[constants.bufferSize];
        int bytesRead = 0;
        while(inputStream.available() > 0){
            bytesRead = inputStream.read(buffer);
            if(bytesRead > 0){
                // Use GBK here to decode Chinese characters
                System.out.println(new String(buffer, 0, bytesRead, "GBK"));
            }
        }
        System.out.println(border);
    }

    private Constants constants;
    private Debugger debugger;
    Process jdbConnection;
}
