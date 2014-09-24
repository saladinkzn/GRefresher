package ru.shadam.grefresher

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

/**
 * @author sala
 */
abstract class ProjectUtils {
  /**
   * Looks for buildscript class path dependency to find version of specified plugin
   *
   * @param project project handle
   * @param group group of project to find version
   * @param name name of project to find version
   * @return version string or null if plugin was not found
   */
  public static String getPluginVersion(Project project, String group, String name) {
    assert group != null, 'Group cannot be null'
    assert name != null, 'Name cannot be null'
    def dependency = project.buildscript.configurations.classpath.dependencies.find {
      it.group == group && it.name == name
    }
    return dependency?.version
  }

  /**
   * Collects a list of ProjectDependencies
   *
   * @param project
   * @param configuration
   * @return
   */
  public static List<Project> getDependencyProjects(Project project, String configuration) {
    project.configurations[configuration].dependencies.withType(ProjectDependency).collect{ it.dependencyProject }
  }
}
