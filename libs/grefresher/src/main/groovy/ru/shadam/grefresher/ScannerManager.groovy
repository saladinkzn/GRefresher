package ru.shadam.grefresher

import org.eclipse.jetty.util.Scanner

/**
 * @author sala
 */
class ScannerManager {
  Scanner scanner

  ScannerManager(List<File> scanDirs, int scanInterval, Closure<List<String>> bulkListener) {
    scanner = new Scanner()
    scanner.with {
      scanDirs.each { addScanDir(it)}
      addListener(bulkListener as Scanner.BulkListener)
      reportExistingFilesOnStartup = false
      it.scanInterval = scanInterval
      scanDepth = -1
    }
  }

  def start() {
    scanner.doStart()
  }

  def stop() {
    scanner.doStop()
  }
}
