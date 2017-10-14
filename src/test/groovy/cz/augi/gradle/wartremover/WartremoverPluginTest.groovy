package cz.augi.gradle.wartremover

import org.gradle.api.tasks.scala.ScalaCompile
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class WartremoverPluginTest extends Specification {
    def "add compiler parameters to compiler"() {
        def project = ProjectBuilder.builder().build()
        when:
        project.plugins.apply 'scala'
        project.plugins.apply 'wartremover'
        project.repositories {
            jcenter()
        }
        project.evaluate()
        then:
        def compileTask = project.tasks.compileScala as ScalaCompile
        !compileTask.scalaCompileOptions.additionalParameters.empty
    }
}
