GRefresher
===============

[![Build Status](https://travis-ci.org/saladinkzn/GRefresher.svg?branch=master)](https://travis-ci.org/saladinkzn/GRefresher)
[![License](http://img.shields.io/badge/license-MIT-47b31f.svg)](#copyright-and-license)

Gradle plugin inspired by sbt-revolver. It provides *re-run* task which improves development turnaround by recompiling
and restarting program in single click.

#### Key features:
* Recompile and restart by single keypress
* Recompile and restart on source changes

#### Requirements
----------------
Gradle 2.1+, Java 1.7+

#### Getting Started
-----------------
To get started you should add following snippet to your `build.gradle`

```groovy
buildscript {
    repositories {
        maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local' }
    }

    dependencies {
        classpath 'ru.shadam.grefresher:grefresher:0.1-SNAPSHOT'
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