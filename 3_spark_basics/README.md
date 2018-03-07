# Spark basics

In this excercise we will create simple Spark application

## Create new Maven project or copy it from other excercises

POM.xml will be identical, but extra dependecis are quired.
Add Spark Dependencies
```xml
<properties>
	<scala.compat.version>2.11</scala.compat.version>
	<spark.version>2.2.0</spark.version>
</properties>

...

<!-- https://mvnrepository.com/artifact/org.apache.spark/spark-core -->
<dependency>
	<groupId>org.apache.spark</groupId>
	<artifactId>spark-core_${scala.compat.version}</artifactId>
	<version>${spark.version}</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.spark/spark-sql -->
<dependency>
	<groupId>org.apache.spark</groupId>
	<artifactId>spark-sql_${scala.compat.version}</artifactId>
	<version>${spark.version}</version>
</dependency>
```

## Create SparkSession

```scala
import org.apache.spark.sql.SparkSession

  val APP_NAME : String = "My first Spark app"
  val SPARK_MASTER : String = "local[*]"
  
  val spark : SparkSession = SparkSession
		.builder()
		.appName(APP_NAME)
		.config("spark.master", SPARK_MASTER)
		.getOrCreate()
```

Now we can use all Spark features from our code locally.
We will deploy to Hadoop in further excercises.

## Assignments

For all assinments we will use YEAR-MONTH pairs from excercise 2 as our "data":
```scala
val START_YEAR : Int = 2016
val END_YEAR : Int = 2019
val START_MONTH : Int = 1
val END_MONTH : Int = 13
case class MyDate(year : Int, month : Int)
val data : Seq[MyDate] = for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield MyDate(year, month)
val dataTuple : Seq[(Int, Int)] = for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield (year, month)
``` 

### Assignment 1: Parallelize data to RDD[MyDate] and write it`s content using println
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
dataRDD.foreach(println)
```

### Assignment 2: Parallelize data to RDD[MyDate] convert it to Dataframe and use show method
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
val dataDF : Dataset[Row] = dataRDD.toDF()
dataDF.show()
```

### Assignment 3: Parallelize dataTuple to RDD[(Int, Int)] and write it`s content using println
```scala
val dataRDD : RDD[(Int, Int)] = spark.sparkContext.parallelize(dataTuple)
dataRDD.foreach(println)
```

### Assignment 4: Parallelize dataTuple to RDD[(Int, Int)] convert it to Dataframe and use show method
```scala
val dataRDD : RDD[(Int, Int)] = spark.sparkContext.parallelize(dataTuple)
val dataDF : Dataset[Row] = dataRDD.toDF("year", "month")
dataDF.show()
```

### Assignment 5: Get count from RDD
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
println(s"This RDD contains ${dataRDD.count()} records")
```

### Assignment 6: Get count from Dataframe
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
val dataDF : Dataset[Row] = dataRDD.toDF()
println(s"This DF contains ${dataDF.count()} records")
```

### Assignment 7: Get count of months in years from Dataframe using DF methods
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
val dataDF : Dataset[Row] = dataRDD.toDF()
dataDF.groupBy(col("year")).count().show()
```

### Assignment 8: Get count of months in years from Dataframe using SQL
```scala
val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
val dataDF : Dataset[Row] = dataRDD.toDF()
dataDF.createOrReplaceTempView("data")
spark.sql("""
	select 
		year
		, count(*) as months_count
	from data 
	group by year
""")
.show()
```

