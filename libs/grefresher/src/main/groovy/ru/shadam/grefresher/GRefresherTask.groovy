package ru.shadam.grefresher
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.GradleConnector

/**
 * @author sala
 */
class GRefresherTask extends DefaultTask {
  volatile Process process
  volatile Thread runningThread

  @TaskAction
  def hello() {
    GRefresherConfig config = project."${GRefresherPlugin.GREFRESHER_EXTENSION}".config
    //
    logger.info "mainClassName: ${config.mainClassName}"
    //
    def hint = 'Press \'q\' or \'Q\' to stop application or any other key to restart'
    System.out.println hint
    runningThread = startProcess(config)
    ScannerManager scannerManager = getScannerManager(config)
    try {
      scannerManager?.start()
      infinite:
      while (true) {
        while (System.in.available() > 0) {
          def input = System.in.read()
          //
          if (input >= 0) {
            char c = (char) input
            if (c == 'q' || c == 'Q') {
              process.destroy()
              runningThread.join()
              break infinite
            } else {
              restartApplication(config)
              System.out.println hint
              // Dumping input
              while (System.in.available() > 0) {
                long available = System.in.available()
                for (int i = 0; i < available; i++) {
                  if (System.in.read() == -1) {
                    break
                  }
                }
              }
            }
          }
        }
        Thread.sleep(500)
      }
    } finally {
      scannerManager.stop()
    }
  }

  synchronized def restartApplication(GRefresherConfig config) {
    process.destroy()
    runningThread.join()
    // calling rebuild
    def connection = GradleConnector.newConnector().useInstallation(project.gradle.gradleHomeDir).forProjectDirectory(project.projectDir).connect()
    try {
      connection.newBuild().forTasks('classes').run()
    } finally {
      connection.close()
    }
    runningThread = startProcess(config)
  }

  private ScannerManager getScannerManager(GRefresherConfig config) {
    if(!config.scanInterval) {
      logger.warn "config.scanInterval = ${config.scanInterval}. Scanning disabled!"
      return null
    }
    def collectSourceSets
    collectSourceSets = { Project p ->
      List<Project> projects = p.configurations['compile'].dependencies.withType(ProjectDependency).collect { it.dependencyProject }
      return p.sourceSets.main.allSource.srcDirs + projects.collect({ collectSourceSets(it) }).flatten()
    }
    def sourceSets = collectSourceSets(project)
    sourceSets.each {
      logger.debug "Collected sourceDir: ${it}"
    }
    return new ScannerManager(sourceSets as List<File>, config.scanInterval, {
      restartApplication(config)
    })
  }

  private Thread startProcess(GRefresherConfig config) {
    Thread.start {
      logger.debug 'Starting new process'
      String javaExe = isWindows() ? 'java.exe' : 'java'
      String javaPath = new File(System.getProperty("java.home"), "bin/$javaExe").absolutePath
      //
      List<String> debugArg
      if(config.debug) {
        debugArg = ["-Xrunjdwp:transport=dt_socket,server=y,suspend=${config.debugSuspend ? 'y' : 'n'},address=${config.debugPort}".toString()]
      } else {
        debugArg = []
      }
      //
      def classPath = getRunnerClassPath()
      classPath = classPath.collect { it.absolutePath }.join(System.getProperty('path.separator'))
      //
      def procParams = [javaPath] + debugArg + config.jvmArgs + config.systemProperties.collect { k, v -> "-D$k=$v" } + ['-cp', classPath, config.mainClassName]
      process = ProcessBuilder.newInstance()
              .command(procParams as List<String>)
              .redirectOutput(ProcessBuilder.Redirect.INHERIT)
              .redirectError(ProcessBuilder.Redirect.INHERIT)
              .start()
      process.waitFor()
      logger.debug 'Process stopped'
    }
  }

  protected Collection<File> getRunnerClassPath() {
    (project.sourceSets.main.output + project.configurations['compile']).files
  }

  /**
   * @author akhikhl
   * @return Checks if current OS is windows
   */
  static boolean isWindows() {
    System.getProperty('os.name', 'generic').toLowerCase().indexOf('win') >= 0
  }
}
