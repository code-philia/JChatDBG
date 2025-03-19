package ChatDBG;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();

        ArrayList<String> ids = main.getIds();
        for(String id : ids) {
            main.changeId(id);
            Constants.getInstance().refresh();
            Agent.getInstance().run();
        }

        System.exit(0);
    }

    private ArrayList<String> getIds(){
        String repo = Constants.getInstance().repo;
        String name = Constants.getInstance().name;
        String rootPath = repo + "/" + name;

        File folder = new File(rootPath);
        File[] subFolders = folder.listFiles(File::isDirectory);
        ArrayList<String> ids = new ArrayList<>();
        for(File subFolder : subFolders) {
            String subFolderName = subFolder.getName();
            ids.add(subFolderName);
        }
        return ids;
    }

    private void changeId(String id){
        String filePath = "config.properties";
        Properties properties = new Properties();
        try{
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = reader.readLine()) != null){
                if(line.trim().startsWith("id=")){
                    line = "id=" + id;
                }
                content.append(line).append("\n");
            }
            content.deleteCharAt(content.length() - 1);

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content.toString());
            writer.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
