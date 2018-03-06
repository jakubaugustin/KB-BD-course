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
  * _Types and Collections_: How to use types? Why should we always declare types? Which collection is the best choice? https://docs.scala-lang.org/overviews/collections/overview.html
  * _String interpolation_: https://docs.scala-lang.org/overviews/core/string-interpolation.html
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
```scala
for(year <- START_YEAR until END_YEAR){
	for(month <- START_MONTH until END_MONTH){
		println(s"year: ${year}, month: ${month}")
	}
}
```
### Assignment 2: write all YEAR-MONTH combinations using println and single FOR loop
```scala
for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH){
	println(s"year: ${year}, month: ${month}")
}
```
### Assignment 3: write all YEAR-MONTH combinations using println and foreach function
```scala
(START_YEAR until END_YEAR)
	.foreach(year => (START_MONTH until END_MONTH)
		.foreach(month => println(s"year: ${year}, month: ${month}")))
```
### Assignment 4: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and Tuples
```scala
(START_YEAR until END_YEAR)
	.flatMap(year => (START_MONTH until END_MONTH)
		.map(month => (year, month)))
	.foreach(x => println(s"year: ${x._1}, month: ${x._2}"))
```
### Assignment 5: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and case class
```scala
case class MyDate(year : Int, month : Int)

(START_YEAR until END_YEAR)
	.flatMap(year => (START_MONTH until END_MONTH)
		.map(month => MyDate(year, month)))
	.foreach((x : MyDate) => println(s"year: ${x.year}, month: ${x.month}"))
```
### Assignment 6: write all YEAR-MONTH combinations using println function for comprehension
```scala
case class MyDate(year : Int, month : Int)

(for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield MyDate(year, month))
	.foreach((x : MyDate) => println(s"year: ${x.year}, month: ${x.month}"))
```

