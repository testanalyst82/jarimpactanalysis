# jarimpactanalysis

Usage

Use application.properties file to configure 

jar path : The jars which needs to be analyzed should be available in this location.
sourceCode : The location of the project that needs to verified for dependency impact. 
repoPath : Local maven repository path to validate the jar dependency based on the dependency mentioned in the pom file (pomLocation)
pomLocation : POM file with the dependency file and version details that needs to be analyzed 

 

Once the property file is set, run the Run.java file 

Jar impact on the project source code is identified from the reference.json file and jar dependency in the library folder can be identified from reference2.json file
