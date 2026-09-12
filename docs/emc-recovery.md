# Experimental modpack EMC recovery

This option was extracted from a local Minecraft 1.21.1 modpack patch. It builds on
raziel23x's EMC correctness and failure-isolation work in upstream PR #2475
(commit `f4d387c15eefa5ff1e0907408bb93af0193a1347`). That prerequisite history is
preserved separately from the recovery changes.

## Enable

Set `recoverMissingItemEmc = true` in `config/ProjectE/mapping.toml` and set
`usePregenerated = false` before recalculating EMC. The option defaults to false.
Replacing a JAR requires a full game/server restart.

The option changes the mapping policy, not individual item assignments. There
are no hard-coded EMC entries for nuggets, buckets, honey treats, or other
recipe outputs. The custom EMC file remains the modpack author's responsibility.

## Behavior

- Track the ingredients used to establish a value and skip reductions that
  would derive that output from itself.
- Prefer publishable recipe values for real items when another route produces a
  positive value below one EMC. Preserve fractional fluids and synthetic groups.
- Defer the one-EMC minimum until ordinary recipes and validation settle, then
  propagate those item values through downstream recipes.
- Fill missing real items from currently valued recipes after cleanup. Respect
  forced conversions, explicit fixed values, and the free-value sentinel.
- Keep the default mapper's existing exploit cleanup when the option is off.

## Balance and remaining validation

This is an opt-in compatibility policy, **not proof of an exploit-free economy**.
Retaining a value in a productive cycle can permit EMC profit. Assigning one EMC
to a fractional recipe output can also create profit (for example, two one-EMC
slabs from one one-EMC stone). Fixed custom values can conflict with recipe costs.

The local gameplay build improved mapping coverage, but reports of missing iron
nuggets and honey treats motivated its final recovery pass. That final pass was
not confirmed in-game in the available record. The cleaned source additionally
preserves free values, excludes item tags from the minimum, and makes recovery
opt-in; it is therefore not byte-identical to the installed gameplay JAR.

The regression tests exercise synthetic recipe graphs, including representative
nugget, bucket, slab, and covalence-dust costs. They do not reproduce every recipe
or prove a honey-treat fix in a complete Productive Bees modpack. Large-pack
runtime validation, recipe-order sensitivity, dependency tracking cost, and
maintainer agreement on the policy are prerequisites for leaving draft status.

The submitted repository does not contain saves, player data, imported EMC tables,
third-party mod JARs, or the separate integration-mod patches.
