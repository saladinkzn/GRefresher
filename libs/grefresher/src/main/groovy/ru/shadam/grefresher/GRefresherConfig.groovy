package ru.shadam.grefresher

/**
 * @author sala
 */
class GRefresherConfig {
  boolean debug = false
  Boolean debugSuspend = false
  Integer debugPort = 5005
  //
  String mainClassName = null
  List<String> jvmArgs = []
  Map<String, String> systemProperties = [:]
  //
  Integer scanInterval = 1
}
