# Pervasive Code's Java Time Utilities

This library includes a few simple classes that simplify the creation of easily-testable code that needs to care about doing things in relation to wall-clock time.

In particular, if you are using dependency injection, you will want an interface to isolate your wall-clock-time-sensitive code from any sources of real time, so that your setup code can provide real instances that get real time values, while your test code provides fake, controllable time providers that let your tests run with no dependency on actual wall-clock time.

This library provides interfaces and implementations that are useful for this sort of approach.

(This is _not_ a library for working with quantities of time, calendars, or for measuring the amount of time something takes.)

## Overview of included classes

See the separate [OVERVIEW.md](OVERVIEW.md) file for a description of what interfaces and classes are included.

## Including it in your project

Use groupId `com.pervasivecode`, name `java-time-utils`, version `0.9` in your build tool of choice.

### Gradle Example

If you are using Gradle 4.x, put this in your build.properties file:

```
// in your build.gradle's repositories {} block:
    mavenCentral();

// in your build.gradle's dependencies {} block:
    implementation 'com.pervasivecode:java-time-utils:0.9'

    // or, if you prefer the separated group/name/version syntax:
    implementation group: 'com.pervasivecode', name: 'java-time-utils', version: '0.9'
```


## Contributing

See [DEVELOPERS.md](DEVELOPERS.md) and [GRADLE_INTRO.md](GRADLE_INTRO.md) if you want to build and hack on the code yourself. 


## Copyright and License

Copyright © 2018 Jamie Flournoy.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

