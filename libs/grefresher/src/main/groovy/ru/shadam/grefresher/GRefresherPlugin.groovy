package ru.shadam.grefresher

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sala
 */
class GRefresherPlugin implements Plugin<Project> {
  public static final String GREFRESHER_EXTENSION = 'grefresher'

    void apply(Project project) {
      def version = ProjectUtils.getPluginVersion(
              project.rootProject,
              'ru.shadam.grefresher',
              'grefresher'
      )
      def motd = "You're running GRefresher v.${version}"
      project.extensions.create(GREFRESHER_EXTENSION, GRefresherPluginExtension)
      project.task('re-run', type: GRefresherTask, group: 'GRefresher') {
        description = 'Runs project in triggered restart mode'
        doFirst {
          println motd
        }
        dependsOn 'classes'
      }
    }
}

