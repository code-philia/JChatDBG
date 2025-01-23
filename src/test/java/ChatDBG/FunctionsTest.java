package ChatDBG;

public class FunctionsTest {
    public static void main(String[] args){
        FunctionsTest functionsTest = new FunctionsTest();
        // functionsTest.testInfo();
        // functionsTest.testInfoWithBuiltIn();
    }

    /**
     * Retrieve the code for the given function and class
     */
    public void testInfo(){
        String className = "org.apache.commons.math3.optimization.direct.CMAESOptimizerTest";
        String methodName = "testFitAccuracyDependsOnBoundary";
        System.out.println(Functions.getInstance().info(className, methodName));
    }

    /**
     * Try to identify if a class is defined by the user
     */
    public void testInfoWithBuiltIn(){
        String className = "java.util.List"; // Java built-in
        try{
            Class<?> clazz = Class.forName(className);
            ClassLoader classLoader = clazz.getClassLoader();
            if(classLoader == null){
                System.out.println("system class");
            }
            else if(classLoader instanceof java.net.URLClassLoader){
                System.out.println("user class");
            }
            else{
                System.out.println("other class");
            }
        } catch (ClassNotFoundException e){
            System.out.println("Class not found: " + className);
        }
    }

    public void testDebug(){
        // Cannot be implemented because Agent.java is not designed to be testable
    }
}
