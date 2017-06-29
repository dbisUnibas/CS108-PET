# ReqMan

ReqMan (Requirements Manager) is a JavaFX application to manage requirements.

**Requirement**s are grouped in **milestone**s and the collection of **requirement**s
with their respective **milestone**s is called **catalogue**. **Catalogue**s are 
created using `ReqMan`'s built in `editor`.

Using `ReqMan`'s built in `evaluator` one can register several **group**s and
assess their voyage on achieving the **requirement**s with so called **progress**.

**Catalogue**s and **group**s may be exported with templates provided.

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
	
## Dependencies

Beside using JavaFX 8 (from java 1.8.0_121) all dependencies are handled by the gradle build script.

## Issues

To report bugs or add feature requests, use the [GitHub issues page](https://github.com/dbisUnibas/ReqMan/issues)

## Contributors

Loris Sauter - loris.sauter@unibas.ch

## License

`ReqMan` is open source and licensed under the MIT license.
See LICENSE.txt for the complete license text.

## Versioning

`ReqMan` uses semantic versioning as defined by http://semver.org/
