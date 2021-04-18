# Gradle WartRemover Plugin [![Build](https://github.com/augi/gradle-wartremover/actions/workflows/build.yml/badge.svg)](https://github.com/augi/gradle-wartremover/actions/workflows/build.yml) [![Version](https://badgen.net/maven/v/maven-central/cz.augi/gradle-wartremover)](https://repo1.maven.org/maven2/cz/augi/gradle-wartremover/)

Gradle plugin to apply [WartRemover](http://www.wartremover.org), the Scala linting tool.

It applies the WartRemover plugin with same settings to all `ScalaCompile` tasks, so even the test code is checked.
> "Keep your tests clean. Treat them as first-class citizens of the system."
 [Robert C. Martin (Uncle Bob)](http://blog.cleancoder.com/uncle-bob/2017/05/05/TestDefinitions.html)

If you want to have different settings for tests then you can use the `test` block as shown below.
 If you don't use `test` block then all the settings is applied to all the Scala code.

Usage
=====
The plugin is published to [Gradle Plugins portal](https://plugins.gradle.org/plugin/cz.augi.gradle.wartremover)

    plugins {
      id 'cz.augi.gradle.wartremover' version 'putCurrentVersionHere'
    }
	
	wartremover {
	    errorWarts.add('Product') // set of warts to use - violation causes error; default is empty set
	    warningWarts.add('Product') // set of warts to use - violation causes warning; default is set of all stable warts
	    excludedFiles.add('src/main/scala/me/project/Main.scala') // set of file to be excluded; default is empty
        classPaths.add(new File("path/to/yourWarts").toURI().toURL().toString()) // set of files or directories to be added to the classpath if using custom warts; default is empty
	    test {
	        errorWarts.add('Serializable') // set of warts to use - violation causes error; default settings from the block above
            warningWarts.add('Serializable') // set of warts to use - violation causes warning; default settings from the block above
            excludedFiles.add('src/test/scala/me/project/Test.scala') // set of file to be excluded; default settings from the block above
            classPaths.add(new File("path/to/yourTestWarts").toURI().toURL().toString()) // set of files or directories to be added to the classpath if using custom warts; default settings from the block above
	    }
	}
