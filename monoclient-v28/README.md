# MonoClient Full v28 — audited hotfix

Minecraft 1.21.11 external Win32 x64 client by **MonoClient | @monobrowser**.

## Current hotfix

- Right Shift is handled by one authoritative UI-thread edge detector.
- The hidden menu keeps a 16 ms Win32 timer, so short RSHIFT taps are not lost in a 100 ms idle gap.
- The old dedicated hotkey worker was removed, eliminating worker/timer races.
- Primary input is `VK_RSHIFT`; a generic `VK_SHIFT` fallback is accepted only when `VK_LSHIFT` is not down.
- Open/close state follows `g_menuAnimTarget`, so a reopen during the 180 ms close animation is not consumed.
- Visible menu is temporarily TOPMOST even when persistent **Поверх окон** is disabled; closing restores the configured z-order and game focus.
- JVM attach continues while TriggerBot is disabled. The toggle gates attacks, not process discovery.
- The JVM attach worker remains joinable during shutdown.
- Persisted config values are range-validated before use.

## Pulse / JVM discovery

The primary path enumerates process-name candidates, then accepts a host only when exact `jvm.dll` is found through Toolhelp module enumeration. Window PID and JVM-host PID are intentionally separate. PEB/Ldr and embedded/manual-image HotSpot recovery remain later read-only fallbacks.

## Build and validation

Reconstruct the monolithic source:

```bat
reconstruct-source.cmd
```

Build the production x64 GUI executable:

```bat
build.cmd
```

Build the isolated Right Shift regression binary:

```bat
build-rshift-selftest.cmd
```

GitHub Actions run **35529711631** passed the full pipeline:

- reconstructed source SHA verification;
- `/W4 /WX` production compile and `/brepro` link;
- integrated **96/96** audit;
- PE32+ / embedded `requireAdministrator` validation;
- Windows startup smoke test;
- isolated RSHIFT `open → close → reopen` self-test;
- byte-for-byte reproducibility rebuild;
- artifact upload.

Production SHA-256: `5bb2c767d0d53ce4a70bbfde053a1332d1dd28894db6b417df9511df2a0e61d2`

Source SHA-256: `d06a9bece235f8e87b5da3c59dd62ecd19eccb48c2afccfbeb598bae6f58a778`

The RSHIFT self-test validates the shared edge handler and menu animation/state transitions inside the compiled Windows binary. A physical key press and live Pulse/JVM attach still need final confirmation on the target machine.

## Import / safety shape

Static PE imports are exactly `KERNEL32.dll`, `USER32.dll`, and `GDI32.dll`. The build contains `ReadProcessMemory`, `SendInput`, and Toolhelp enumeration APIs. It does not contain `WriteProcessMemory`, `VirtualAllocEx`, `CreateRemoteThread`, or `SetWindowsHookEx*`.
