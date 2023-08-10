package com.testanalyst82.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class pomXmlReader {

    public static class pomStruct{
        String groupId;
        String artifactId;
        String version;
    }
    static ArrayList <pomStruct> listPomStruct = new ArrayList<pomStruct>();
    public static ArrayList<pomStruct> printPomDependencies() throws Exception{
    	Properties prop = Utility.readPropertiesFile("src/main/resources/application.properties");
    	
        //final File pomFile = new File("./pom.xml");
    	final File pomFile = new File(prop.get("pomLocation").toString());
        
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dBuilder.parse(pomFile);
        doc.getDocumentElement().normalize();
        final NodeList dependencyNodes = doc.getElementsByTagName("dependency");

        for (int i = 0; i < dependencyNodes.getLength(); i++) {
            final Node n = dependencyNodes.item(i);
            final NodeList list = n.getChildNodes();
            pomXmlReader.pomStruct  objPomStruct = new pomStruct();
                for (int j = 0; j < list.getLength(); j++) {
                    final Node n2 = list.item(j);

                    if (n2.getNodeType() != Node.ELEMENT_NODE) continue;



               if(n2.getNodeName().equals("groupId")){
                    objPomStruct.groupId=n2.getTextContent();

                }
                else if(n2.getNodeName().equals("artifactId")){objPomStruct.artifactId=n2.getTextContent();

                }
                else if(n2.getNodeName().equals("version")){objPomStruct.version=n2.getTextContent();

                }

            }
            listPomStruct.add(objPomStruct);
            //System.out.println(listPomStruct.size());

        }


        //listPomStruct.forEach(x-> System.out.println("groupId"+":"+x.groupId+"\n"+"artifactId"+":"+x.artifactId+"\n"+"version"+":"+x.version));
        return listPomStruct;


    }
}