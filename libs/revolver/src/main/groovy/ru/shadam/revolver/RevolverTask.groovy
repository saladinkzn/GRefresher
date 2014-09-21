package ru.shadam.revolver
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.GradleConnector

/**
 * @author sala
 */
class RevolverTask extends DefaultTask {
  @TaskAction
  def hello() {
    String mainClassName = project.getProperties()['mainClassName']
    logger.info "mainClassName: ${mainClassName}"
    //
    def proc = startProcess(mainClassName)
    while (true) {
      System.out.println 'Press q to stop the application or any other key to restart'
      def input = System.in.read()
      proc.destroy()
      if(input == 'q' || input == 'Q') {
        System.out.println 'Stopping the application'
        break
      }
      //
      // calling rebuild
      def connection = GradleConnector.newConnector().useInstallation(project.gradle.gradleHomeDir).forProjectDirectory(project.projectDir).connect()
      try {
        connection.newBuild().forTasks('classes').run()
      } finally {
        connection.close()
      }
      proc = startProcess(mainClassName);
    }
  }

  private Process startProcess(String mainClassName) {
    String javaExe = isWindows() ? 'java.exe' : 'java'
    String javaPath = new File(System.getProperty("java.home"), "bin/$javaExe").absolutePath
    def classPath = getRunnerClassPath()
    classPath = classPath.collect { it.absolutePath }.join(System.getProperty('path.separator'))
    def procParams = [javaPath] + ['-cp', classPath, mainClassName]
//    Process proc = procParams.execute()
    def proc = ProcessBuilder.newInstance()
            .command(procParams as List<String>)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
    proc
  }

  protected Collection<File> getRunnerClassPath() {
    (project.sourceSets.main.output + project.configurations['compile']).files
  }

  static boolean isWindows() {
    System.getProperty('os.name', 'generic').toLowerCase().indexOf('win') >= 0
  }
}
