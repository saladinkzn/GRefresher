package ru.shadam.revolver

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author sala
 */
class RevolverTask extends DefaultTask {
    @TaskAction
    def hello() {
        println 'Hello'
    }
}
