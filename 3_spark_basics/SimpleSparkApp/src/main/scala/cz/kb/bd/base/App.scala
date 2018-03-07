package cz.kb.bd.base

import org.apache.spark.sql.{SparkSession, Dataset, Row}
import org.apache.spark.rdd.RDD
import org.apache.log4j.{Logger, Level, BasicConfigurator}
import scala.util.Try

object App {

	private[this] val START_YEAR : Int = 2016
	private[this] val END_YEAR : Int = 2019
	private[this] val START_MONTH : Int = 1
	private[this] val END_MONTH : Int = 13
	private[this] val APP_NAME : String = "My first Spark app"
	private[this] val SPARK_MASTER : String = "local[*]"
	
	case class MyDate(year : Int, month : Int)
	
	private[this] val spark : SparkSession = SparkSession
		.builder()
		.appName(APP_NAME)
		.config("spark.master", SPARK_MASTER)
		.getOrCreate()
	import spark.implicits._
	
	private[this] val data : Seq[MyDate] = for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield MyDate(year, month)
	private[this] val dataTuple : Seq[(Int, Int)] = for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield (year, month)
	//Logger
	private[this] val log : Logger = Logger.getRootLogger
	log.setLevel(Level.INFO)
	
	def main(args : Array[String]) {
		if (args.length == 0) throw new IllegalStateException("Please specify assignment number as an argument.")
		val assignmentNumber : Int = Try(args(0).toInt).getOrElse(0) 
		require(assignmentNumber > 0, s"Assignment number:${args(0)} either can`t be converted to numeric value or is invalid")
		log.info(s"Going to launch assignment: ${assignmentNumber}")
		try
		{
			assignmentNumber match{
				case 1 => assignment1
				case 2 => assignment2
				case 3 => assignment3
				case 4 => assignment4
				/*case 5 => assignment5
				case 6 => assignment6*/
				case _ => throw new IllegalArgumentException(s"${assignmentNumber} was not recognized as valid assignment code.")
			}
		}catch{
			case e : IllegalArgumentException => log.error(e.getMessage)
			case _ : Throwable => log.error("Unknown error uccured")
		}
		
		spark.close()
		sys.exit
	}
	
	def assignment1 : Unit = {
		val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
		dataRDD.foreach(println)
	}
	
	def assignment2 : Unit = {
		val dataRDD : RDD[MyDate] = spark.sparkContext.parallelize(data)
		val dataDF : Dataset[Row] = dataRDD.toDF()
		dataDF.show()
	}
	
	def assignment3 : Unit = {
		val dataRDD : RDD[(Int, Int)] = spark.sparkContext.parallelize(dataTuple)
		dataRDD.foreach(println)
	}
	
	def assignment4 : Unit = {
		val dataRDD : RDD[(Int, Int)] = spark.sparkContext.parallelize(dataTuple)
		val dataDF : Dataset[Row] = dataRDD.toDF("year", "month")
		dataDF.show()
	}

}
