package ru.shadam.grefresher
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.GradleConnector

/**
 * @author sala
 */
class GRefresherTask extends DefaultTask {
  volatile Process process

  @TaskAction
  def hello() {
    String mainClassName = project."${GRefresherPlugin.GREFRESHER_EXTENSION}".mainClassName
    List<String> jvmArgs = project."${GRefresherPlugin.GREFRESHER_EXTENSION}".jvmArgs ?: []
    Map<String, String> systemProperties = project."${GRefresherPlugin.GREFRESHER_EXTENSION}".systemProperties ?: [:]
    //
    logger.info "mainClassName: ${mainClassName}"
    //
    System.out.println 'Press any key to restart'
    def startThread = startProcess(mainClassName, jvmArgs, systemProperties);
    infinite:
    while (true){
      while (System.in.available() > 0) {
        def input = System.in.read()
        //
        if(input >= 0) {
          char c = (char)input
          if(c == '\n') {
            logger.debug "Got restart command: ${(char)input}!"
            //
            logger.debug 'Destroying process'
            process.destroy()
            logger.debug 'Joining running thread'
            startThread.join()
            logger.debug 'Running thread joined'
            //
            // calling rebuild
            def connection = GradleConnector.newConnector().useInstallation(project.gradle.gradleHomeDir).forProjectDirectory(project.projectDir).connect()
            try {
              connection.newBuild().forTasks('classes').run()
            } finally {
              connection.close()
            }
            startThread = startProcess(mainClassName, jvmArgs, systemProperties);


            dumpInput()
          }
        }

      }
      Thread.sleep(500)
    }
  }

  private static void dumpInput() {
    // dumping other input
    while (System.in.available() > 0) {
      long available = System.in.available()
      for (int i = 0; i < available; i++) {
        if (System.in.read() == -1) {
          break
        }
      }
    }
  }

  private Thread startProcess(String mainClassName, List<String> jvmArgs, Map<String, String> systemProperties) {
    Thread.start {
      logger.debug 'Starting new process'
      String javaExe = isWindows() ? 'java.exe' : 'java'
      String javaPath = new File(System.getProperty("java.home"), "bin/$javaExe").absolutePath
      def classPath = getRunnerClassPath()
      classPath = classPath.collect { it.absolutePath }.join(System.getProperty('path.separator'))
      def procParams = [javaPath] + jvmArgs + systemProperties.collect { k, v -> "-D$k=$v" } + ['-cp', classPath, mainClassName]
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

  static boolean isWindows() {
    System.getProperty('os.name', 'generic').toLowerCase().indexOf('win') >= 0
  }
}
