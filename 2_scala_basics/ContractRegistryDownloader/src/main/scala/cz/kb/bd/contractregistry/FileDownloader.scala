package cz.kb.bd.contractregistry

import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import org.apache.log4j.{LogManager, Logger}

/** This object enable URL content to be downloaded. */
object FileDownloader{

	private[this] val log : Logger = LogManager.getRootLogger
	
	/** Download source document to target
	*
	* @param source Source URL
	* @param target Target on local FS and or HDFS
	*/
	def apply(source : String, target : String) : Unit = {
		downloadFile(source, target)
	}
	
	/** Download source document to target
	*
	* @param source Source URL
	* @param target Target on local FS and or HDFS
	*/
	def downloadFile(source : String, target : String) : Unit = {
		require(source != "", "source can`t be empty")
		require(target != "", "target can`t be empty")
		
		log.info(s"processing download from: ${source}")
		try {
			val url = new URL(source)
			val file = new File(target)
			if(!file.exists()){
				FileUtils.copyURLToFile(url, file)		
				log.info(s"downloaded file from ${source} to ${target}")
			} else {
				log.info(s"skipped download of: ${source} because it exists")
			}
		} catch {
			case _: Throwable => log.warn(s"download failed for URL: ${source}")
		}
	}
}
