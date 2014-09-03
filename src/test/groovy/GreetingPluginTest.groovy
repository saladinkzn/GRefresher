import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import ru.shadam.revolver.RevolverTask

/**
 * @author sala
 */
class GreetingPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'ru.shadam.revolver'

        Assert.assertTrue(project.tasks.hello instanceof RevolverTask)
    }
}
