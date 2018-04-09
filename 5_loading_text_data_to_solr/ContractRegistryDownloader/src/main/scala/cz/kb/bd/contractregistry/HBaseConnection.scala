package cz.kb.bd.contractregistry

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Table
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.ResultScanner
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.conf.Configuration
import org.apache.log4j.Logger
import java.nio.file.{ Files, Paths }
import scala.util.control.NonFatal

object HBaseConnection {

  private[this] val config: Configuration = HBaseConfiguration.create();
  private[this] val log: Logger = Logger.getRootLogger
  private[this] val hbase: Connection = ConnectionFactory.createConnection(config)

  def putFile(tableName: String, filePath: String, rowName: String, colFamily: String, key: String): Unit = {
    try {
      val table: Table = hbase.getTable(TableName.valueOf(tableName))
      val p: Put = new Put(Bytes.toBytes(rowName))
      p.add(Bytes.toBytes(colFamily), Bytes.toBytes(key), Files.readAllBytes(Paths.get(filePath)));
      table.put(p)
    } catch {
      case NonFatal(e) => log.error(s"""HBase put operation failed: ${e.getMessage}\n${e.getStackTrace.mkString("\n")}""")
    }
  }
}