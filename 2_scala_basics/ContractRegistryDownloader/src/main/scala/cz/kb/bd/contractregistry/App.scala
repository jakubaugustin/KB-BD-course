package cz.kb.bd.contractregistry

import org.apache.log4j.{Logger, Level, BasicConfigurator}

/** <strong>About:</strong>
  * <p>
  * This is simple application used to download data from Czech Contract registry.
  * Go to: https://smlouvy.gov.cz/ to see more information about the registry.
  * </p>
  *
  * @author Jakub.Augustin
  */
object App {
  
  private[this] val START_YEAR : Int = 2016
  private[this] val END_YEAR : Int = 2019
  private[this] val START_MONTH : Int = 1
  private[this] val END_MONTH : Int = 13
  private[this] val DOWNLOAD_PATH_PREFIX : String = "data/"
  
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
  
  val processDownload = (x : BulkDate) => new ContractBulkLink(x.year, x.month, DOWNLOAD_PATH_PREFIX).download()
  
  (for(year <- START_YEAR until END_YEAR; month <- START_MONTH until END_MONTH) yield BulkDate(year, month))
	  .par
	  .foreach(processDownload)
	  
  sys.exit
  }

}
