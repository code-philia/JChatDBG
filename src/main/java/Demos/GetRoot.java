package Demos;

/*Get system path of this project*/
public class GetRoot {
    public static void main(String[] args) {
        String rootPath = System.getProperty("user.dir");
        System.out.println("Root path: " + rootPath);
    }
}
