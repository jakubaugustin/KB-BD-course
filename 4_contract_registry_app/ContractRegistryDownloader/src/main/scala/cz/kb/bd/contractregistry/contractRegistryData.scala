package cz.kb.bd.contractregistry

import org.apache.spark.sql.{Dataset, Row, SparkSession}

class ContractRegistryData (val spark : SparkSession, val registryData : Dataset[Row], val attachmentsData : Dataset[Row], val contractPartyData : Dataset[Row]) {
	
	//private[this] val downloader : FileDownloader = new FileDownloader(spark)
	
	def writeRegistryTblData(path : String, fileType : String = FileType.CSV) : Unit = {
		DFWriter.write(registryData, path, fileType)
	}

	def writeAttachmentsTblData(path : String, fileType : String = FileType.CSV) : Unit = {
		DFWriter.write(attachmentsData, path, fileType)
	}

	def writeContractPartyTblData(path : String, fileType : String = FileType.CSV) : Unit = {
		DFWriter.write(contractPartyData, path, fileType)
	}
	
	
	def writeAllTblData(registryDataPath : String, attachmentsDataPath : String, contractPartyDataPath : String, fileType : String = FileType.CSV) : Unit = {
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