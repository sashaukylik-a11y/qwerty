# MonoClient Full v28 — final audit

## Result

- **8/8 audit groups passed**
- **96/96 checks passed**
- **21/21 combat scenario-model checks passed**
- Production compile: **PASS** under `/W4 /WX`
- Production link: **PASS** with `/brepro`
- Windows startup smoke test: **PASS**
- Internal Right Shift regression: **PASS**
- Reproducibility rebuild: **PASS**, byte-identical
- CI run: **35529711631**
- Production SHA-256: `5bb2c767d0d53ce4a70bbfde053a1332d1dd28894db6b417df9511df2a0e61d2`
- Source SHA-256: `d06a9bece235f8e87b5da3c59dd62ecd19eccb48c2afccfbeb598bae6f58a778`

## Right Shift

The previous two-detector design was removed. RSHIFT now uses one 16 ms UI-thread polling path and one shared rising-edge state machine. The hidden window keeps the same 16 ms timer. Generic `VK_SHIFT` is only a fallback when `VK_LSHIFT` is not active, so left Shift is not treated as the menu key.

A separately compiled `MONO_CI_RSHIFT_TEST` binary exercised the same edge handler and verified:

1. first event opens the menu and reaches the fully-open animation target;
2. second event closes the menu and reaches alpha/state zero;
3. third event reopens it.

## PE / manifest / imports

- PE32+ x86-64 Windows GUI
- 6 sections including `.rsrc`
- embedded `requireAdministrator` manifest
- static import DLLs: `KERNEL32.dll`, `USER32.dll`, `GDI32.dll`
- present: `ReadProcessMemory`, `SendInput`, Toolhelp process/module enumeration
- absent: `WriteProcessMemory`, `VirtualAllocEx`, `CreateRemoteThread`, `SetWindowsHookExW/A`

## JVM / process path

Exact `jvm.dll` Toolhelp discovery remains primary. Window PID and JVM PID are separate. Module enumeration handles transient `ERROR_BAD_LENGTH` and records `ACCESS_DENIED` / `PARTIAL_COPY`. Read-only PEB/Ldr and embedded HotSpot fallbacks remain available. JVM attach runs even while TriggerBot is disabled.

## Limitation

The CI self-test validates the compiled menu state machine, not a human physical keyboard on the user's desktop. Live Pulse/JVM behavior must still be confirmed on the target Windows machine.
