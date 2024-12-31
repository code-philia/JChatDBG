package Demos;

import java.io.IOException;
import java.util.Scanner;
import java.io.InputStream;

public class debugtest {
    /*Connect to specified port as debugging client, keep sending commands and getting outputs*/
    public static void main(String[] args) {
        String border = "----------------------------------------";
        Integer port = 5005;
        Integer bufferSize = 100000;
        String command = "jdb -connect com.sun.jdi.SocketAttach:hostname=localhost,port=" + port;
        try {
            Process p = Runtime.getRuntime().exec(command);
            Scanner scanner = new Scanner(System.in);
            while(true){
                // Send command
                System.out.print("User command: ");
                String inputCommand = scanner.nextLine();
                if(inputCommand.equals("exit")){
                    p.destroy();
                    break;
                }
                inputCommand += "\n";
                p.getOutputStream().write(inputCommand.getBytes());
                p.getOutputStream().flush();
                // Sleep for a while, because the output may not be ready immediately
                Thread.sleep(500);
                // Get output
                InputStream inputStream = p.getInputStream();
                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                while(inputStream.available() > 0){
                    bytesRead = inputStream.read(buffer);
                    if(bytesRead > 0){
                        System.out.println(new String(buffer, 0, bytesRead));
                    }
                }
                System.out.println(border);
            }
            scanner.close();
        } 
        catch (IOException e) {
            System.out.println("Debug section is already closed.");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
