package ru.shadam.revolver

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sala
 */
class RevolverPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("revolver", RevolverPluginExtension)
        project.task('hello', type: RevolverTask)
    }
}

class RevolverPluginExtension {

}
