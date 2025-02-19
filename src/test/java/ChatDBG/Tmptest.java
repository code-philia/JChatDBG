package ChatDBG;

import java.io.BufferedReader;
import java.io.InputStream;
import java.text.MessageFormat;

public class Tmptest {
    public static void main(String[] args) {
        Tmptest test = new Tmptest();
        String traceFormat = test.getInstruction("trace.txt");
        String trace = MessageFormat.format(traceFormat, "John");
        System.out.println(trace);
    }

    public String getInstruction(String path){
        InputStream inputStream = Prompt.class.getClassLoader().getResourceAsStream(path);
        if(inputStream == null){
            return "File not found";
        }
        try(BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(inputStream))){
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            return "Error in TmpTest.getInstruction: " + e.getMessage();
        }
    }
}
