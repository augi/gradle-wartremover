package cz.augi.gradle.wartremover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.scala.ScalaCompile

class WartremoverPlugin implements Plugin<Project> {

    static final String wartremoverVersionScala210 = '2.3.7'
    static final String wartremoverVersionScala211 = '2.4.21'
    static final String wartremoverVersionScala212Plus = '3.4.0'

    @Override
    void apply(Project project) {
        WartremoverExtension extension = project.extensions.create('wartremover', WartremoverExtension, project)
        project.afterEvaluate {
            project.dependencies { scalaCompilerPlugins "org.wartremover:wartremover_${getWartremoverArtifactSuffix(project)}" }
            project.tasks.withType(ScalaCompile).configureEach { scalaTask ->
                List<String> updatedParameters = new ArrayList<>()
                if (scalaTask.scalaCompileOptions.additionalParameters != null) {
                    updatedParameters.addAll(scalaTask.scalaCompileOptions.additionalParameters)
                }
                WartremoverSettings settings = scalaTask.name.toLowerCase().contains('test') ? extension.getTest() : extension
                updatedParameters.addAll(settings.errorWarts.collect { getErrorWartDirective(it) })
                updatedParameters.addAll(settings.warningWarts.collect { getWarningWartDirective(it) })
                updatedParameters.addAll(settings.excludedFiles.collect { getExcludedFileDirective(project.file(it).canonicalPath) })
                updatedParameters.addAll(settings.classPaths.collect { getClasspathDirective(it) })

                scalaTask.scalaCompileOptions.additionalParameters = updatedParameters
            }
        }
    }

    private static String getWartremoverArtifactSuffix(Project p) {
        Property<String> wartremoverVersionProperty = p.extensions.wartremover.wartremoverVersion
        String scalaVersion = getScalaVersion(p)

        if (wartremoverVersionProperty.isPresent()) "$scalaVersion:${wartremoverVersionProperty.get()}"
        else if (scalaVersion.startsWith('2.10')) "$scalaVersion:$wartremoverVersionScala210"
        else if (scalaVersion.startsWith('2.11')) "$scalaVersion:$wartremoverVersionScala211"
        else if (scalaVersion.startsWithAny('2.12', '2.13')) "$scalaVersion:$wartremoverVersionScala212Plus"
        else throw new RuntimeException("Unsupported Scala version: $scalaVersion")
    }

    private static String getScalaVersion(Project p) {
        Property<String> scalaVersionProperty = p.extensions.wartremover.scalaVersion
        if (scalaVersionProperty.isPresent()) {
            return checkValidScalaVersion(scalaVersionProperty.get()) // Uses full Scala version when defined by user
        } else {
            return checkValidScalaVersion(getScalaVersionFromProject(p))
        }
    }

    private static String getScalaVersionFromProject(Project p) {
        def scalaLibrary = p.configurations
                .findAll { ['compile', 'api', 'implementation'].contains(it.name) }
                .collectMany { it.dependencies }
                .find { it.group == 'org.scala-lang' && it.name == 'scala-library' }
        if (!scalaLibrary) {
            throw new RuntimeException('Scala library dependency not found')
        }
        return scalaLibrary.version.split('\\.').take(2).join('.') // 2.13.16 -> 2.13
    }

    private static String checkValidScalaVersion(String scalaVersion) {
        if (!scalaVersion.contains('.') || !scalaVersion.chars.any { it.digit } || scalaVersion.contains('?')) {
            throw new RuntimeException("Invalid Scala library version: $scalaVersion")
        } else {
            return scalaVersion
        }
    }

    private static String getErrorWartDirective(String name) {
        if (!name.contains('.')) {
            name = 'org.wartremover.warts.' + name
        }
        '-P:wartremover:traverser:' + name
    }

    private static String getWarningWartDirective(String name) {
        if (!name.contains('.')) {
            name = 'org.wartremover.warts.' + name
        }
        '-P:wartremover:only-warn-traverser:' + name
    }

    private static String getExcludedFileDirective(String absoluteFileName) {
        '-P:wartremover:excluded:' + absoluteFileName
    }

    private static String getClasspathDirective(String classpath) {
        '-P:wartremover:cp:' + classpath
    }
}
