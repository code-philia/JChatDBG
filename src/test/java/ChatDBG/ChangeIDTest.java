package ChatDBG;

import java.io.*;
import java.util.Properties;

public class ChangeIDTest {
    public static void main(String[] args){
        String filePath = "config.properties";
        String newId = "18";
        try {
            // 创建临时文件用于存储修改后的内容
            StringBuilder content = new StringBuilder();
            // 逐行读取文件
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 检查是否是id对应的行
                    if (line.trim().startsWith("id=")) {
                        // 替换id的值
                        line = "id=" + newId;
                    }
                    // 将当前行追加到内容中
                    content.append(line).append("\n");
                }
            }
            content.deleteCharAt(content.length() - 1);

            // 将修改后的内容写回文件
            /*try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }*/
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content.toString());
            writer.close();

            System.out.println("Properties file updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(filePath)){
            properties.load(fis);
            int id = Integer.parseInt(properties.getProperty("id"));
            System.out.println("Current id: " + id);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
