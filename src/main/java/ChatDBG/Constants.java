package ChatDBG;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Initialize this class to read properties from config.properties and set corrects values in defects4j.build.xml.
 * Has functions to get configuration related values.
 */
public class Constants {
    private static Constants instance;

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    private Constants() {
        readProperties();
        updateXML();
    }

    /**
     * Read properties from config.properties
     */
    private void readProperties(){
        String configFilePath = "config.properties";
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(configFilePath)){
            properties.load(fis);
            repo = properties.getProperty("repo");
            name = properties.getProperty("name");
            id = Integer.parseInt(properties.getProperty("id"));
            task = properties.getProperty("task");
            port = Integer.parseInt(properties.getProperty("port"));
            interval = Integer.parseInt(properties.getProperty("interval"));
            bufferSize = Integer.parseInt(properties.getProperty("bufferSize"));
            basedir = getBasedir();
            getTestEntryClassAndMethod();
            commands = getCommands();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Update properties in defects4j.build.xml
     */
    private void updateXML() {
        String xmlPath = "defects4j.build.xml";
        try{
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Set value of basedir in project
            NodeList propertiesList = doc.getElementsByTagName("project");
            Element project = (Element) propertiesList.item(0);
            project.setAttribute("basedir", basedir);

            // Set values in properties
            propertiesList = doc.getElementsByTagName("property");
            setAttributeValue(propertiesList, "d4j.home", getD4jHome());
            setAttributeValue(propertiesList, "basedir", basedir);
            setAttributeValue(propertiesList, "test.entry.class", testEntryClass);
            setAttributeValue(propertiesList, "test.entry.method", testEntryMethod);

            // Write the changes back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlPath));
            transformer.transform(source, result);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setAttributeValue(NodeList propertiesList, String propertyName, String newValue) {
        for (int i = 0; i < propertiesList.getLength(); i++) {
            Element property = (Element) propertiesList.item(i);
            if (property.getAttribute("name").equals(propertyName)) {
                property.setAttribute("value", newValue);
                break;
            }
        }
    }

    private String getBasedir(){
        File file1 = new File(repo, name);
        File file2 = new File(file1, Integer.toString(id));
        File file3 = new File(file2, "bug");
        String basedir = file3.getAbsolutePath();
        return basedir;
    }

    private String getD4jHome(){
        String rootPath = System.getProperty("user.dir");
        File file = new File(rootPath, "defects4j");
        String d4jHome = file.getAbsolutePath();
        return d4jHome;
    }

    private void getTestEntryClassAndMethod(){
        File file = new File(basedir, "failing_tests");
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            String[] parts = line.substring(4).split("::");
            testEntryClass = parts[0];
            testEntryMethod = parts[1];
            System.out.println("Test entry class: " + testEntryClass);
            System.out.println("Test entry method: " + testEntryMethod);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private ArrayList<String> getCommands(){
        ArrayList<String> commands = new ArrayList<>();
        String configFilePath = "config.properties";
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream(configFilePath)){
            properties.load(fis);
            String commandsWithComma = properties.getProperty("commands");
            String[] parts = commandsWithComma.split(",");
            for(String part: parts){
                commands.add(part);
            }
            return commands;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public String basedir = null;
    public String testEntryClass = null;
    public String testEntryMethod = null;
    public String repo = null;
    public String name = null;
    public int id = 1;
    public String task = null;
    public int port = 5005;
    public int interval = 500;
    public int bufferSize = 100000;
    public ArrayList<String> commands = new ArrayList<>();
}