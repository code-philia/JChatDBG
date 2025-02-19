package ChatDBG;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.io.File;
import java.util.List;

/**
 * Functions used by LLM to interact with the target program.
 */
public class Functions {
    private static Functions instance;

    public static Functions getInstance() {
        if (instance == null) {
            instance = new Functions();
        }
        return instance;
    }

    private Functions() {}

    /**
     * Get the documentation and source code for the function.
     * @param className The class name of the function.
     *                  Example: "org.apache.commons.math3.optimization.direct.CMAESOptimizerTest"
     * @param methodName The method name of the function.
     *                   Example: "testFitAccuracyDependsOnBoundary"
     * @return The documentation and source code for the function.
     * */
    public String info(String className, String methodName){
        // TODO: QDox and none of the existing tools can help getting the line numbers of the code,
        //   so that we have to modify the prompt instead, ask ChatGPT to use both list and info
        //   commands to locate the line of code
        // TODO: ChatDBG try to use info to learn about a variable name some times, try to find out why
        if(isSystemClass(className)){
            return String.format("%s is a system class", className);
        }
        String[] parts = className.split("\\.");
        String mainPath = Constants.getInstance().basedir + "/src/main/java";
        String testPath = Constants.getInstance().basedir + "/src/test/java";
        for(int i=0; i<parts.length-1; i++){
            mainPath += "/" + parts[i];
            testPath += "/" + parts[i];
        }
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(mainPath));
        builder.addSourceTree(new File(testPath));
        JavaClass cls = builder.getClassByName(className);
        if(cls == null){
            return "Function not found, please check if the class name and method name are correct, or try to use breakpoint and step command to locate the function";
        }
        List<JavaMethod> methods = cls.getMethods();
        for(JavaMethod method: methods){
            if(method.getName().equals(methodName)){
                return method.getCodeBlock();
            }
        }
        return "Function not found";
    }

    /**
     * Run the JDB debugger commands on the stopped program.
     * @param command The JDB debugger command.
     *                Example: "print x"
     * @return The output of the JDB debugger command.
     * */
    public String debug(String command){
        if(!isAllowedCommand(command)){
            return "Command not allowed or not valid";
        }
        try{
            if(command.startsWith("list")){
                return handleListCommand(command);
            }
            return Agent.getInstance().getResponse(command);
        }
        catch (Exception e){
            e.printStackTrace();
            return "Error in ChatDBG.Functions.debug: " + e.getMessage();
        }
    }

    private boolean isSystemClass(String className){
        try{
            Class<?> clazz = Class.forName(className);
            ClassLoader classLoader = clazz.getClassLoader();
            return classLoader == null;
        } catch (ClassNotFoundException e){
            return false;
        }
    }

    private boolean isAllowedCommand(String command){
        String[] allowedCommands = {"where", "up", "down", "print", "list"};
        for(String allowedCommand: allowedCommands){
            if(command.startsWith(allowedCommand)){
                return true;
            }
        }
        return false;
    }

    private String handleListCommand(String command) throws Exception{
        String mainPath = Constants.getInstance().basedir + "/src/main/java";
        String testPath = Constants.getInstance().basedir + "/src/test/java";
        String englishErrorMessage = "Source file not found";
        String chineseErrorMessage = "找不到源文件";
        String response;
        // Try main first
        Agent.getInstance().getResponse("use " + mainPath);
        response = Agent.getInstance().getResponse(command);
        if(!response.contains(englishErrorMessage) && !response.contains(chineseErrorMessage)){
            return response;
        }
        // Try test next
        Agent.getInstance().getResponse("use " + testPath);
        return Agent.getInstance().getResponse(command);
    }
}
