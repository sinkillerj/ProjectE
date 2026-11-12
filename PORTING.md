# Minecraft 26.3 port

This branch targets Minecraft 26.3, NeoForge 26.3.0.102, and a Java 25 JDK.
Build with `./gradlew build`; the runtime JAR is written to `build/libs`.
Regenerate server data and client assets together with `./gradlew runData`.

The API migration adapts MIT-licensed work from [Yaskulsky/Equivox](https://github.com/Yaskulsky/Equivox):
- Initial ProjectE 26.1.2 migration: `f53cd453fe49`.
- Minecraft 26.3 / native transfer API migration: `17772102da43`.

ProjectE retains its namespace, registry identifiers, and content. Equivox-only
items and integrations are not included. The original MIT license is retained.
Further fixes here preserve inventory filters, legacy saved inventories, and the
extensible tablet API. The port also restores chest and projectile rendering,
armor equipment assets, active item models, brewing and component-aware recipe
mapping, fuel values, and 26.3 data generation.

JEI, Curios, and Jade target their published 26.3 releases. CraftTweaker, EMI, WTHIT,
and The One Probe integration sources remain in the repository but are
excluded from this build pending compatible integration updates.

Validation on this branch:
- `./gradlew runData` generated both server data and client assets, including all
  108 registered item model definitions.
- `./gradlew clean build` passed all 92 tests (no failures or skips), including
  transaction rollback, inventory filtering, output-slot removal, and legacy
  inventory loading.
- The runtime JAR includes the shaded math dependency and MIT license.

The NeoForge test framework can log a loader shutdown race after its ephemeral
server stops; the test runner nevertheless reports all tests passing. In-game
visuals have not been manually playtested.
