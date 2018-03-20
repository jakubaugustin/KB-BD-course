package cz.kb.bd.contractregistry

import org.apache.log4j.{Logger, Level, BasicConfigurator}
import org.apache.spark.sql.{SparkSession}

/** <strong>About:</strong>
	* <p>
	* This is simple application used to download data from Czech Contract registry.
	* Go to: https://smlouvy.gov.cz/ to see more information about the registry.
	* </p>
	*
	* @author Jakub.Augustin
	*/
object App {

	//////////////////////////////////////////////////////////////////////////////////
	//INITIAL CONSTANTS DEFINITION
	//TODO: Change to configuration properties
	/////////////////////////////////////////////////////////////////////////////////
	private[this] val SPARK_MASTER : String = "local[*]"
	private[this] val APP_NAME : String = "ContractRegistry"
	private[this] val LOG_LEVEL : String = "INFO"
	
	private[this] val XML_DOWNLOAD_PATH : String = "data/contract-registry-raw/"
	private[this] val ATTACHMENTS_DOWNLOAD_PATH : String = "data/attachments-data-raw-sample/"
	private[this] val PARSED_ATTACHMENTS_PATH : String = "data/attachment-docs-parsed/"
	private[this] val REGISTRY_DATA_PATH : String = "data/tbl-registry"
	private[this] val ATTACHMENTS_DATA_PATH : String = "data/tbl-attachments"
	private[this] val CONTRACT_PARTY_DATA_PATH : String = "data/tbl-contract-party"
	
	private[this] val WRITE_CODEC : String = FileType.CSV
	
	private[this] val START_YEAR : Int = 2016
	private[this] val END_YEAR : Int = 2019
	private[this] val START_MONTH : Int = 1
	private[this] val END_MONTH : Int = 13
	
	private[this] val SKIP_DOWNLOAD_XML : Boolean = false
	private[this] val SKIP_WRITE_PARSED_XML : Boolean = false
	private[this] val SKIP_DOWNLOAD_DOCS : Boolean = false
	private[this] val SKIP_PARSE_TO_TEXT : Boolean = false
	
	/**
	*Represents year-month pair
	* @param year year value
	* @param month month value
	**/
	case class BulkDate(year : Int, month : Int)
	
	/**
	* Main method used to run contract registry downloader
	* @param args program arguments array
	**/
	def main(args : Array[String]) {
		//Development workaround
		BasicConfigurator.configure()
		
		val log : Logger = Logger.getRootLogger
		log.setLevel(Level.INFO)
		log.info("Program start")
		
		//////////////////////////////////////////////////////////////////////////////////
		//STEP 0: Initiate spark session
		/////////////////////////////////////////////////////////////////////////////////
		val spark = 
			SparkSession.builder
			.appName(SPARK_MASTER)
			.config("spark.master", SPARK_MASTER)
			.getOrCreate()
		spark.sparkContext.setLogLevel(LOG_LEVEL)
		log.info("Contract registry App starting.")
		
		//////////////////////////////////////////////////////////////////////////////////
		//STEP 1: DOWNLOAD XML FROM WEB
		/////////////////////////////////////////////////////////////////////////////////
		val processDownload = (x : BulkDate) => new ContractBulkLink(x.year, x.month, XML_DOWNLOAD_PATH).download()
		(for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield BulkDate(year, month))
			.par
			.foreach(processDownload)
			
		//////////////////////////////////////////////////////////////////////////////////
		//STEP 2: Parse XML to structured data
		//////////////////////////////////////////////////////////////////////////////////
		if(!(SKIP_WRITE_PARSED_XML && SKIP_DOWNLOAD_DOCS))
		log.info("STEP 2: NOW PARSING XML")
		val registry : ContractRegistryData = new ContractRegistryXMLParser(spark, XML_DOWNLOAD_PATH).parse()
		
		//////////////////////////////////////////////////////////////////////////////////
		//STEP 3: Store parsed structured data
		//////////////////////////////////////////////////////////////////////////////////
		log.info("STEP 3: NOW WRITING ALL TBL DATA")
		if(!SKIP_WRITE_PARSED_XML){
			registry.writeAllTblData(REGISTRY_DATA_PATH, ATTACHMENTS_DATA_PATH, CONTRACT_PARTY_DATA_PATH, WRITE_CODEC)
		} else log.info("SKIPPED")
			
		sys.exit
	}

}
