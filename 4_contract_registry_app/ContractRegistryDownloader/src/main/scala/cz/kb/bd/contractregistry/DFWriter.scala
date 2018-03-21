package cz.kb.bd.contractregistry

import org.apache.spark.sql.{SaveMode, Dataset, Row}

/**
 * Helper object for writing Dataframes to disk
 */
object DFWriter {
  
  private[this] val CSV_DELIMITER : String = ConfigParser.getArgumentStringValue("application.csv_delimiter")
  private[this] val CSV_WRITE_HEADER : Boolean = ConfigParser.getArgumentBooleanValue("application.csv_write_header")
  private[this] val PARQUET_COMPRESSION_CODEC : String = ConfigParser.getArgumentStringValue("application.parquet_compression_coded")

   /**
    * Writes Dataframe to disk
    * @param df Dataframe to be written
    * @param path Path where the dataframe will be stored
    * @param fileType File type of data written
    * @param numPartitions before writing the data Dataframe will be repartitioned to given number of partitions
    */
	def write(df: Dataset[Row], path: String, fileType : String = FileType.CSV, numPartitions : Int = 1) : Unit = {
		if (fileType == FileType.CSV) writeDataFrameAsCSV(df, path, numPartitions)
		else if (fileType == FileType.PARQUET) DFWriter.writeDataFrameAsParquet(df, path, numPartitions)
		else throw new NotImplementedError("fileType not implemented yet, currently only CSV & Parquet is supported")
	}

	private def writeDataFrameAsCSV(df: Dataset[Row], path: String, numPartitions : Int = 1) : Unit = {
		try {
			df
				.repartition(numPartitions)
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
	
	private def writeDataFrameAsParquet(df: Dataset[Row], path: String, numPartitions : Int = 1) : Unit = {
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
}