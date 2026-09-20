# MonoClient Full v28 — source snapshot

Minecraft 1.21.11 external Win32 x64 client by **MonoClient | @monobrowser**.

This snapshot was uploaded from the working v28 build.

## v28 changes

- Pulse/JVM detection follows the exact Toolhelp approach first:
  1. `CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS)`
  2. process-name candidates containing `pulse`, `minecraft`, `javaw`, or `java`
  3. `CreateToolhelp32Snapshot(TH32CS_SNAPMODULE | TH32CS_SNAPMODULE32, pid)`
  4. accept a JVM host only when exact `jvm.dll` is loaded.
- Minecraft window PID and JVM host PID are intentionally separate.
- The executable uses a `requireAdministrator` UAC manifest so module enumeration does not silently fail when Pulse/Java is elevated.
- Right Shift menu opening is independent of the **Поверх окон / Always on top** setting:
  - dedicated 16 ms RSHIFT edge worker;
  - timer fallback runs only if that worker could not be created;
  - 50 ms duplicate-edge debounce;
  - opening immediately switches the UI timer to 16 ms and makes the first frame visible;
  - reopening during the close animation follows the animation target, so the first press is not consumed;
  - opening temporarily raises the menu to TOPMOST before activation;
  - closing restores the configured topmost state and Minecraft focus.
- Read-only HotSpot fallbacks remain after the exact `jvm.dll` path.
- No `WriteProcessMemory`, `VirtualAllocEx`, `CreateRemoteThread`, or Windows hooks are used.

## Source layout

The complete monolithic `MonoClient.cpp` is stored losslessly as ordered UTF-8 parts under `source-parts/` because this connector uploads text files individually.

On Windows run:

```bat
reconstruct-source.cmd
```

It recreates `MonoClient.cpp` from the ordered parts.

## Build

Requires LLVM/clang-cl + lld-link and the Windows SDK libraries:

```bat
build.cmd
```

## Validation

See `AUDIT-v28.md`, `PE_IMPORTS-v28.txt` and `SHA256_REPRO-v28.txt`.

The build was statically/reproducibly checked in the development environment. A live Pulse Visual session is not available there, so live in-game attach still has to be confirmed on the target Windows machine.

> Hotfix note: the source parts in this branch differ from the original pre-hotfix v28 executable used for the historical PE/reproducibility report. Rebuild `MonoClient.cpp` before using binary hashes as validation.

- Hotfix: JVM/process attach now continues even while TriggerBot is disabled; the toggle gates attacks, not process discovery.
