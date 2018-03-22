package cz.kb.bd.contractregistry

import org.apache.spark.sql.{SparkSession}
import com.databricks.spark.xml._
import org.apache.spark.sql.{Column, Dataset, Row}
import org.apache.spark.sql.functions.{explode, udf, col}
import org.apache.spark.sql.types.{StructType}

/**
 * Parses XML from Contract Registry containing it`s metadata to "table like" data in 3rd normal form
 * @param spark Spark session object
 * @param xmlSource mask containing files to be parsed. This can be file reference, directory or mask with wildcard.
 * @param charset charset of XML files 
 */
class ContractRegistryXMLParser (val spark : SparkSession, val xmlSource : String, val charset : String = "ASCII"){

	require(xmlSource != "", "xmlSource can`t be empty")
	
	/**
	 * Perform parsing itself
	 */
	def parse () : ContractRegistryData = {
		import spark.implicits._
		
		val rawData = spark
			.read
			.format("com.databricks.spark.xml")
			.option("rowTag", "zaznam")
			.option("mode", "PERMISSIVE")
			.option("charset", charset)
			.option("ignoreSurroundingSpaces", true)
			.load(xmlSource)
			//.repartition(100)
			
		rawData.cache()

		val flatData = rawData
			.select(flattenSchema(rawData.schema):_*)
		
		val attachmentsDataTmp = flatData
			.drop("casZverejneni")
			.drop("odkaz")
			.drop("platnyZaznam")
			.drop("cisloSmlouvy")
			.drop("hodnota")
			.drop("mena")
			.drop("datumUzavreni")
			.drop("hodnotaBezDph")
			.drop("hodnotaVcetneDph")
			.drop("navazanyZaznam")
			.drop("predmet")
			.drop("smluvniStrana")
			.drop("schvalil")
			.drop("adresa")
			.drop("datovaSchranka")
			.drop("ico")
			.drop("nazev")
			.drop("platce")
			.drop("utvar")
			.withColumn("prilohy", explode(flatData.col("priloha")))
			
		val contractPartyDataTmp = flatData
			.drop("casZverejneni")
			.drop("odkaz")
			.drop("platnyZaznam")
			.drop("priloha")
			.drop("cisloSmlouvy")
			.drop("hodnota")
			.drop("mena")
			.drop("datumUzavreni")
			.drop("hodnotaBezDph")
			.drop("hodnotaVcetneDph")
			.drop("navazanyZaznam")
			.drop("predmet")
			.drop("schvalil")
			.drop("adresa")
			.drop("datovaSchranka")
			.drop("ico")
			.drop("nazev")
			.drop("platce")
			.drop("utvar")
			.withColumn("smluvniStrany", explode(flatData.col("smluvniStrana")))
			
		val registryDataTmp = flatData
			.drop("smluvniStrana")
			.drop("priloha")
		
		val attachmentsData = attachmentsDataTmp
			.select(flattenSchema(attachmentsDataTmp.schema):_*)
			.drop("priloha")
			.drop("_corrupt_record")
			
		val contractPartyData = contractPartyDataTmp
			.select(flattenSchema(contractPartyDataTmp.schema):_*)
			.drop("smluvniStrana")
			.drop("_corrupt_record")
			
		val registryData = registryDataTmp
			.drop("_corrupt_record")
			
		new ContractRegistryData(spark, registryData, attachmentsData, contractPartyData)	
	}
	
	/**
	 * Flattens schema containing non-scalar types
	 * @param schema Original schema to be flattened
	 * @param prefix This will be prepended to column names
	 */
	private def flattenSchema(schema: StructType, prefix: String = "") : Array[Column] = {
		schema.fields.flatMap(f => {
			val colName = if (prefix == "") f.name else (prefix + "." + f.name)

			f.dataType match {
				case st: StructType => flattenSchema(st, colName)
				case _ => Array(col(colName))
			}
		})
	}
	
}