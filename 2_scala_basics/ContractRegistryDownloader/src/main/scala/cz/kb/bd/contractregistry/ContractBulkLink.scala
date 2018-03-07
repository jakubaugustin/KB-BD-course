package cz.kb.bd.contractregistry

import java.io.File

/** Represents XML document containing contracts for given month/year
  * @param year Represents bulk year
  * @param month Represents bulk month
  * @param downloadPrefix This will be appended before download path
  */
class ContractBulkLink (year : Int, month : Int, downloadPrefix : String = "") extends Downloadable {
	
	require(month >= 1 && month <= 12, "Invalid month specified - must be within 1-12")
	require(year >= 2016, "Invalid year specified - must be greather than 2016")
	
	private[this] val monthFormatted : String = "%02d".format(month)
	private[this] val downloadSource : String = s"https://data.smlouvy.gov.cz/dump_${year}_${monthFormatted}.xml";
	private[this] val downloadTarget : String = s"${downloadPrefix}dump_${year}_${monthFormatted}.xml"
	
	/** Download document
	  */
	def download () : Unit = {		
		FileDownloader(downloadSource, downloadTarget)
	}
	/** Check if document exists on download location
	  */
	def isDownloaded () : Boolean = {
		new File(downloadTarget).exists
	}
}