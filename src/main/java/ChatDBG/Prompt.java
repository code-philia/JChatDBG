package ChatDBG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        // TODO: Implement `debug` and `info` functions as done in ChatDBG
        userText = question;
        String prompt = "";
        try{
            prompt += getInstructions() + "\n";
            prompt += getStackTrace() + "\n";
            prompt += getErrorMessage() + "\n";
            prompt += getErrorDetails() + "\n";
            prompt += getDebugHistory() + "\n";
            prompt += getUserText() + "\n";
            return prompt;
        }
        catch (Exception e){
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String getInstructions(){
        String instructions = "";
        instructions += "You are a debugging assistant. You will be give a Python stack trace" +
                "for an error and answer questions related to the root cause of the error.\n" +
                "Call the `debug` function to run JDB debugger commands on the stopped program." +
                "You may call the `debug` function to run the following commands: ";
        // Add some commands dynamically
        int commandsLength = Constants.getInstance().commands.size();
        for(int i=0; i<commandsLength-1; i++){
            instructions += "`" + Constants.getInstance().commands.get(i) + "`" + ", ";
        }
        instructions += Constants.getInstance().commands.get(commandsLength-1) + ". ";
        instructions += "Call `debug` to print any variable value or expression that you believe" +
                "may contribute to the error.\nCall the `info` function to get the documentation " +
                "and source code for any variable, function, package, class, method reference, field reference, " +
                "or dotted reference visible in the current frame. Examples include: n, e.n where e is an " +
                "expression, and t.n where t is a type. Unless it is from a common, widely-used library, " +
                "you MUST call `info` exactly once on any symbol that is reference in code leading up to the error.\n" +
                "Call the provided functions as many times as you would like.\n" +
                "The root cause of any error is likely due to a problem in the source code from the user. " +
                "Explain why each variable contributing to the error has been set to the value that is has. " +
                "Continue with your explanation until you reach the root cause of the error. Your answer " +
                "may be as long as necessary.\n" +
                "End your answer with a section titled \"Recommendation\" that contains one of\n" +
                "- a fix if you have identified the root cause\n" +
                "- a numbered list of 1-3 suggestions for how to continue debugging if you have not";
        return instructions;
    }

    private String getStackTrace() {
        String prompt = "";
        prompt += "The program has this stack trace:\n";
        try{
            String originCodeBlock = Agent.getInstance().getResponse("where");
            prompt += wrapCodeBlock(originCodeBlock);
            return prompt;
        }
        catch (Exception e){
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String getErrorMessage() throws Exception {
        File file = new File(Constants.getInstance().basedir, "failing_tests");
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            String rest = "";
            while((line = br.readLine()) != null){
                rest += line;
            }
            String prompt = "";
            prompt += "The program encountered the following error:\n";
            prompt += wrapCodeBlock(rest);
            return prompt;
        }
        catch(IOException e){
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String getErrorDetails(){
        return "The assertion code is correct and must not be changed.";
    }

    // The command line information is included in ChatDBG's code but not in paper, so I decide not to add it to prompt

    // The program input is included in ChatDBG's code but not in paper, so I decide not to add it to prompt

    private String getDebugHistory(){
        String prompt = "";
        prompt += "This is the history of some pdb commands I ran and the results:\n";
        String originCodeBlock = Agent.getInstance().getHistory();
        prompt += wrapCodeBlock(originCodeBlock);
        return prompt;
    }

    private String getUserText(){
        return userText;
    }

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
