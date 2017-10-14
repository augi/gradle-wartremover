package cz.augi.gradle.wartremover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.scala.ScalaCompile

class WartremoverPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def extension = project.extensions.create('wartremover', WartremoverExtension)
        project.afterEvaluate {
            def configuration = project.configurations.getByName('compileOnly')
            project.dependencies.add(configuration.name, 'org.wartremover:wartremover_2.12:2.2.1')
            File pluginFile = configuration.resolve().find { it.toString().toLowerCase().contains('wartremover_2.12') && it.toString().toLowerCase().endsWith('.jar') }
            if (pluginFile == null) {
                throw new RuntimeException('Wartremover JAR cannot be found')
            }
            project.tasks.withType(ScalaCompile).all { scalaTask ->
                if (scalaTask.scalaCompileOptions.additionalParameters == null) {
                    scalaTask.scalaCompileOptions.additionalParameters = new ArrayList<String>()
                }
                scalaTask.scalaCompileOptions.additionalParameters.add("-Xplugin:${pluginFile.canonicalPath}".toString())
                scalaTask.scalaCompileOptions.additionalParameters.addAll(extension.errorWarts.collect { getErrorWartDirective(it) })
                scalaTask.scalaCompileOptions.additionalParameters.addAll(extension.warningWarts.collect { getWarningWartDirective(it) })
                scalaTask.scalaCompileOptions.additionalParameters.addAll(extension.excludedFiles.collect { getExludedFileDirective(project.file(it).canonicalPath) })
            }
        }
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
