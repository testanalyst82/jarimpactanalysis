package com.testanalyst82.tools;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;


class DependencyMapper {
    List<String> collect = null;
    String temp = "";
    Properties prop;
    List<String> collect2 = null;

    private class MavenDep {
        String jarPath;
        String pomPath;
    }
    ArrayList<DependencyMapper.MavenDep> mavenDepList = new ArrayList<MavenDep>();
    
    
    public DependencyMapper() {
        try {
            prop = Utility.readPropertiesFile("src/main/resources/application.properties");
            fileFinder(prop.get("sourceCode").toString());
            
            dependencyReportFromSourceCode();
            dependencyReportFromPomStructure();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

     }
    
    public DependencyMapper(String sourceCode) {
        fileFinder(sourceCode);
        dependencyReportFromSourceCode();
    }
    
    
    private void dependencyReportFromSourceCode() {
        JsonOutput pojo = new JsonOutput();
        pojo.setJars(getJars());
        Gson gson = new Gson();
        FileWriter writer = null;
        try {
            writer = new FileWriter("reference.json");
            writer.write(gson.toJson(pojo));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void dependencyReportFromPomStructure() {
    	FileWriter writer = null;
        try {
            writer = new FileWriter("reference2.json");
            writer.write(scan2().toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }
    private JSONArray scan2() {
    	JSONArray array = new JSONArray();
        try {
        	
        	getJarsWithDependancies();
        	/*Reading the POM file dependency list  of the validating source code*/
            ArrayList<pomXmlReader.pomStruct> objArraylist = pomXmlReader.printPomDependencies();
            
            objArraylist
            .stream()
            .parallel()
            .forEach(attribute->{
            	ArrayList<String> jars=new ArrayList<>();
            	JSONObject jsonObject = new JSONObject();
            	
            	/*traversing through the external jar list*/
            	mavenDepList
            	.stream()
            	.parallel()
            	.forEach(depAttribute-> {
                    	File fi = new File(depAttribute.pomPath);
                    	String jarName= new File(depAttribute.jarPath).getName();
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        try {
                             Document db = dbf.newDocumentBuilder().parse(fi);
                            NodeList dependencyNodes = db.getElementsByTagName("dependency");
                            for(int i=1; i < dependencyNodes.getLength(); i++){
                            	final Node n = dependencyNodes.item(i);
                                final NodeList list = n.getChildNodes();
                                boolean check=false;
                                for (int j = 0; j < list.getLength(); j++) {
                                    final Node n2 = list.item(j);
                                    if (n2.getNodeType() != Node.ELEMENT_NODE) continue;

                                    if(n2.getNodeName().equals("artifactId")) {
                                    	
                                        if(!n2.getTextContent().equals(attribute.artifactId)){
                                        	continue;}
                                        else {check=true;}
                                        }
                                    if(n2.getNodeName().equals("version")){
                                        if(check && n2.getTextContent().equals(attribute.version)){
                                        	jars.add(jarName);}
                                           }
                                	}
                                }
                             
                        	}catch(Exception e){ e.printStackTrace();}
                     });
            	
            		          	
            		 if(!jars.isEmpty()) {
                     	jsonObject.put("artifactId", attribute.artifactId);
                     	jsonObject.put("version", attribute.version);
                     	jsonObject.put("jars", jars);
                     	array.put(jsonObject);
                     }
                }
            );
            System.out.println(array.toString());
            
        } catch (Exception e) {e.printStackTrace();}
        return array;
        
    }

    private void getJarsWithDependancies () {
        String repoPath = prop.getProperty("repoPath").toString();
        Path start = Paths.get(repoPath);
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            this.collect2 = stream
            	.parallel()
                .filter((path -> (path.toString().endsWith(".jar"))))
                .map(String::valueOf)
                .sorted()
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        collect2
        .stream()
        .parallel()
        .forEach(val -> {
            if (Files.exists(Paths.get(val.replace(".jar", ".pom")))) {
                DependencyMapper.MavenDep path = new MavenDep();
                path.jarPath = val;
                path.pomPath = val.replace(".jar", ".pom");
                mavenDepList.add(path);
            } else {

            }

        });

    }
    
   

    private List<Jar> getJars() {
        String jarPath = prop.getProperty("jarPath").toString();
        List<Jar> jarList = new ArrayList<Jar>();
        File directoryPath = new File(jarPath);
        String contents[] = directoryPath.list();

        for (int i = 0; i < contents.length; i++) {

            System.out.println("------MY JARS------" +"\n"+ contents[i]);
            String filePath = jarPath + contents[i];

            Jar jar_object = new Jar();
            jar_object.setJarName(contents[i]);
            jar_object.setClasses(getClassNames(filePath));
            jarList.add(jar_object);
        }
        return jarList;
    }
    

    private ArrayList<Class> getClassNames(String jarName) {
        ArrayList<Class> listOfClasses = new ArrayList<Class>();
        JSONObject myList;
        myList = getClassNamesFromJar(jarName);
        JSONArray ja = myList.getJSONArray("List of Class");

        for (int k = 0; k < ja.length(); k++) {
            String[] arr = ja.getString(k).split("[.]", 0);
            if (arr[arr.length - 1].contains("$"))
                continue;

            Class obj2 = new Class();
            obj2.setClassNames(arr[arr.length - 1]);
            obj2.setReferenceFiles(scan(arr[arr.length - 1]));
            System.out.println("CLASSNAME====>" + arr[arr.length - 1]);
            if(scan(arr[arr.length - 1]).isEmpty())continue;
            listOfClasses.add(obj2);
        }
        //list of class objects are returned
        return listOfClasses;
    }

    private ArrayList<ReferenceFile> scan(String scanningPattern) {
        ArrayList<ReferenceFile> filesReference = new ArrayList<ReferenceFile>();
        collect
        .forEach(path -> {
            BufferedReader br = null;
            ArrayList<String> str = new ArrayList<String>();
            try {
                Pattern p1 = Pattern.compile("\\s[\b" + scanningPattern + "\b]{" + scanningPattern.length() + "}[.].*");
                Pattern p2 = Pattern.compile("(\\bpublic\\b|\\bprivate\\b|\\bprotected\\b)\\s[a-zA-Z_0-9]+\\s[a-zA-Z_0-9$]+([(][)]|[(][\\s|[a-zA-Z_0-9\\s\\[\\],$]+[)]])");
                br = Files.newBufferedReader(Paths.get(path));
                Stream<String> lines = br.lines();
                lines
                .parallel()
                .forEach(line -> {
                    if (p2.matcher(line).find()) {
                        temp = line.toString();
                    }
                    if (p1.matcher(line).find()) {
                        if (line.contains(" " + scanningPattern + ".")) {
                            System.out.println("THE ASSOCIATED JAVA FILES: "+"\n"+ path);
                            System.out.println(temp);
                            str.add(temp);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!str.isEmpty()) {
                ReferenceFile obj = new ReferenceFile();
                String[] tempArray = path.split("\\\\");
                //tempArray[tempArray.length-1];
                //obj.setFileName(path);
                obj.setFileName(tempArray[tempArray.length-1]);
                obj.setMethods(str);
                filesReference.add(obj);
            }
        });
        return filesReference;
    }

    
    /*===================================================================================================*/
    private void fileFinder(String sourceCode) {
        Path start = Paths.get(sourceCode);
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            this.collect = stream
            		.parallel()
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static JSONObject getClassNamesFromJar(String jarName) {
        JSONArray listofClasses = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jar;

            while (true) {
                jar = jarFile.getNextJarEntry();
                if (jar == null) {
                    break;
                }
                if ((jar.getName().endsWith(".class"))) {
                    String className = jar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    listofClasses.put(myClass);
                }
            }
            object.put("Jar File Name", jarName);
            object.put("List of Class", listofClasses);
        } catch (Exception e) {
            System.out.println("Oops.. Encounter an issue while parsing jar" + e.toString());
        }
        return object;
    }


    


}
