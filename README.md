GRefresher
===============

[![Build Status](https://travis-ci.org/saladinkzn/GRefresher.svg?branch=master)](https://travis-ci.org/saladinkzn/GRefresher)
[![Release](http://img.shields.io/badge/release-0.2-47b31f.svg)](https://github.com/saladinkzn/GRefresher/releases/latest)
[![Snapshot](http://img.shields.io/badge/current-0.3--SNAPSHOT-47b31f.svg)](https://github.com/saladinkzn/GRefresher/tree/master)
[![License](http://img.shields.io/badge/license-MIT-47b31f.svg)](#copyright-and-license)

Gradle plugin inspired by sbt-revolver. It provides *re-run* task which improves development turnaround by recompiling
and restarting program in single click.

#### Key features:
* Recompile and restart by single keypress
* Recompile and restart on source changes

#### Requirements
----------------
Gradle 1.12+ (tested on 1.12, but may be supported by earlier versions), Java 1.7+

#### Getting Started
-----------------
To get started you should add following snippet to your `build.gradle`

##### Release:

```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'ru.shadam.grefresher:grefresher:0.2'
    }
}
```

##### Snapshot:

```groovy
buildscript {
    repositories {
        // .. your preferred repos: mavenCentral() or jcenter() or some else (at least one is required for plugin's dependencies)
        maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local' }
    }

    dependencies {
        classpath 'ru.shadam.grefresher:grefresher:0.3-SNAPSHOT'
    }
}

apply plugin: 'ru.shadam.grefresher'
```

#### Configuration
-----------------
GRefresher provides grefresher extension to configure task

```groovy
grefresher {
    debug = (true | false) /* Enables debug */
    debugSuspend = (true | false) /* Specifies if application waits for debugger on start */
    debugPort = Integer /* Specifies port for listening to debugger */

    mainClassName = String /* Main class name of your app goes here */
    jvmArgs = [] /* list of jvm args to provide to JVM which will run your app */
    systemProperties = [:] /* map of systemProperties. Each entry expands to following jvm arg: -D${key}=${value} */

    scanInterval = Integer /* Source scanning period. 0 or null to disable scanning */
}
```

#### Copyright and License
---------------------
This project is licensed under [MIT license](LICENSE)