# Gradle Wartremover Plugin

[![Build Status](https://travis-ci.org/augi/gradle-wartremover.svg)](https://travis-ci.org/augi/gradle-wartremover) [ ![Download](https://api.bintray.com/packages/augi/maven/gradle-wartremover/images/download.svg) ](https://bintray.com/augi/maven/gradle-wartremover/_latestVersion)

Gradle plugin to apply [wartremover](http://www.wartremover.org), the Scala linting tool.


Usage
====================

	buildscript {
		repositories {
			jcenter()
		}
		dependencies {
			classpath 'cz.augi:gradle-wartremover:$putCurrentVersionHere'
		}
	}

	apply plugin: 'wartremover'
	
	wartremover {
	    errorWarts.add('Product') // set of warts to use - violation causes error; default is empty set
	    warningWarts.add('Product') // set of warts to use - violation causes warning; default is set of all stable warts
	    excludedFiles.add('src/scala/me/project/Main.scala') // set of file to be excluded; default is empty
	}

It applies the wartremover plugin with this settings to all `ScalaCompile` tasks, so even the test code is checked.
> "Keep your tests clean. Treat them as first-class citizens of the system."
 [Robert C. Martin (Uncle Bob)](http://blog.cleancoder.com/uncle-bob/2017/05/05/TestDefinitions.html)
 
