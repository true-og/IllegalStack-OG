# IllegalStack-OG

A fork of IllegalStack maintained by [TrueOG Network](https://true-og.net). IllegalStack is a Spigot-based plugin dedicated to fixing glitches and exploits that have made it into final Minecraft releases.

## Changes from IllegalStack

- Completely removed references to the proprietary JetsMinions API. — @NotAlexNoyle

- Disabled automatic configuration updates and migrations. — @NotAlexNoyle

- Builds with TrueOG Network's config file, including detailed inline documentation. — @NotAlexNoyle

- Updated Gradle from 8.1.1 to 8.14.3 and modernized the build tooling. — @NotAlexNoyle

- Optimized the hopper transfer event handler (`onHopperXfer`) to reduce CPU overhead. — @NotAlexNoyle
  - `CheckEntireInventory` skips `RemoveItemTypesCheck` when `RemoveItemsOfType` is empty. — @NotAlexNoyle
  - `CheckEntireInventory` returns early when none of its sub-checks are enabled. — @NotAlexNoyle
  - The `DisableInWorlds` check runs before `CheckEntireInventory`, allowing disabled worlds to skip the expensive inventory scan. — @NotAlexNoyle

- Added thread-safe APIs that allow other plugins to exempt players from timer-based item checks. — @NotAlexNoyle

- Fixed vanilla default attribute modifiers being treated as illegal custom attributes. — @NotAlexNoyle

- Fixed dyed leather armor and other items with legitimate vanilla metadata being removed by creative-slot protection. — @NotAlexNoyle

- Refined `BlockBadItemsFromCreativeTab` to reject unsafe attribute payloads without rejecting ordinary metadata, and added automated tests for the behavior. — @NotAlexNoyle

- Removed hardcoded operator bypasses so protections consistently respect the permission system. — @GWServer

- Fixed modern server-version detection and changed configured item-name and item-lore startup logging to concise entry counts. — @NotAlexNoyle

## Building

> ./gradlew clean build eclipse

The usable jar is located at `/build/libs/IllegalStack<version>.jar`.
