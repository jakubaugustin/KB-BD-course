package cz.kb.bd.contractregistry

import org.apache.log4j.{ Logger, Level, BasicConfigurator }
import org.apache.spark.sql.{ SparkSession }

/**
 * <strong>About:</strong>
 * <p>
 * This is simple application used to download data from Czech Contract registry.
 * Go to: https://smlouvy.gov.cz/ to see more information about the registry.
 * </p>
 *
 * @author Jakub.Augustin
 */
object App {

  //Development workaround
  //BasicConfigurator.configure()

  //////////////////////////////////////////////////////////////////////////////////
  //INITIAL CONSTANTS DEFINITION
  /////////////////////////////////////////////////////////////////////////////////
  private[this] val SPARK_MASTER: String = ConfigParser.getArgumentStringValue("spark.spark_master", """(local\[([0-9]+|\*)\]|yarn)""")
  private[this] val APP_NAME: String = ConfigParser.getArgumentStringValue("spark.app_name")
  private[this] val LOG_LEVEL: String = ConfigParser.getArgumentStringValue("spark.log_level")
  private[this] val WRITE_CODEC: String = ConfigParser.getArgumentStringValue("application.file_format", "(CSV|PARQUET)")
  private[this] val XML_CHARSET: String = ConfigParser.getArgumentStringValue("application.xml_charset")
  private[this] val XML_DOWNLOAD_PATH: String = ConfigParser.getArgumentStringValue("application.xml_download_path")
  private[this] val ATTACHMENTS_DOWNLOAD_PATH: String = ConfigParser.getArgumentStringValue("application.attachments_download_path")
  private[this] val PARSED_ATTACHMENTS_PATH: String = ConfigParser.getArgumentStringValue("application.parsed_attachments_path")
  private[this] val REGISTRY_DATA_PATH: String = ConfigParser.getArgumentStringValue("application.registry_data_path")
  private[this] val ATTACHMENTS_DATA_PATH: String = ConfigParser.getArgumentStringValue("application.attachments_data_path")
  private[this] val CONTRACT_PARTY_DATA_PATH: String = ConfigParser.getArgumentStringValue("application.contract_party_data_path")
  private[this] val START_YEAR: Int = ConfigParser.getArgumentIntValue("application.start_year")
  private[this] val END_YEAR: Int = ConfigParser.getArgumentIntValue("application.end_year")
  private[this] val START_MONTH: Int = ConfigParser.getArgumentIntValue("application.start_month")
  private[this] val END_MONTH: Int = ConfigParser.getArgumentIntValue("application.end_month")
  private[this] val SKIP_DOWNLOAD_XML: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_download_xml")
  private[this] val SKIP_WRITE_PARSED_XML: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_write_parsed_xml")
  private[this] val SKIP_DOWNLOAD_DOCS: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_download_docs")
  private[this] val SKIP_PARSE_TO_TEXT: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_parse_to_text")
  private[this] val SKIP_WRITE_CORE_TABLES: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_write_core_tables")
  private[this] val SKIP_SOLR_INDEXING: Boolean = ConfigParser.getArgumentBooleanValue("skips.skip_solr_indexing")

  /**
   * Represents year-month pair
   * @param year year value
   * @param month month value
   */
  case class BulkDate(year: Int, month: Int)

  /**
   * Main method used to run contract registry downloader
   * @param args program arguments array
   */
  def main(args: Array[String]) {

    val log: Logger = Logger.getRootLogger
    log.setLevel(Level.INFO)
    log.info("Program starting")

    //SolrConnection.indexFile("""D:\Libraries\Documents\KB\BDP\KB-BD-course\6_loading_text_data_to_solr\ContractRegistryDownloader\data\attachment-docs-raw\157_157_Smlouva pro cast VZ c  2 KAROSA PARDUBICE_155610221_sing.pdf""", "157_157_Smlouva pro cast VZ c  2 KAROSA PARDUBICE_155610221_sing.pdf")

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
    //STEP 1: Download XMLs from web
    /////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 1: Download XMLs from web")
    if (!SKIP_DOWNLOAD_XML) {
      val processDownload = (x: BulkDate) => new ContractBulkLink(x.year, x.month, XML_DOWNLOAD_PATH).download()
      (for (year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield BulkDate(year, month))
        .par
        .foreach(processDownload)
    } else {
      log.info("SKIPPED STEP 1")
    }

    //////////////////////////////////////////////////////////////////////////////////
    //STEP 2: Parse XML to structured data
    //////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 2: Parse XML to structured data")
    val registry: ContractRegistryData = new ContractRegistryXMLParser(spark, XML_DOWNLOAD_PATH, XML_CHARSET).parse()

    //////////////////////////////////////////////////////////////////////////////////
    //STEP 3: Store parsed structured data
    //////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 3: Store parsed structured data")
    if (!SKIP_WRITE_PARSED_XML) {
      registry.writeAllTblData(REGISTRY_DATA_PATH, ATTACHMENTS_DATA_PATH, CONTRACT_PARTY_DATA_PATH, WRITE_CODEC)
    } else log.info("SKIPPED STEP 3")

    //////////////////////////////////////////////////////////////////////////////////
    //STEP 4: Transform and clean the data sets
    //////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 4: Transform and clean the data sets")
    if (!SKIP_WRITE_CORE_TABLES) {
      registry.writeCoreTables()
    } else log.info("SKIPPED STEP 4")

    //////////////////////////////////////////////////////////////////////////////////
    //STEP 5: Index data in SOLR
    //////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 5: Index data in SOLR")
    if (!SKIP_SOLR_INDEXING) {
      registry.indexInSolr()
    } else log.info("SKIPPED STEP 5")

    //////////////////////////////////////////////////////////////////////////////////
    //STEP 6: Download attachments and put them to HBase
    //////////////////////////////////////////////////////////////////////////////////
    log.info("STEP 6: Download attachments and put them to HBase")
    if (!SKIP_DOWNLOAD_DOCS) {
      registry.downloadAttachmentDocsToHBase(ATTACHMENTS_DOWNLOAD_PATH)
    } else log.info("SKIPPED STEP 6")

    sys.exit
  }

}
