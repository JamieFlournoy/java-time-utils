# Developer Instructions

## Gradle

This project uses the Gradle build system. See [Gradle Intro](GRADLE_INTRO.md) for instructions for how to get Gradle working on a new development machine.

## Command line things

Basic build steps:

Use `./gradlew build` to build the code and run tests.

To just compile the code without running tests, use `./gradlew compileJava`.

When it's time to publish a new release, use `./gradlew publish`. This will create a .pom file, publish JARs of classes, sources, and javadocs, and sign all of these assets. They will appear in `build/repo` in a form that should be OK to upload as-is to a Maven package repository.

Use `./gradlew javadoc` to generate Javadoc documentation in [build/docs/javadoc](build/docs/javadoc/), then open [build/docs/javadoc/index.html](build/docs/javadoc/index.html) to browse it.

## Eclipse

The following Eclipse configuration files are included with this source tree.

* `.classpath`
* `.settings`
* `.project`

Install the "Eclipse Buildship for Gradle" plugin and then use File->Import->Gradle Project to create an Eclipse project using these files.

### Eclipse Plugins

You may find the following Eclipse plugins useful when working with this code:

* Eclipse Buildship for Gradle
* Cucumber Editor (from org.agileware)
* MoreUnit for Java

All of these can be installed from within Eclipse, using the Help->Install New Software menu item.

#### Eclipse Buildship for Gradle

Sometimes, you may change something in the build.gradle file (such as adding or updating a dependency) which results in a successful build from the command line, but which has lots of strange build errors in Eclipse, even after you refresh sources, clean rebuild, etc. In this case, you probably need to refresh the Gradle project from within Eclipse.

To do this, right-click on the top-level project folder for this project in the Package Explorer view, and in the resulting context menu, select `Gradle -> Refresh Gradle Project`. If this results in downloading some JARs from the internet, that's good because it means Eclipse's set of dependency JARs was out of date and is being brought up-to-date. Once the downloads are done, Eclipse will automatically rebuild the project (assuming you have `Project -> Build Automatically` checked) and the build errors in Eclipse should go away.
