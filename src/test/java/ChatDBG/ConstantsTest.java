package ChatDBG;

public class ConstantsTest {
    public static void main(String[] args){
        Constants constants = Constants.getInstance();
        System.out.println("basedir: " + constants.basedir);
        System.out.println("testEntryClass: " + constants.testEntryClass);
        System.out.println("testEntryMethod: " + constants.testEntryMethod);
        System.out.println("repo: " + constants.repo);
        System.out.println("name: " + constants.name);
        System.out.println("id: " + constants.id);
        System.out.println("task: " + constants.task);
        System.out.println("port: " + constants.port);
        System.out.println("interval: " + constants.interval);
        System.out.println("bufferSize: " + constants.bufferSize);
    }
}
