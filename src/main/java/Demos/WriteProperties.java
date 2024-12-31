package Demos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WriteProperties {
    /*Try to modify the values in config.properties*/
    public static void main(String[] args){
        String configFilePath = "config.properties";
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(configFilePath)){
            properties.load(fis);
            // Change value of property
            /*properties.setProperty("task", "maven");
            try(FileOutputStream fos = new FileOutputStream(configFilePath)){
                properties.store(fos, "Modified by WriteProperties.java");
                System.out.println("Task value updated successfully.");
            }*/
            // Read value of porperty
            String basedir = properties.getProperty("basedir");
            System.out.println("basedir: " + basedir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
