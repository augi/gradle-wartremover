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
        project.dependencies {
            compile 'org.scala-lang:scala-library:2.12.3'
        }
        project.wartremover {
            warningWarts.add('MyProduction')
            test {
                warningWarts.add('MyTest')
            }
        }
        project.evaluate()
        then:
        def compileTask = project.tasks.compileScala as ScalaCompile
        compileTask.scalaCompileOptions.additionalParameters.find { it.contains('MyProduction') }
        !compileTask.scalaCompileOptions.additionalParameters.find { it.contains('MyTest') }
        def compileTestTask = project.tasks.compileTestScala as ScalaCompile
        compileTestTask.scalaCompileOptions.additionalParameters.find { it.contains('MyProduction') }
        compileTestTask.scalaCompileOptions.additionalParameters.find { it.contains('MyTest') }
    }
}
