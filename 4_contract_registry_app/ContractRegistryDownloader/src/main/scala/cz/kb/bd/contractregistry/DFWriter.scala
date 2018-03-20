package cz.kb.bd.contractregistry

import org.apache.spark.sql.{SaveMode, Dataset, Row}


object DFWriter {

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
				.option("header", "true")
				.option("delimiter", "~")
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
				.option("compression.codec", "snappy")
				.parquet(path)
		} catch {
			case _: Throwable => s"an error occured while writing Parquet"
		}
	}	
}