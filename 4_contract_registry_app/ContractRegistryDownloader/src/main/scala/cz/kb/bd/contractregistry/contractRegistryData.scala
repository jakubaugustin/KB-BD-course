package cz.kb.bd.contractregistry

import org.apache.spark.sql.{ Dataset, Row, SparkSession }

/**
 * Represents data from Contract Registry.
 * @param spark Spark Sesssion object
 * @param registryData Dataframe with registry data
 * @param attachmentsData Dataframe with attachments data
 * @param contractPartyData Dataframe with contract party data
 */
class ContractRegistryData(val spark: SparkSession, val registryData: Dataset[Row], val attachmentsData: Dataset[Row], val contractPartyData: Dataset[Row]) {

  //private[this] val downloader : FileDownloader = new FileDownloader(spark)

  /**
   * Writes table data for registry
   * @param path directory where output will be stored
   * @param fileType file type of the output
   */
  def writeRegistryTblData(path: String, fileType: String = FileType.CSV): Unit = {
    DFWriter.write(registryData, path, fileType)
  }

  /**
   * Writes table data for attachments
   * @param path directory where output will be stored
   * @param fileType file type of the output
   */
  def writeAttachmentsTblData(path: String, fileType: String = FileType.CSV): Unit = {
    DFWriter.write(attachmentsData, path, fileType)
  }

  /**
   * Writes table data for contract party
   * @param path directory where output will be stored
   * @param fileType file type of the output
   */
  def writeContractPartyTblData(path: String, fileType: String = FileType.CSV): Unit = {
    DFWriter.write(contractPartyData, path, fileType)
  }

  /**
   * Writes all table data (registry, attachments, contract_party)
   * @param registryDataPath directory where output will be stored
   * @param attachmentsDataPath directory where output will be stored
   * @param contractPartyDataPath directory where output will be stored
   * @param fileType file type of the output
   */
  def writeAllTblData(registryDataPath: String, attachmentsDataPath: String, contractPartyDataPath: String, fileType: String = FileType.CSV): Unit = {
    writeRegistryTblData(registryDataPath, fileType)
    writeAttachmentsTblData(attachmentsDataPath, fileType)
    writeContractPartyTblData(contractPartyDataPath, fileType)
  }

  /*def downloadAttachmentDocs(path : String) : Unit = {
		require(path != "", "download path can`t be empty")
		attachmentsData
			.select("idSmlouvy", "idVerze", "nazevSouboru", "odkaz")
			.collect()
			//Performance boost - parallel download
			.par
			.foreach(d => downloader.downloadFile(d(3).toString, s"${path}${d(0)}_${d(1)}_${d(2)}"))
	}*/

}