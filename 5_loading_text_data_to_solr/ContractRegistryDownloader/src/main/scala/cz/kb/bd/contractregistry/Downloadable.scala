package cz.kb.bd.contractregistry

trait Downloadable {
	def download : Unit;
	def isDownloaded : Boolean;
}