package cz.kb.bd.contractregistry

import org.apache.log4j.{Logger, Level, BasicConfigurator}

/**
 * @author ${user.name}
 */
object App {
  
  private[this] val DOWNLOAD_PATH_PREFIX : String = "data/"
  
  case class BulkDate(year : Int, month : Int)
  
  def main(args : Array[String]) {
  //Development workaround
  BasicConfigurator.configure()
  
  val log : Logger = Logger.getRootLogger
  log.setLevel(Level.INFO)
  log.info("Program start")
  
  val processDownload = (x : BulkDate) => new ContractBulkLink(x.year, x.month, DOWNLOAD_PATH_PREFIX).download()
  
  (for(year <- 2016 until 2019; month <- 1 until 13) yield BulkDate(year, month))
	  .par
	  .foreach(processDownload)
  sys.exit
  }

}
