package cz.kb.bd.assignments

import org.apache.log4j.{Logger, Level, BasicConfigurator}

/**
 * @author Jakub.Augustin
 */
object App {
	
	//CONSTANTS
	private[this] val START_YEAR : Int = 2016
	private[this] val END_YEAR : Int = 2019
	private[this] val START_MONTH : Int = 1
	private[this] val END_MONTH : Int = 13
	//Logger
	private[this] val log : Logger = Logger.getRootLogger
	
	case class MyDate(year : Int, month : Int)

	def main(args : Array[String]) {
	  //Development workaround
	  BasicConfigurator.configure()
	  
	  log.setLevel(Level.INFO)
	  log.info(s"Going to launch assignment: ${args(0)}")
	  try
	  {
		  args(0) match{
			  case "1" => assignment1
			  case "2" => assignment2
			  case "3" => assignment3
			  case "4" => assignment4
			  case "5" => assignment5
			  case "6" => assignment6
			  case _ => throw new IllegalArgumentException(s"${args(0)} was not recognized as valid assignment code.")
		  }
	  }catch{
		  case e : ArrayIndexOutOfBoundsException => log.error("No assignment specified?")
		  case e : IllegalArgumentException => log.error(e.getMessage)
		  case _ : Throwable => log.error("Unknown error uccured")
	  }
	  sys.exit
	}

	private def assignment1 : Unit = {
		log.info("Assignment 1: write all YEAR-MONTH combinations using println and  2 nested FOR loops")
		
		for(year <- START_YEAR until END_YEAR){
			for(month <- START_MONTH until END_MONTH){
				println(s"year: ${year}, month: ${month}")
			}
		}
	}
	
	private def assignment2 : Unit = {
		log.info("Assignment 2: write all YEAR-MONTH combinations using println and single FOR loop")
		
		for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH){
			println(s"year: ${year}, month: ${month}")
		}
	}
	
	
	private def assignment3 : Unit = {
		log.info("Assignment 3: write all YEAR-MONTH combinations using println and foreach function")
		
		(START_YEAR until END_YEAR)
			.foreach(year => (START_MONTH until END_MONTH)
				.foreach(month => println(s"year: ${year}, month: ${month}")))
	}
	
	private def assignment4 : Unit = {
		log.info("Assignment 4: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and Tuples")
		
		(START_YEAR until END_YEAR)
			.flatMap(year => (START_MONTH until END_MONTH)
				.map(month => (year, month)))
			.foreach(x => println(s"year: ${x._1}, month: ${x._2}"))
	}
	
	private def assignment5 : Unit = {
		log.info("Assignment 5: write all YEAR-MONTH combinations using println, flatMap, map, foreach functions and case class")
		
		(START_YEAR until END_YEAR)
			.flatMap(year => (START_MONTH until END_MONTH)
				.map(month => MyDate(year, month)))
			.foreach((x : MyDate) => println(s"year: ${x.year}, month: ${x.month}"))
	}
	
	private def assignment6 : Unit = {
		log.info("Assignment 6: write all YEAR-MONTH combinations using println function for comprehension")
		
		(for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield MyDate(year, month))
			.foreach((x : MyDate) => println(s"year: ${x.year}, month: ${x.month}"))
	}
}
