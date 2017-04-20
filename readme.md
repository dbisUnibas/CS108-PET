# ReqMan - branch: experimental

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
 - Repository structure

## Build

`ReqMan` uses gradle. The source of `ReqMan` is located under `dev/src/`.
To start building `ReqMan`, navigate to the `dev/` folder.

### ReqMan

To build `ReqMan` run:

```
gradlew build
```

## Usage

`ReqMan` (and its modes `editor` and `evaluator`) is a JavaFX application
packed in an executable jar. Thus to run `ReqMan` one must execute the jar.

After building the jar as described above, it is assumed the user is still
in the `dev/` folder.

The command to run `ReqMan` from command line, starting the GUI application
use the following command (where X stands for the complete version string
(refer to versioning for further information about the version)):

```
java -jar build/libs/reqman-X.jar
```
	
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
 - `reqman` which is up-to-date with the latest **recommended** version of
  `ReqMan`
 - `editor-exp` which is the branch for developing features related
   to `editor`
 - `assessment-exp` which is the branch for developing features related
   to `evaluator`
 - `reqman-exp` which is the branch for developing features related to both: 
   `editor` and `evaluator` and points to the **latest** version of `ReqMan`
 - `experimental` a branch dedicated to highly experimental development.
   The application as in the state of this branch may not be fully compatible
   with the ones of other states.
   
## Repository structure

The repository contains the following files & folders:

 - .gitignore   The gitignore for this repository.
 - CS108.json   A catalogue used by FS17, cs108
 - dev/         The development folder
 - LICENSE.txt  The full license file
 - process/     A folder containing additional documentation and process related files
 - readme.md    This readme