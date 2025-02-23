package ChatDBG;

import java.io.*;
import java.text.MessageFormat;

/**
 * Used to build the prompt for the LLM.
 */
public class Prompt {
    private static Prompt instance;

    public static Prompt getInstance() {
        if (instance == null) {
            instance = new Prompt();
        }
        return instance;
    }

    public String getPrompt(String question) {
        String promptFormat = getFormat("NewPrompt/PromptFormat.txt");
        try{
            String instructions = getInstructions();
            String stackTrace = getStackTrace();
            String errorMessage = getErrorMessage();
            String debugHistory = getDebugHistory();
            String userText = getUserText();
            return MessageFormat.format(promptFormat, instructions, stackTrace, errorMessage, debugHistory, userText);
        }
        catch (Exception e){
            e.printStackTrace();
            return "Error in ChatDBG.Prompt.getPrompt: " + e.getMessage();
        }
    }

    private String getInstructions(){
        String instructionsFormat = getFormat("NewPrompt/InstructionsFormat.txt");
        String commandsString = "";
        int commandsLength = Constants.getInstance().commands.size();
        for(int i=0; i<commandsLength-1; i++){
            String command = Constants.getInstance().commands.get(i);
            commandsString += "`" + command + "`" + ", ";
        }
        commandsString += "`" + Constants.getInstance().commands.get(commandsLength-1) + "`";
        return MessageFormat.format(instructionsFormat, commandsString);
    }

    private String getStackTrace() {
        String stackTraceFormat = getFormat("NewPrompt/StackTraceFormat.txt");
        try{
            String stackTrace = Agent.getInstance().getResponse("where");
            stackTrace = wrapCodeBlock(stackTrace);
            return MessageFormat.format(stackTraceFormat, stackTrace);
        }
        catch (Exception e){
            e.printStackTrace();
            return "Error in ChatDBG.Prompt.getStackTrace: " + e.getMessage();
        }
    }

    private String getErrorMessage() throws Exception {
        String errorFormat = getFormat("NewPrompt/ErrorFormat.txt");
        String errorDetails = getFormat("NewPrompt/ErrorDetails.txt");
        File file = new File(Constants.getInstance().basedir, "failing_tests");
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            String errorMessage = "";
            while((line = br.readLine()) != null){
                errorMessage += line;
            }
            errorMessage = wrapCodeBlock(errorMessage);
            return MessageFormat.format(errorFormat, errorMessage, errorDetails);
        }
        catch(IOException e){
            e.printStackTrace();
            return "Error in ChatDBG.Prompt.getErrorMessage: " + e.getMessage();
        }
    }

    // The command line information is included in ChatDBG's code but not in paper, so I decide not to add it to prompt

    // The program input is included in ChatDBG's code but not in paper, so I decide not to add it to prompt

    private String getDebugHistory(){
        String historyFormat = getFormat("NewPrompt/HistoryFormat.txt");
        String history = Agent.getInstance().getHistory();
        history += wrapCodeBlock(history);
        return MessageFormat.format(historyFormat, history);
    }

    public String getUserText(){
        return getFormat("NewPrompt/UserText.txt");
    }

    private String getFormat(String path){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
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

    /**
     * Truncate the code block if it is too long and wrap it with triple backticks
     * @param code the code block to be wrapped
     * @return the wrapped code block
     */
    private String wrapCodeBlock(String code){
        code = truncateProportionally(code);
        String result = "";
        result += "```\n";
        result += code + "\n";
        result += "```";
        return result;
    }

    private String truncateProportionally(String code){
        int maxLength = 2048;
        double topProportion = 0.5;
        if(code.length() > maxLength){
            int pre = Math.max(0, (int)((maxLength-5)*topProportion));
            int post = Math.max(0, maxLength-5-pre);
            code = code.substring(0, pre) + "[...]" + code.substring(code.length()-post);
        }
        return code;
    }

    private String userText = "";
}
