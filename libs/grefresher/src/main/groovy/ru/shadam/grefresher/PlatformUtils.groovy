package ru.shadam.grefresher;

/**
 * Taken from gretty-plugin
 *
 * @author akhikhl
 */
abstract class PlatformUtils {
  /**
   * @author akhikhl
   * @return Checks if current OS is windows
   */
  static boolean isWindows() {
    System.getProperty('os.name', 'generic').toLowerCase().indexOf('win') >= 0
  }
}
