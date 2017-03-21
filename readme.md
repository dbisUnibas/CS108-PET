# ReqMan

ReqMan (Requirements Manager) is an application to manage requirements.

**Requirement**s are grouped in **milestone**s and the collection of **requirement**s
with their respective **milestone**s is called **catalogue**. **Catalogue**s are 
created using `ReqMan`'s built in `editor`.

Using `ReqMan`'s built in `evaluator` one can register several **group**s and
assess their voyage on achieving the **requirement**s with so called **progress**.

*Catalogue**s and **group**s may be exported with templates provided.


## Table of Contents

 - Build
 - Usage
 - Issues
 - Contributors
 - License
 - Versioning
 - Branching

## Build

`ReqMan` uses the gradle.

### ReqMan

To build and pack `ReqMan` in an executable jar run:

	gradle jar

### Editor

To exclusively have the `editor` application executable in a jar, run:

	gradle deployEditor

### Evaluator

To exclusively have the `evaluator` application executable in a jar, run:

	gralde deployEvaluator
	

## Usage

`ReqMan` (and its modes `editor` and `evaluator`) is a JavaFX application
packed in an executable jar. Thus to run `ReqMan` one must execute the jar.

The command to run `ReqMan` from command line, starting the GUI application
use the following command (where X stands for the complete version string
(refer to versioning for further information about the version)):

	java -jar reqman-X.jar
	
## Issues

Please use Maniphest to report issues. Create a task and set the tag to 
the ReqMan project as well as setting the visibility to *Members of Project...* ReqMan.

## Contributors

Loris Sauter - loris.sauter@unibas.ch

## License

`ReqMan` is open source and licensed under the MIT license.
See LICENSE.txt for the complete license text.

## Versioning

`ReqMan` uses semantic versioning as defined by http://semver.org/

## Branching

There are several branches active:

 - `master` which is up-to-date with the documentation and core classes
 - `reqman` which is up-to-date with the latest recommended version of
  `ReqMan`
 - `editor-exp` which is the branch for developing features related
   to `editor`
 - `assessment-exp` which is the branch for developing features related
   to `evaluator`
 - `reqman-exp` which is the branch for developing features related to both: 
   `editor` and `evaluator`
 - `experimental` a branch dedicated to highly experimental development.
   The application as in the state of this branch may not be fully compatible
   with the ones of other states.