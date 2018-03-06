package cz.kb.bd.contractregistry

import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import org.apache.log4j.{LogManager, Logger}

object FileDownloader{
		
	/**
	*
	* @param source Source URL
	* @param target Target on local FS and or HDFS
	*/
	private[this] val log : Logger = LogManager.getRootLogger
	
	def apply(source : String, target : String) : Unit = {
		downloadFile(source, target)
	}
	
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
