# AGENTS.md – Minecraft Forge Modding Agent Setup (v1.20.1)

> This document provides setup and behavioral instructions for AI coding agents (e.g., OpenAI Codex, GitHub Copilot, ChatGPT) assisting in the development of a Minecraft Forge mod targeting version **1.20.1**.

---

## 🎯 Project Target

- **Minecraft Version**: `1.20.1`
- **Mod Loader**: `Forge`
- **Java Version**: `17`
- **Gradle Version**: Managed via wrapper (do not assume system Gradle)
- **Language**: `Java` (no Kotlin)

---

## 🛠 Project Structure Overview

The project follows the standard Forge directory structure:

```
.
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/mymod/
│   │   │       └── MyMod.java
│   │   └── resources/
│   │       ├── META-INF/mods.toml
│   │       └── pack.mcmeta
│   └── test/
```

---

## 🧠 Agent Behavior Guidelines

### ✅ You **should**:
- Use Forge 1.20.1 conventions (e.g., `@Mod`, `DeferredRegister`, `Mod.EventBusSubscriber`)
- Write clean, idiomatic Java
- Reference and respect the contents of `mods.toml`
- Suggest proper Gradle tasks (e.g., `./gradlew build`, `./gradlew genIntellijRuns`)
- Generate only code that compiles under Java 17
- Use registry patterns for items, blocks, entities, etc.

### ❌ You **should not**:
- Assume Minecraft code is present unless a stub is provided
- Use Minecraft `Mappings` unless explicitly specified (default to Mojang mappings)
- Generate Kotlin or Scala code
- Assume Fabric or Quilt APIs
- Add runtime mixins unless requested

---

## 🔧 Build Instructions

To build the mod:

```bash
./gradlew build
```

To generate IntelliJ run configs:

```bash
./gradlew genIntellijRuns
```

To run the client:

```bash
./gradlew runClient
```

---

## 📦 Key Files

- **`mods.toml`** – Defines mod metadata and entrypoint
- **`MyMod.java`** – Entry point class with `@Mod(MODID)`
- **`RegistryHandler.java`** – Use `DeferredRegister` for block/item/creative tab registration
- **`pack.mcmeta`** – Required metadata for the resource pack system

---

## 🧩 Example Entrypoint Class

```java
package com.example.mymod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MyMod.MODID)
public class MyMod {
    public static final String MODID = "mymod";

    public MyMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
    }
}
```

---

## 💡 Registry Pattern Example

```java
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MyMod.MODID);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item",
        () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
```

---

## 📚 Suggested Prompt Formats

- "Create a new block called `Copper Furnace` that behaves like a normal furnace."
- "Add a creative tab for all mod items and blocks."
- "Refactor `ModItems` to register all items with tooltips."
- "Add an event handler that logs when a player jumps."

---

## ⚠️ Known Constraints

- ForgeGradle's `mappings channel` is Mojang by default for 1.20.1
- Obfuscated names should not be used in source code unless decompiling
- Resource packs must follow vanilla 1.20.1 formats
- The project assumes single-mod structure (not a multi-mod build)


# Repository Agent Instructions

Do not include any binaries in the commits

For tests-

Build the mod
Run the client


