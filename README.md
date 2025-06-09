# Grey Goo Mod (Modernized)

This repository contains a minimal port of the original Grey Goo mod to **Minecraft 1.20.1** using **Forge 47.4.1** (the last stable Forge release for 1.20.x). It registers a demonstration block and provides a working environment for further development.

## Requirements
- Java 17 JDK (set `JAVA_HOME` to your JDK 17 installation)
- Git
- [Gradle](https://gradle.org/) is included via the wrapper. Because binary files are not stored in the repository, run `gradle wrapper` once with a local Gradle install to download `gradle-wrapper.jar`.

## IntelliJ Setup
1. Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (Community Edition is fine).
2. Run `./gradlew genIntellijRuns --refresh-dependencies` to generate run configurations and download Forge/Minecraft dependencies.
3. Open the project using the provided `build.gradle` file. IntelliJ will import the Gradle project and index the sources.
4. After the sync completes you will find `Minecraft Client` and `Minecraft Server` under the **Gradle** run configurations.
5. Use the `runClient` task to launch the game with the mod in development mode.

## Building
Clone the repository and run the Gradle wrapper to produce the mod jar:
```bash
git clone <repo-url>
cd GreyGooMod
./gradlew build
```
The compiled jar will appear in `build/libs/`. You can place this file in your Minecraft `mods` folder.

## Notes
- The provided code only includes a basic block to verify that the mod loads on 1.20.1. More of the original mod features will need to be ported manually.
- Add your 16x16 textures under `src/main/resources/assets/greygoo/textures` when developing locally. Texture files are not stored in the repository.
