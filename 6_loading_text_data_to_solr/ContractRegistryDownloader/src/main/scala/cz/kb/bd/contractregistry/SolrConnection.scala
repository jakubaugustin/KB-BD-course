package cz.kb.bd.contractregistry

import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.response.UpdateResponse
import org.apache.solr.client.solrj.request.{ ContentStreamUpdateRequest, AbstractUpdateRequest }
import java.io.File
import scala.util.control.NonFatal

/**
 * Represents connection to SOLR server
 */
object SolrConnection {

  //Get SOLR connection URL from configuration
  private[this] val SOLR_URL: String = ConfigParser.getArgumentStringValue("application.solr_url")
  //Get update request path
  private[this] val SOLR_UPDATE_REQUEST_URL: String = ConfigParser.getArgumentStringValue("application.solr_update_request_url")
  //Represents SOLR connection object
  private[this] val solr: SolrServer = new HttpSolrServer(SOLR_URL)
  private[this] val updateRequest: ContentStreamUpdateRequest = new ContentStreamUpdateRequest(SOLR_UPDATE_REQUEST_URL)

  println(s"SOLR URL IS: $SOLR_URL")

  /**
   * Adds document to SOLR index
   * @param doc SOLR document to be added to index
   */
  def addDocument(doc: SolrInputDocument): UpdateResponse = {
    solr.add(doc)
  }

  /**
   * Send given file to SOLR for indexing
   * @param file Path on filesystem
   * @param solrId ID assigned to given doc
   */
  def indexFile(file: String, solrId: String): Unit = {
    updateRequest.addFile(new File(file), "")
    updateRequest.setParam("literal.id", solrId);
    updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true)
    solr.request(updateRequest)
  }

  /**
   * Commit changes to SOLR index
   */
  def commit(): UpdateResponse = {
    solr.commit(true, true)
  }

}