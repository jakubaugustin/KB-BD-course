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

  private[this] val HBASE_ATTACHMENTS_TABLE: String = ConfigParser.getArgumentStringValue("application.hbase_attachments_table")
  private[this] val HBASE_ATTACHMENTS_TABLE_COL_FAMILY: String = ConfigParser.getArgumentStringValue("application.hbase_attachments_table_col_family")
  private[this] val HBASE_ATTACHMENTS_TABLE_KEY: String = ConfigParser.getArgumentStringValue("application.hbase_attachments_table_key")
  private[this] val METASTORE_CORE_DB: String = ConfigParser.getArgumentStringValue("application.metastore_core_db")
  private[this] val METASTORE_REGISTRY_TABLE_NAME: String = ConfigParser.getArgumentStringValue("application.metastore_registry_table_name")
  private[this] val METASTORE_ATTACHMENTS_TABLE_NAME: String = ConfigParser.getArgumentStringValue("application.metastore_attachments_table_name")
  private[this] val METASTORE_CONTRACT_PARTY_TABLE_NAME: String = ConfigParser.getArgumentStringValue("application.metastore_contract_party_table_name")

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

  /**
   * Select attachment links from attachmentsData Dataframe.
   * For each link it`s target will be downloaded locally, put to HBase and deleted from local file system
   *
   * @param tmpPath Temporary place on local file system for downloaded documents
   */
  def downloadAttachmentDocsToHBase(tmpPath: String): Unit = {
    require(tmpPath != "", "download path can`t be empty")
    attachmentsData
      .select("id", "nazevSouboru", "odkaz")
      .collect()
      //Performance boost - parallel download
      .par
      .foreach((d: Row) => {
        //Download path: Path prefix followed by: id_nazevSouboru
        val downloadPath: String = s"${tmpPath}${d(0)}_${d(1)}}"
        FileDownloader.downloadFile(d(2).toString, downloadPath, true)
        val rowName: String = s"${d(0)}_${d(1)}"
        HBaseConnection.putFile(HBASE_ATTACHMENTS_TABLE, downloadPath, rowName, HBASE_ATTACHMENTS_TABLE_COL_FAMILY, HBASE_ATTACHMENTS_TABLE_KEY)
        FileDownloader.deleteFile(downloadPath)
      })
  }

  def writeCoreTables(): Unit = {
    registryData.createOrReplaceTempView("registry")
    val registryCore: Dataset[Row] = spark.sql("""
      select
         id
         , idSmlouvy
         , idVerze
         , cast(casZverejneni as timestamp) casZverejneni
         , odkaz
         , cast(platnyZaznam as boolean) platnyZaznam
         , cisloSmlouvy
         , hodnota
         , upper(mena) mena
         , cast(datumUzavreni as timestamp) datumUzavreni
         , hodnotaBezDph
         , hodnotaVcetneDph
         , navazanyZaznam
         , predmet
         , schvalil
         , adresa
         , datovaSchranka
         , lpad(ico, 8 , "0") ico
         , nazev
         , cast(platce as boolean) platce
         , utvar
      from registry
    """)
    DFWriter.writeDataFrameAsTable(registryCore, METASTORE_CORE_DB, METASTORE_REGISTRY_TABLE_NAME)

    contractPartyData.createOrReplaceTempView("contractParty")
    val contractPartyCore: Dataset[Row] = spark.sql("""
      select
        concat(id, regexp_replace(nazev, "\\s", "_"), "_", row_number() over (partition by id, ico order by 1)) id
        , id parentId
        , adresa
        , datovaSchranka
        , lpad(ico, 8, "0") ico
        , nazev
        , cast(prijemce as boolean) prijemce
        , utvar
      from contractParty
     """)
    DFWriter.writeDataFrameAsTable(contractPartyCore, METASTORE_CORE_DB, METASTORE_CONTRACT_PARTY_TABLE_NAME)

    attachmentsData.createOrReplaceTempView("attachments")
    val attachmentsCore: Dataset[Row] = spark.sql("""
    select
      id parentId
      , _algoritmus algoritmus
      , _VALUE hash
      , nazevSouboru
      , odkaz
    from attachments
    """)
    DFWriter.writeDataFrameAsTable(attachmentsCore, METASTORE_CORE_DB, METASTORE_ATTACHMENTS_TABLE_NAME)

  }
  
  /**
   * Sends data in cleaned structures to SOLR for indexing
   */
  def indexInSolr() : Unit = {
    registryData.createOrReplaceTempView("registry")
    val registryCore: Dataset[Row] = spark.sql("""
      select
         id
         , idSmlouvy
         , idVerze
         , date_format(cast(casZverejneni as timestamp), "y-MM-dd'T'hh:mm:ss.SSS'Z'") casZverejneni
         , odkaz
         , cast(platnyZaznam as boolean) platnyZaznam
         , cisloSmlouvy
         , hodnota
         , upper(mena) mena
         , date_format(cast(datumUzavreni as timestamp), "y-MM-dd'T'hh:mm:ss.SSS'Z'") datumUzavreni
         , hodnotaBezDph
         , hodnotaVcetneDph
         , navazanyZaznam
         , predmet
         , schvalil
         , adresa
         , datovaSchranka
         , lpad(ico, 8 , "0") ico
         , nazev
         , cast(platce as boolean) platce
         , utvar
         , "REGISTRY-RECORD" type
      from registry
    """)
    DFWriter.writeDataFrameToSOLR(registryCore)
    
    contractPartyData.createOrReplaceTempView("contractParty")
    val contractPartyCore: Dataset[Row] = spark.sql("""
      select
        concat(id, regexp_replace(nazev, "\\s", "_"), "_", row_number() over (partition by id, ico order by 1)) id
        , id parentId
        , adresa
        , datovaSchranka
        , lpad(ico, 8, "0") ico
        , nazev
        , cast(prijemce as boolean) prijemce
        , utvar
        , "CONTRACT-PARTY-RECORD" type
      from contractParty
     """)
     DFWriter.writeDataFrameToSOLR(contractPartyCore)
  }

}