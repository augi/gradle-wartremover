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
            mavenCentral()
        }
        project.dependencies {
            implementation 'org.scala-lang:scala-library:2.12.3'
        }
        project.wartremover {
            warningWarts.add('MyProduction')
            test {
                warningWarts.add('MyTest')
            }
        }
        project.evaluate()
        then:
        assert project.configurations.getByName("wartremover").dependencies.find { it.name == 'wartremover_2.12' }
        def compileTask = project.tasks.compileScala as ScalaCompile
        assert compileTask.scalaCompileOptions.additionalParameters.find { it.contains('MyProduction') }
        assert !compileTask.scalaCompileOptions.additionalParameters.find { it.contains('MyTest') }
        def compileTestTask = project.tasks.compileTestScala as ScalaCompile
        assert compileTestTask.scalaCompileOptions.additionalParameters.find { it.contains('MyProduction') }
        assert compileTestTask.scalaCompileOptions.additionalParameters.find { it.contains('MyTest') }
    }

    def "should infer compilation target correctly from different compile configurations"() {
        def project = ProjectBuilder.builder().build()
        when:
        project.plugins.apply 'scala'
        project.plugins.apply 'wartremover'
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            implementation 'org.scala-lang:scala-library:2.11.12'
        }
        project.evaluate()
        then:
        assert project.configurations.getByName("wartremover").dependencies.find { it.name == 'wartremover_2.11.12' }
    }
}
