package ru.shadam.revolver

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sala
 */
class RevolverPlugin implements Plugin<Project> {
    void apply(Project project) {
        if(!project.plugins.hasPlugin('application')) {
            throw new IllegalStateException('Application plugin is not applied')
        }
        project.extensions.create("revolver", RevolverPluginExtension)
        project.task('re-run', type: RevolverTask) {
            dependsOn 'classes'
        }
    }
}

class RevolverPluginExtension {

}
