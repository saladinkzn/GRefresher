import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import ru.shadam.grefresher.GRefresherTask
/**
 * @author sala
 */
class GreetingPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'ru.shadam.grefresher'

        Assert.assertTrue(project.tasks.'re-run' instanceof GRefresherTask)
    }
}
