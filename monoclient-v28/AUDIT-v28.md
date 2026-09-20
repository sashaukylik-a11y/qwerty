# MonoClient Full v28 — detector + Right Shift audit

## Result
- **8/8 audit groups passed**
- **91/91 checks passed**
- **21/21 combat scenario-model checks passed**
- Compiler/linker: **0 warnings / 0 errors** under `/W4 /WX`
- Clang static analyzer: **0 diagnostics**
- Final EXE + three `/brepro` builds are byte-identical.

## Pulse / Java detection
The primary JVM-host path now explicitly mirrors the supplied detector logic:
1. enumerate processes with `TH32CS_SNAPPROCESS`;
2. keep names containing `pulse`, `minecraft`, `javaw`, or `java`;
3. enumerate modules with `TH32CS_SNAPMODULE | TH32CS_SNAPMODULE32`;
4. accept a JVM host only when exact `jvm.dll` is present.

The Minecraft window owner and JVM-host PID remain separate. Toolhelp module enumeration retries transient `ERROR_BAD_LENGTH` and records `ACCESS_DENIED` / `PARTIAL_COPY` diagnostics. PEB/Ldr and embedded/manual-map recovery remain later fallbacks.

## Administrator level
The final PE contains an embedded UAC manifest with `requestedExecutionLevel level='requireAdministrator'`. This prevents a lower-integrity MonoClient instance from silently failing module enumeration against an elevated Pulse/Java process.

## Right Shift / menu fix
- Dedicated background RSHIFT edge worker runs every 16 ms and is independent of the menu window visibility/z-order.
- Existing UI timer remains as a fallback detector.
- 140 ms debounce prevents both paths from toggling twice for one press.
- Opening forces the menu to TOPMOST before showing/activating it.
- A visible menu stays reachable even with `Поверх окон` disabled.
- Closing restores the configured topmost state and Minecraft focus.
- Esc and menu close release mouse capture and slider drag state.

## Combat retained
- Players / Mobs independent.
- Sword / Mace independent.
- 13 / 34 attack-tick gates.
- 625 / 1670 ms fallback.
- Critical-only uses `onGround`/`fallDistance`, not Space.
- Water, eye-water, vehicle, sprint guards.
- Mace threshold >= 1.50.
- See Invisible runtime remains removed.

## PE/import audit
Present: `ReadProcessMemory`, `SendInput`, Toolhelp process/module APIs.
Absent: `WriteProcessMemory`, `VirtualAllocEx`, `CreateRemoteThread`, `SetWindowsHookEx*`.

## SHA-256
`d6dc2f19f4d90c0fb1fc96aa4143b159d1dbb7242187714113820050a6ac37b0`

## Live-test limitation
No Windows Pulse Visual instance is available in this environment. The audit validates the compiled paths and behavior model; it does not claim a successful live attach on the user's Pulse build.
