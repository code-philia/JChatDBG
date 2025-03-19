package ChatDBG;

import java.io.File;
import java.util.ArrayList;

public class BatchTest {
    public static void main(String[] args) {
        String repo = Constants.getInstance().repo;
        String name = Constants.getInstance().name;
        String rootPath = repo + "/" + name;

        File folder = new File(rootPath);
        File[] subFolders = folder.listFiles(File::isDirectory);
        ArrayList<String> ids = new ArrayList<>();
        for (File subFolder : subFolders) {
            String subFolderName = subFolder.getName();
            ids.add(subFolderName);
        }
        for(String id : ids) {
            System.out.println("Running test for id: " + id);
        }
    }
}
