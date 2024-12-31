package Demos;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/*Try to add or modify the value of a property in test.xml*/
public class ModifyXML {
    public static void main(String[] args) {
        String xmlFilePath = "./test.xml";
        try{
            File xmlFile = new File("test.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList propertiesList = doc.getElementsByTagName("property");

            // 1. Read a property
            String propertyName = "d4j.dir.projects";
            String propertyValue = getAttributeValue(propertiesList, propertyName);
            System.out.println("Property " + propertyName + " has value: " + propertyValue);

            // 2. Modify a property
            String propertyNameToModify = "basedir";
            String newValue = "new_basedir";
            setAttributeValue(propertiesList, propertyNameToModify, newValue);

            // 3. Add a new property
            String newPropertyName = "new_property";
            String newPropertyValue = "new_property_value";
            addProperty(doc, newPropertyName, newPropertyValue);

            // Write the changes back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);

            System.out.println("XML file updated successfully.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Read the value of a property
    private static String getAttributeValue(NodeList propertiesList, String propertyName){
        for (int i = 0; i < propertiesList.getLength(); i++) {
            Element property =  (Element) propertiesList.item(i);
            if (property.getAttribute("name").equals(propertyName)) {
                return property.getAttribute("value");
            }
        }
        return null;
    }

    // Modify the value of a property
    private static void setAttributeValue(NodeList propertiesList, String propertyName, String newValue){
        for (int i = 0; i < propertiesList.getLength(); i++) {
            Element property =  (Element) propertiesList.item(i);
            if (property.getAttribute("name").equals(propertyName)) {
                property.setAttribute("value", newValue);
                break;
            }
        }
    }

    // Add a new property
    private static void addProperty(Document doc, String propertyName, String propertyValue){
        Element properties = (Element) doc.getElementsByTagName("project").item(0);
        Element newProperty = doc.createElement("property");
        newProperty.setAttribute("name", propertyName);
        newProperty.setAttribute("value", propertyValue);
        properties.appendChild(newProperty);
    }
}
