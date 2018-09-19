package cz.augi.gradle.wartremover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.scala.ScalaCompile

class WartremoverPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def extension = project.extensions.create('wartremover', WartremoverExtension)
        project.afterEvaluate {
            def scalaVersion = getScalaVersion(project)
            def configuration = project.configurations.create('wartremover')
            project.dependencies.add(configuration.name, "org.wartremover:wartremover_${scalaVersion}:2.3.6")
            File pluginFile = configuration.resolve().find { it.toString().toLowerCase().contains("wartremover_${scalaVersion}") && it.toString().toLowerCase().endsWith('.jar') }
            if (pluginFile == null) {
                throw new RuntimeException('Wartremover JAR cannot be found')
            }
            project.tasks.withType(ScalaCompile).all { scalaTask ->
                if (scalaTask.scalaCompileOptions.additionalParameters == null) {
                    scalaTask.scalaCompileOptions.additionalParameters = new ArrayList<String>()
                }
                scalaTask.scalaCompileOptions.additionalParameters.add("-Xplugin:${pluginFile.canonicalPath}".toString())
                WartremoverSettings settings = scalaTask.name.toLowerCase().contains('test') ? extension.getTest() : extension
                scalaTask.scalaCompileOptions.additionalParameters.addAll(settings.errorWarts.collect { getErrorWartDirective(it) })
                scalaTask.scalaCompileOptions.additionalParameters.addAll(settings.warningWarts.collect { getWarningWartDirective(it) })
                scalaTask.scalaCompileOptions.additionalParameters.addAll(settings.excludedFiles.collect { getExludedFileDirective(project.file(it).canonicalPath) })
            }
        }
    }

    private String getScalaVersion(Project p) {
        def scalaLibrary = p.configurations.getByName('compile').dependencies.find { it.group == 'org.scala-lang' && it.name == 'scala-library' }
        if (!scalaLibrary) {
            p.logger.warn('Scala library dependency not found in \'compile\' configuration, defaulting to 2.12')
            return '2.12'
        }
        if (!isValidScalaVersion(scalaLibrary.version)) {
            p.logger.warn("Invalid Scala library version ('${scalaLibrary.version}'), defaulting to 2.12")
            return '2.12'
        }
        scalaLibrary.version.split('\\.').take(2).join('.') // 2.12.3 -> 2.12
    }

    private boolean isValidScalaVersion(String scalaVersion) {
        scalaVersion.contains('.') && scalaVersion.chars.any { it.digit } && !scalaVersion.contains('?')
    }

    private String getErrorWartDirective(String name) {
        if (!name.contains('.')) {
            name = 'org.wartremover.warts.' + name
        }
        '-P:wartremover:traverser:' + name
    }

    private String getWarningWartDirective(String name) {
        if (!name.contains('.')) {
            name = 'org.wartremover.warts.' + name
        }
        '-P:wartremover:only-warn-traverser:' + name
    }

    private String getExludedFileDirective(String absoluteFileName) {
        '-P:wartremover:excluded:' + absoluteFileName
    }
}
