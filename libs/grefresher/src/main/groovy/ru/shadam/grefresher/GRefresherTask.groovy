package ru.shadam.grefresher

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecHandle
import org.gradle.process.internal.JavaExecHandleBuilder
import org.gradle.tooling.GradleConnector

import javax.inject.Inject
/**
 * @author sala
 */
class GRefresherTask extends DefaultTask {
  volatile ExecHandle execHandle
  volatile Thread runningThread

  @Inject
  protected FileResolver getFileResolver() {
    throw new UnsupportedOperationException();
  }

  @TaskAction
  def hello() {
    GRefresherConfig config = project."${GRefresherPlugin.GREFRESHER_EXTENSION}".config
    //
    if(!config.mainClassName) {
      throw new IllegalStateException('mainClassName was not specified')
    }
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
              execHandle.abort()
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
    execHandle.abort()
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
      List<Project> projects = ProjectUtils.getDependencyProjects(p, 'compile')
      return p.sourceSets.main.allSource.srcDirs + projects.collect({ collectSourceSets(it)}).flatten()
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
      JavaExecHandleBuilder defaultJavaExecAction = new JavaExecHandleBuilder(getFileResolver())

      if (config.debug) {
        defaultJavaExecAction.jvmArgs("-Xrunjdwp:transport=dt_socket,server=y,suspend=${config.debugSuspend ? 'y' : 'n'},address=${config.debugPort}".toString())
      }

      defaultJavaExecAction.main = config.mainClassName
      defaultJavaExecAction.classpath = project.sourceSets.main.runtimeClasspath
      config.systemProperties.each {
        defaultJavaExecAction.systemProperties.put(it.key, it.value)
      }
      defaultJavaExecAction.jvmArgs(config.jvmArgs)

      execHandle = defaultJavaExecAction.build()
      execHandle.start().waitForFinish()
      logger.debug('Process stopped')
    }
  }
}
