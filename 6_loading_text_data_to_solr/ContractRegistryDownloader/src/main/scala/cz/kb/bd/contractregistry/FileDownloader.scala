package cz.kb.bd.contractregistry

import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import org.apache.log4j.{ LogManager, Logger }
import org.apache.hadoop.fs.{ FileSystem, Path }
import org.apache.spark.sql.{ SparkSession }

/** This object enable URL content to be downloaded. */
object FileDownloader {
  private[this] val USING_HDFS: Boolean = ConfigParser.getArgumentBooleanValue("application.using_hdfs")

  private[this] val log: Logger = LogManager.getRootLogger
  private[this] val hadoopFS: FileSystem = FileSystem.get(SparkSession.builder.getOrCreate.sparkContext.hadoopConfiguration)

  /**
   * Download source document to target
   *
   * @param source Source URL
   * @param target Target on local FS and or HDFS
   */
  def apply(source: String, target: String): Unit = {
    downloadFile(source, target)
  }

  /**
   * Download source document to target.
   * IF HDFS is configured, this method will also upload data to HDFS.
   *
   * @param source Source URL
   * @param target Target on local FS and or HDFS
   */
  def downloadFile(source: String, target: String, localOnly: Boolean = false): Unit = {
    require(source != "", "source can`t be empty")
    require(target != "", "target can`t be empty")

    log.info(s"processing download from: ${source}")
    try {
      val url = new URL(source)
      val file = new File(target)
      if (!file.exists()) {
        FileUtils.copyURLToFile(url, file)
        log.info(s"downloaded file from ${source} to ${target}")
      } else {
        log.info(s"skipped download of: ${source} because it exists")
      }
    } catch {
      case _: Throwable => log.warn(s"download failed for URL: ${source}")
    }
    if (USING_HDFS && !localOnly) {
      try {
      if (!hadoopFS.exists(new Path(target))) {
        if (new File(target).exists()) {
          hadoopFS.moveFromLocalFile(new Path(target), new Path(target))
          log.info(s"uploaded ${target} to hdfs")
        } else {
          log.warn(s"Could not upload ${target} to hdfs, because it does not exist locally.")
        }
      }
      } catch {
        case _: Throwable => log.warn(s"moving to HDFS failed for file: ${target}")
      }
    }
  }
  
  def deleteFile(filename : String) : Boolean ={
    new File(filename).delete()
  }
}
