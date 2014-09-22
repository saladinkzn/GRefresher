GRefresher
===============

[![Build Status](https://travis-ci.org/saladinkzn/GRefresher.svg?branch=master)](https://travis-ci.org/saladinkzn/GRefresher)
[![License](http://img.shields.io/badge/license-MIT-47b31f.svg)](#copyright-and-license)

Gradle plugin inspired by sbt-revolver. It provides *re-run* task which improves development turnaround by recompiling
and restarting program in single click.

Configuration
-----------------
GRefresher provides grefresher extension to configure task

```
grefresher {
    mainClassName = /* Main class name of your app goes here */
    jvmArgs = /* list of jvm args to provide to JVM which will run your app */
    systemProperties = /* map of systemProperties. Each entry expands to following jvm arg: -D${key}=${value} */
}
```

Future plans
---------------------
* Recompile and restart on source changes.