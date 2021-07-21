package cz.augi.gradle.wartremover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.scala.ScalaCompile

class WartremoverPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def extension = project.extensions.create('wartremover', WartremoverExtension)
        project.afterEvaluate {
            def configuration = project.configurations.create('wartremover')
            project.dependencies.add(configuration.name, "org.wartremover:wartremover_${getWartremoverArtifactSuffix(project)}")
            File pluginFile = configuration.resolve().find { it.toString().toLowerCase().contains("wartremover_") && it.toString().toLowerCase().endsWith('.jar') }
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

                scalaTask.scalaCompileOptions.additionalParameters.addAll(settings.classPaths.collect { getClasspathDirective(it) })
            }
        }
    }

    private String getWartremoverArtifactSuffix(Project p) {
        String scalaVersion = getScalaVersion(p)
        String scalaMajorVersion = scalaVersion.split('\\.').take(2).join('.') // 2.12.3 -> 2.12
        Integer scalaMinorVersion = scalaVersion.split('\\.').takeRight(1).join('').toInteger() // 2.12.3 -> 3
        if (scalaMajorVersion == '2.10') '2.10:2.3.7'
        else if (scalaMajorVersion == '2.11') {
            scalaMinorVersion >= 12 ? "$scalaVersion:2.4.16" : '2.11:2.4.16'
        } else if (scalaMajorVersion == '2.12') {
            scalaMinorVersion >= 10 ? "$scalaVersion:2.4.16" : (scalaMinorVersion >= 8 ? "$scalaVersion:2.4.13" : '2.12:2.4.16')
        } else if (scalaMajorVersion == '2.13') {
            "$scalaVersion:2.4.16"
        } else {
            throw new RuntimeException("Unsupported Scala version: $scalaVersion")
        }
    }

    private String getScalaVersion(Project p) {
        def scalaLibrary = p.configurations
                .findAll { ['compile', 'api', 'implementation'].contains(it.name) }
                .collectMany { it.dependencies }
                .find { it.group == 'org.scala-lang' && it.name == 'scala-library' }
        if (!scalaLibrary) {
            throw new RuntimeException('Scala library dependency not found')
        }
        checkValidScalaVersion(scalaLibrary.version)
        scalaLibrary.version
    }

    private void checkValidScalaVersion(String scalaVersion) {
        if (!scalaVersion.contains('.') || !scalaVersion.chars.any { it.digit } || scalaVersion.contains('?')) {
            throw new RuntimeException("Invalid Scala library version: $scalaVersion")
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

    private String getClasspathDirective(String classpath) {
        '-P:wartremover:cp:' + classpath
    }
}
