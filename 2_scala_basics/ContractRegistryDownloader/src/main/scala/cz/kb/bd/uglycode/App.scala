package cz.kb.bd.uglycode

import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL

object App {
  def main(args : Array[String]) {
	(2016 until 2019).flatMap(y => (1 until 13).map(m => (y,m))).par.foreach(x => {
		if(! new File(s"""data/dump_${x._1}_${"%02d".format(x._2)}.xml""").exists()) {
			try {FileUtils.copyURLToFile(new URL(s"""https://data.smlouvy.gov.cz/dump_${x._1}_${"%02d".format(x._2)}.xml"""), new File(s"""data/dump_${x._1}_${"%02d".format(x._2)}.xml"""))}
			catch {case _: Throwable => println(s"""download failed for: https://data.smlouvy.gov.cz/dump_${x._1}_${"%02d".format(x._2)}.xml""")}
		}else{println(s"""download skipped for: https://data.smlouvy.gov.cz/dump_${x._1}_${"%02d".format(x._2)}.xml""")}
	})
	sys.exit
  }
}
