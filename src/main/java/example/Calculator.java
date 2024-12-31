package example;

public class Calculator {
    public int add(int a, int b) {
        // Get the caller of this function and print it out
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        System.out.println("Caller: " + caller.getClassName() + "." + caller.getMethodName());
        return a + b;
    }
}
