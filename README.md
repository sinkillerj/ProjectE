![](/src/main/resources/logo.png?raw=true)

---

## ⚠️ Crash Fix Fork — NeoForge 1.21.1

This is a community fork of ProjectE that fixes a server crash introduced in PE1.1.0 on NeoForge 1.21.1.

**The bug:** Crafting any DM/RM tool or armor (Dark Matter Pickaxe, Red Matter Sword, etc.) causes the server to crash immediately with `IllegalStateException: Value must be positive: 0`. Every player on the server is kicked and the server cannot restart until the item is removed from the player's inventory.

**The fix:** DM/RM tools and armor are now registered with a real `max_damage` data component (required by NeoForge 1.21.1's serialization system). Durability is still never consumed — the items work exactly as intended.

### How to install

1. Go to the [**Releases**](../../releases) tab and download the latest `projecte-1.1.0.jar`
2. Replace the existing `ProjectE-1.21.1-PE1.1.0.jar` in your `mods/` folder with the downloaded jar
3. **Both the server and all clients must use this jar** (NeoForge requires matching mod versions)

> Once an official fix is released by the ProjectE team, switch back to the official jar from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/projecte/files).

---

Repository for ProjectE, a complete rewrite of EE2 (Equivalent Exchange 2) for modern Minecraft versions. Transmutation tables, collectors, condensers, flying rings, and all the other trinkets you love are here.

Discover powerful alchemical tools, items, and devices. Break down unwanted items into EMC (Energy-Matter Covalence) and use that EMC to create new items.


# I found a bug
Bugs can be reported at: https://github.com/sinkillerj/ProjectE/issues

Please try the latest release build before reporting, be sure to also include any logs or steps to reproduce you may have, as well as your Forge version, and whether or not you are using a third party loader such as Cauldron. When submitting logs please use a service such as Pastebin, do not paste the log directly into the issue.

# Downloads
https://www.curseforge.com/minecraft/mc-mods/projecte/files

# Support Development
![](/patreon.png?raw=true)

We accept donations via Patreon and other methods, visit the team members section to learn more.

# Join the Conversation

Discord: https://discord.gg/fkpxV5Z

# Current Team Members
Members actively working on ProjectE.

SinKillerJ - Head of Alchemical Studies - Main Project & Community Lead:

* Bluesky: https://bsky.app/profile/sinkillerj.bsky.social
* Patreon: https://www.patreon.com/sinkillerj

pupnewfster - Alchemical Adaptation - Lead of New Version Ports

* Bluesky: https://bsky.app/profile/sara.freimer.dev
* GitHub Sponsors: https://github.com/sponsors/pupnewfster
* Ko-fi: https://ko-fi.com/pupnewfster
* Patreon: https://www.patreon.com/pupnewfster

MaPePeR(Blubberbub) - Alchemical Value Archivist - Lead EMC Mapper Developer: 

* Twitter: https://twitter.com/Blubb3rbub

# Former Team Members
MozeIntel - Original lead developer: https://twitter.com/Moze_Intel

Kolatra - Former collaborator: https://twitter.com/ItsKolatra

Williewillus - Maintainer, 1.8.x-1.13.x: https://twitter.com/williewillus

Lilylicious - Assistant Developer: https://twitter.com/Lilyliciously

# Thanks To
x3n0ph0b3 - EE2 creator, Allowed use of EE2 assets: https://twitter.com/x3n0ph0b3x

MidnightLightning - EE2 GUI Textures: https://github.com/MidnightLightning
