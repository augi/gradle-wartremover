# Gradle WartRemover Plugin [![Build](https://github.com/augi/gradle-wartremover/actions/workflows/build.yml/badge.svg)](https://github.com/augi/gradle-wartremover/actions/workflows/build.yml) [![Version](https://badgen.net/maven/v/maven-central/cz.augi/gradle-wartremover)](https://repo1.maven.org/maven2/cz/augi/gradle-wartremover/)

Gradle plugin to apply [WartRemover](http://www.wartremover.org), the Scala linting tool.

It applies the WartRemover plugin with same settings to all `ScalaCompile` tasks, so even the test code is checked.
> "Keep your tests clean. Treat them as first-class citizens of the system."
 [Robert C. Martin (Uncle Bob)](http://blog.cleancoder.com/uncle-bob/2017/05/05/TestDefinitions.html)

If you want to have different settings for tests then you can use the `test` block as shown below.
 If you don't use `test` block then all the settings is applied to all the Scala code.

> Please note that since `0.15.0` version at least Gradle 6.4 is required (because the plugin uses `scalaCompilerPlugins` configuration).

## Usage

The plugin is published to [Gradle Plugins portal](https://plugins.gradle.org/plugin/cz.augi.gradle.wartremover)

    plugins {
      id 'cz.augi.gradle.wartremover' version '<putCurrentVersionHere>'
    }

## Configuration

* `errorWarts.add(<String>)` or `errorWarts.addAll(Collection<String>)` (default is an empty `Set<String>`): 
  * Set of warts to use which throw <ins>an error</ins> when violated.
* `warningWarts.add(<String>)` or `warningWarts.addAll(Collection<String>)` (default is a `Set<String>` with all `wartremover:2.x` [Unsafe](https://github.com/wartremover/wartremover/blob/v2.4.21/core/src/main/scala-2/org/wartremover/warts/Unsafe.scala) warts):
  * Set of warts to use which throw <ins>a warning</ins> when violated.
* `excludedFiles.add(<String>)` or `excludedFiles.addAll(Collection<String>)` (default is an empty `Set<String>`):
  * Set of files to be excluded from wartremover coverage.
* `classPaths.add(<String>)` or `classPaths.addAll(Collection<String>)` (default is an empty `Set<String>`):
  * Set of files or directories to be added to the classpath if using custom warts.
* `scalaVersion = <String>` (default `detected`):
  * Scala version used to denote the wartremover artifact, e.g. `scalaVersion = 2.13.16` -> `wartremover_2.13.16`; `scalaVersion = 2.13` -> `wartremover_2.13`.
  * Refer to the [Maven Repository](https://mvnrepository.com/artifact/org.wartremover/wartremover) for corresponding artifact versions.
* `wartremoverVersion = <String>` (default `'2.4.21'` for Scala 2.11+, `2.3.7` for Scala 2.10):
  * Version of the `org.wartremover` dependency. This plugin is compatible with `2.x` by default, while `3.x` may require manually adding the [different Unsafe](https://github.com/wartremover/wartremover/blob/e8f4fb77aa20ea37a289dbaabf88b38be88e8e26/core/src/main/scala/org/wartremover/warts/Unsafe.scala) warts.

```
watremover {
    errorWarts.add('Product')
    warningWarts.add('Product')
    excludedFiles.add('src/main/scala/me/project/Main.scala')
    classPaths.add(new File("path/to/yourWarts").toURI().toURL().toString())
    scalaVersion = '2.13.16'
    wartremoverVersion = '2.4.21'
    test {
        errorWarts.add('Serializable') // default settings from the block above
        warningWarts.add('Serializable') // default settings from the block above
        excludedFiles.add('src/test/scala/me/project/Test.scala') // default settings from the block above
        classPaths.add(new File("path/to/yourTestWarts").toURI().toURL().toString()) // default settings from the block above
    }
}
```