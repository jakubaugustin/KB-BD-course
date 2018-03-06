# Scala basics

In this excercise we will create simple Scala application to download some public data.
Before that we will establish some rules and best practices.

## Getting started

_Scala Tour_: https://docs.scala-lang.org/tour/tour-of-scala.html

Points for discussion:
  * _Functional vs. Object oriented_: Can you describe the difference? Which one do you prefer and why? https://stackoverflow.com/questions/2078978/functional-programming-vs-object-oriented-programming
  * _Scala vs. Java compatibility_: Can you run Java from Scala and vice versa? Are there any constraints? http://www.scala-lang.org/old/faq/4
  * _Functions vs. methods_: Is there a difference?
  * _Objects vs. Classes_: What is singleton?
  * _Types and Collections_ How to use types? Why should we always declare types? Which collection is the best choice? https://docs.scala-lang.org/overviews/collections/overview.html
  * _Class constructors_: How to use constructors in Scala?
  * _Higher order functions_: Function as argument - what? https://en.wikibooks.org/wiki/Scala/Tuples
  * _Case classes and Tuples_: How to use them and which one is better? 
  * _Patter matching_: How to use it?
  * _For comprehension_: ...Mind blown...
  
  
## Create Maven project

Create maven project with following settings:
```
groupId: cz.kb.bd
artifactId: ContractRegistryDownloader
version: 1.0-SNAPSHOT
package: cz.kb.bd.contractregistry
```

## First program

We are going to download monthly bulks from 2016 to 2018.
For now, lets just work with YEAR-MONTH combinations without actually downloading something.
<br />
Use main method in App class.

### Assignment 1: write all YEAR-MONTH combinations using println and  2 nested FOR loops
### Assignment 2: write all YEAR-MONTH combinations using println and single FOR loop
### Assignment 3: write all YEAR-MONTH combinations using println and foreach function
### Assignment 4: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and Tuples
### Assignment 5: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and case class
### Assignment 6: write all YEAR-MONTH combinations using println function for comprehension

