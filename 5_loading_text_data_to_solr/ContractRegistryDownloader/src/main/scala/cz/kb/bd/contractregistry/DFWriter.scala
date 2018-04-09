package cz.kb.bd.contractregistry

import org.apache.spark.sql.{ SaveMode, Dataset, Row, SparkSession }
import org.apache.spark.sql.types.StructField
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.response.UpdateResponse
import org.apache.log4j.Logger
import scala.util.control.NonFatal

/**
 * Helper object for writing Dataframes to disk
 */
object DFWriter {

  private[this] val CSV_DELIMITER: String = ConfigParser.getArgumentStringValue("application.csv_delimiter")
  private[this] val CSV_WRITE_HEADER: Boolean = ConfigParser.getArgumentBooleanValue("application.csv_write_header")
  private[this] val PARQUET_COMPRESSION_CODEC: String = ConfigParser.getArgumentStringValue("application.parquet_compression_coded")

  private val log: Logger = Logger.getRootLogger

  /**
   * Writes Dataframe to disk
   * @param df Dataframe to be written
   * @param path Path where the dataframe will be stored
   * @param fileType File type of data written
   * @param numPartitions before writing the data Dataframe will be repartitioned to given number of partitions
   */
  def write(df: Dataset[Row], path: String, fileType: String = FileType.CSV, numPartitions: Int = 1): Unit = {
    if (fileType == FileType.CSV) writeDataFrameAsCSV(df, path, numPartitions)
    else if (fileType == FileType.PARQUET) DFWriter.writeDataFrameAsParquet(df, path, numPartitions)
    else throw new NotImplementedError("fileType not implemented yet, currently only CSV & Parquet is supported")
  }

  private def writeDataFrameAsCSV(df: Dataset[Row], path: String, numPartitions: Int = 1): Unit = {
    try {
      df
        //.repartition(numPartitions)
        .write
        .mode(SaveMode.Overwrite)
        .format("com.databricks.spark.csv")
        .option("header", CSV_WRITE_HEADER)
        .option("delimiter", CSV_DELIMITER)
        .option("quoteMode", "ALL")
        .option("quote", "\"")
        .save(path)
    } catch {
      case _: Throwable => s"an error occured while writing CSV"
    }
  }

  private def writeDataFrameAsParquet(df: Dataset[Row], path: String, numPartitions: Int = 1): Unit = {
    try {
      df
        .repartition(numPartitions)
        .write
        .mode(SaveMode.Overwrite)
        .option("compression.codec", PARQUET_COMPRESSION_CODEC)
        .parquet(path)
    } catch {
      case _: Throwable => s"an error occured while writing Parquet"
    }
  }

  /**
   * Writes Dataframe content as Metastore table (Hive/Impala)
   *
   * @param df Datarfame to be written as Metastore table
   * @param databaseName Name of target database - must exist
   * @param tableName Name of target table
   * @param dropIfExists Specifies whether target table should be dropped if exists
   */
  def writeDataFrameAsTable(df: Dataset[Row], databaseName: String, tableName: String, dropIfExists: Boolean = true): Unit = {
    val wholeName: String = s"${databaseName}.${tableName}"
    val tmpName: String = s"tmp_${tableName}"
    df.createTempView(tmpName)
    try {
      if (dropIfExists) SparkSession.builder.getOrCreate.sql(s"drop table if exists ${wholeName}")
      SparkSession.builder.getOrCreate.sql(s"create table ${wholeName} stored as parquet as select * from ${tmpName} ")
    } catch {
      case NonFatal(e) => log.error(s"""Creating Metastore table failed: ${e.getMessage}\n${e.getStackTrace.mkString("\n")}""")
    } finally {
      SparkSession.builder.getOrCreate.catalog.dropTempView(tmpName)
    }
  }

  /**
   * Writes a Dataframe contents to SOLR
   * @param df Datarfame to be written to SOLR
   */
  def writeDataFrameToSOLR(df: Dataset[Row]): Unit = {
    df
      .foreach((r: Row) => {
        val doc: SolrInputDocument = new SolrInputDocument()
        r.schema.fields.foreach((f: StructField) => {
          doc.setField(f.name, r.getAs[String](f.name))
        })
        //try {
        val opResponse: UpdateResponse = SolrConnection.addDocument(doc)
        log.debug(s"Document loaded to SOLR with result: ${opResponse.toString()}")
        // } catch {
        //   case NonFatal(e) => log.error(s"Adding document to SOLR failed: ${e.getStackTrace.mkString("\n")}")
        // }
      })
    SolrConnection.commit()
  }
}