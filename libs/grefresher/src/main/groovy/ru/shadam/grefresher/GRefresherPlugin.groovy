package ru.shadam.grefresher

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sala
 */
class GRefresherPlugin implements Plugin<Project> {
  public static final String GREFRESHER_EXTENSION = 'grefresher'

    void apply(Project project) {
        project.extensions.create(GREFRESHER_EXTENSION, GRefresherPluginExtension)
        project.task('re-run', type: GRefresherTask) {
            dependsOn 'classes'
        }
    }
}

