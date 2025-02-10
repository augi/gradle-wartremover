package cz.augi.gradle.wartremover

import org.gradle.api.Project
import org.gradle.api.tasks.scala.ScalaCompile
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class WartremoverPluginTest extends Specification {
    def "add compiler parameters to compiler"() {
        setup:
        Project project = setupBaseProject('2.10.7')

        when:
        project.wartremover {
            warningWarts.add('MyProduction')
            test {
                warningWarts.add('MyTest')
            }
        }
        project.evaluate()

        then:
        List<String> compileScalaParams = (project.tasks.compileScala as ScalaCompile).scalaCompileOptions.additionalParameters
        List<String> compileTestScalaParams = (project.tasks.compileTestScala as ScalaCompile).scalaCompileOptions.additionalParameters

        wartremoverDependencyName(project) == "wartremover_2.10"
        wartremoverDependencyVersion(project) == WartremoverPlugin.wartremoverVersionScala210
        compileScalaParams.find { it.contains('MyProduction') }
        !compileScalaParams.find { it.contains('MyTest') }
        compileTestScalaParams.find { it.contains('MyProduction') }
        compileTestScalaParams.find { it.contains('MyTest') }
    }

    def "should infer compilation target correctly from different compile configurations"() {
        setup:
        Project project = setupBaseProject('2.11.12')

        when:
        project.evaluate()

        then:
        wartremoverDependencyName(project) == "wartremover_2.11"
        wartremoverDependencyVersion(project) == WartremoverPlugin.wartremoverVersionScala211
    }

    def "should include the user-defined scala version in the wartremover artifact name"() {
        setup:
        Project project = setupBaseProject('2.13.15')

        when:
        project.wartremover {
            scalaVersion = '2.13.16'
        }
        project.evaluate()

        then:
        wartremoverDependencyName(project) == 'wartremover_2.13.16' // Uses full Scala version when defined by user
        wartremoverDependencyVersion(project) == WartremoverPlugin.wartremoverVersionScala212Plus
    }

    def "should set the wartremover artifact version to user-defined wartremover version"() {
        setup:
        Project project = setupBaseProject('2.13.15')
        def testVersion = '2.4.20'

        when:
        project.wartremover {
            wartremoverVersion = testVersion
        }
        project.evaluate()

        then:
        wartremoverDependencyName(project) == 'wartremover_2.13'
        wartremoverDependencyVersion(project) == testVersion
    }

    private def setupBaseProject(String scalaVersion) {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply 'scala'
        project.plugins.apply 'wartremover'
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            implementation "org.scala-lang:scala-library:$scalaVersion"
        }
        return project
    }

    private static String wartremoverDependencyName(Project project) {
        project.configurations.getByName('scalaCompilerPlugins').dependencies.find().name
    }

    private static String wartremoverDependencyVersion(Project project) {
        project.configurations.getByName('scalaCompilerPlugins').dependencies.find().version
    }
}
