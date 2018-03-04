# Maven Basics

In this excersice we will create our first Maven Scala project.
We will use Maven in command line environment for educational purposes. You can use Maven from your favourite IDE if you are  already familiar with it.

## Preconditions - Maven Installation:

_Install JDK_<br/>
https://en.wikipedia.org/wiki/Java_Development_Kit

Maven requires JDK, if you only have JRE, please install JDK too.
During the course we will use JDK 8 161 - if possible, plese use the same version.
Download link: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
JAVA_HOME for Windows by default should be `C:\Program Files\Java\jre1.8.0_161`

_Install Maven_<br/>
https://en.wikipedia.org/wiki/Apache_Maven

During the course we will use Maven 3.5.2 - if possible, plese use the same version.
Download Maven binaries from: http://mirror.hosting90.cz/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip
Extract files and add location to PATH (this bin directory of course)


_Test it_<br/>
Try running `mvn -version` from command line
You should see a message similar to this:
```
Apache Maven 3.5.2 (138edd61fd100ec658bfa2d307c43b76940a5d7d; 2017-10-18T09:58:13+02:00)
Maven home: D:\apache-maven-3.5.2\bin\..
Java version: 1.8.0_161, vendor: Oracle Corporation
Java home: C:\Program Files\Java\jre1.8.0_161
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```



## Create first Scala project
https://maven.apache.org/guides/introduction/introduction-to-archetypes.html

Open command line and write `mvn archetype:generate` and hit enter
You will see many archetypes to choose from
```
Choose a number or apply filter
```
To limit results to Scala archetypes only write `scala` and hit enter. Now Maven will only show results containing scala.
Still, many results should come up. We will use `net.alchim31.maven:scala-archetype-simple` archetype
Search for number next to tthis archetype, write it and hit enter.
```
Choose net.alchim31.maven:scala-archetype-simple version:
1: 1.4
2: 1.5
3: 1.6
```
Lets use latest version. Write 3 and hit enter.
```
Define value for property 'groupId':
```
Let`s pick you group ID
https://maven.apache.org/guides/mini/guide-naming-conventions.html
According to naming conventions use `cz.kb.bd` and hit enter
```
Define value for property 'artifactId':
```
Use `SampleApp` and hit enter
```
Define value for property 'version' 1.0-SNAPSHOT:
```
Let`s name our version. Default is OK so hit enter
```
Define value for property 'package' cz.kb.bd:
```
Name our package - for this excercise will will use single package, but let`s name it anyway `cz.kb.bd.base`
```
Confirm properties configuration:
groupId: cz.kb.bd
artifactId: SampleProject
version: 1.0-SNAPSHOT
package: cz.kb.bd.base
 Y: 
 ```
 To confim type `Y` and hit enter. Our project will be created to new directory named `SampleProject`.
 Explore the directory.
 
 There should be 4 things in your new project directory:
   * pom.xml file - https://maven.apache.org/pom.html
   * application source file directory - src/main/scala/...
   * application test file directory - src/test/scala/...
   * .gitignore file - https://git-scm.com/docs/gitignore
 
 First edit the POM file. 
 Unfortunately there is an error in this simple archetype - remove line 70 `<arg>-make:transitive</arg>`
 
 
 
 ## Compile the project
 Now the project

