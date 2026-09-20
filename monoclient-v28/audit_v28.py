from pathlib import Path
import hashlib,re,sys
root=Path(__file__).resolve().parent
src=(root/'MonoClient.cpp').read_text(encoding='utf-8')
exe=(root/'MonoClient-Full-v28.exe').read_bytes()
pe=(root/'PE_IMPORTS-v28.txt').read_text(errors='ignore')
checks=[]
def ck(group,name,cond): checks.append((group,name,bool(cond)))
def section(a,b):
    i=src.index(a); j=src.index(b,i+len(a)); return src[i:j]

# 1 build + manifest
ck(1,'v28 branding','full v28 • exact jvm.dll • reliable RSHIFT' in src)
ck(1,'PE x64',exe[:2]==b'MZ' and b'PE\0\0' in exe[:4096])
ck(1,'requireAdministrator manifest embedded',b'requireAdministrator' in exe)
ck(1,'compiler/link clean',all((root/f).stat().st_size==0 for f in ['compile.err','link.err']))
ck(1,'static analyzer clean',all((root/f).stat().st_size==0 for f in ['analyze.out','analyze.err']))
hashes=[]
for f in ['MonoClient-Full-v28.exe','repro1.exe','repro2.exe','repro3.exe']:
    hashes.append(hashlib.sha256((root/f).read_bytes()).hexdigest())
ck(1,'3 reproducible builds + final identical',len(set(hashes))==1)

# 2 exact Pulse/Java detector copied from user's approach
blk=section('static int BuildExactJvmDllHosts','static ULONGLONG GetProcCreateTimeValue')
nameblk=section('static BOOL IsGeminiJvmCandidateName','// Primary JVM-host discovery')
ck(2,'process snapshot enumeration','CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS' in blk and 'Process32FirstW' in blk and 'Process32NextW' in blk)
ck(2,'candidate names pulse/minecraft/java/javaw',all(x in nameblk for x in ['L"pulse"','L"minecraft"','L"javaw"','L"java"']))
ck(2,'exact module snapshot', 'TH32CS_SNAPMODULE|TH32CS_SNAPMODULE32' in src)
ck(2,'exact jvm.dll acceptance', 'WEqualI(me.szModule,L"jvm.dll")' in src and 'ProcessHasJvmDll(pid)' in blk)
ck(2,'exact path first in attach', section('static BOOL BridgeAttachPid','static DWORD WINAPI BridgeAttachWorker').index('BuildExactJvmDllHosts') < section('static BOOL BridgeAttachPid','static DWORD WINAPI BridgeAttachWorker').index('BuildBridgeHostList'))
ck(2,'ERROR_BAD_LENGTH retry','for(int attempt=0;attempt<6;attempt++)' in src and 'e!=24' in src)
ck(2,'module ACCESS_DENIED diagnostic','ACCESS_DENIED' in src and 'g_diagModuleDenied' in src)
ck(2,'module PARTIAL_COPY diagnostic','PARTIAL_COPY' in src and 'g_diagModulePartial' in src)
ck(2,'window PID and JVM PID separated','Window PID ' in src and 'JVM PID ' in src)

# 3 resilient fallback remains
findj=section('static U64 FindJvmBase','static U64 RemoteExport')
ck(3,'PEB/Ldr fallback','EnumeratePebModules' in findj)
ck(3,'MEM_IMAGE fallback','MEM_IMAGE' in findj)
ck(3,'MEM_MAPPED fallback','MEM_MAPPED' in findj)
ck(3,'MEM_PRIVATE fallback','MEM_PRIVATE' in findj)
ck(3,'VMStruct marker fallback','HasHotSpotMarkersRange' in findj)
ck(3,'attach worker idle priority','THREAD_PRIORITY_IDLE' in section('static DWORD WINAPI BridgeAttachWorker','static BOOL BridgeEnsure'))

# 4 reliable global Right Shift menu
ck(4,'dedicated RSHIFT worker','static DWORD WINAPI MenuHotkeyWorker' in src)
worker=section('static DWORD WINAPI MenuHotkeyWorker','static LRESULT CALLBACK MainProc')
ck(4,'worker reads VK_RSHIFT globally','GetAsyncKeyState(VK_RSHIFT)' in worker)
ck(4,'worker independent of menu HWND visibility','ShowWindow' not in worker and 'IsWindowVisible' not in worker)
ck(4,'worker low overhead 16ms sleep','Sleep(16)' in worker)
ck(4,'custom toggle message','WM_MONO_TOGGLE' in src and 'SendMessageW(g_main,WM_MONO_TOGGLE' in worker)
ck(4,'timer polling fallback only if worker unavailable','if(!g_hotkeyThread)' in src and 'if(kd&&!g_rshiftDown)ToggleMenuReliable();' in src)
ck(4,'short duplicate-edge debounce','g_lastMenuToggleTick' in src and 'now-g_lastMenuToggleTick<50' in src)
ck(4,'visible menu forced topmost even setting off','(g_menuVisible||g_cfg.alwaysOnTop)?HWND_TOPMOST:HWND_NOTOPMOST' in src)
ck(4,'open path raises before show','SetWindowPos(g_main,HWND_TOPMOST' in section('static void OpenMenuReliable','static void CloseMenuReliable'))
ck(4,'open path foreground request','SetForegroundWindow(g_main)' in section('static void OpenMenuReliable','static void CloseMenuReliable'))
ck(4,'open wakes 16ms animation timer','g_uiTimerMs=16;SetTimer(g_main,1,g_uiTimerMs,0);' in section('static void OpenMenuReliable','static void CloseMenuReliable'))
ck(4,'reopen follows animation target','static void ToggleMenuReliable(){if(g_menuAnimTarget>0)CloseMenuReliable();else OpenMenuReliable();}' in src)
ck(4,'close releases capture','ReleaseCapture();g_dragSlider=0' in section('static void CloseMenuReliable','static void ToggleMenuReliable'))
ck(4,'Esc uses reliable close','WM_KEYDOWN&&w==VK_ESCAPE' in src and 'CloseMenuReliable()' in src)
ck(4,'closing restores configured topmost + game focus','UpdateTopmost();RestoreGameFocus();' in src)

# 5 combat retained
for fld in ['field_1692','field_1724','field_1755','field_7525','field_6273','field_5952','field_6017','field_6034','field_5957','field_6000','field_8038','field_49814']:
    ck(5,f'mapping {fld}',fld in src)
ck(5,'players/mobs filters','g_cfg.hitPlayers' in src and 'g_cfg.hitMobs' in src)
ck(5,'sword/mace filters','g_cfg.useSword' in src and 'g_cfg.useMace' in src)
trig=section('static void TriggerTick','static BOOL RectGood')
ck(5,'13/34 tick gate','WPN_MACE)?34:13' in trig)
ck(5,'625/1670 fallback','1670:625' in trig)
ck(5,'critical no Space','GetAsyncKeyState(VK_SPACE)' not in src)
ck(5,'critical fall gate','fallDistance<=0.0' in trig and 'onGround' in trig)
ck(5,'water/vehicle/sprint guards',all(x in trig for x in ['touchWater','eyeWater','hasVehicle','sprinting']))
ck(5,'mace 1.50 threshold','fallDistance<1.50' in trig)

# 6 performance
eng=section('static DWORD WINAPI EngineWorker','static LRESULT CALLBACK HudProc')
ck(6,'engine below normal','THREAD_PRIORITY_BELOW_NORMAL' in eng)
ck(6,'disabled sleeps 180ms','Sleep(180)' in eng)
ck(6,'disabled trigger still attaches JVM','if(!g_cfg.triggerEnabled)' in eng and 'BridgeEnsure();' in eng)
ck(6,'background sleeps 100ms','Sleep(100)' in eng)
ck(6,'active loop 16ms','Sleep(16)' in eng)
ck(6,'See Invisible disabled','g_cfg.seeInvisible=0' in src and 'WorldToScreen' not in src)

# 7 import/safety shape
imports=re.findall(r'DLL Name:\s*([^\r\n]+)',pe)
ck(7,'only kernel/user/gdi imports',set(imports)<= {'KERNEL32.dll','USER32.dll','GDI32.dll'} and bool(imports))
for good in ['ReadProcessMemory','SendInput','CreateToolhelp32Snapshot','Module32FirstW','Module32NextW']:
    ck(7,f'import {good}',good in pe)
for bad in ['WriteProcessMemory','VirtualAllocEx','CreateRemoteThread','SetWindowsHookExW','SetWindowsHookExA']:
    ck(7,f'no {bad}',bad not in pe and bad not in src)

# 8 scenario mirror
def allow(enabled=True,ready=True,target=True,weapon='sword',use_sword=True,use_mace=True,kind='mob',hit_players=True,hit_mobs=True,ticker_known=True,ticker=13,last_elapsed=9999,critical=False,crit_known=True,on_ground=False,fall=.2,water=False,eye=False,vehicle=False,sprint=False):
    if not enabled or not ready or not target:return False
    if weapon=='sword' and not use_sword:return False
    if weapon=='mace' and not use_mace:return False
    if weapon not in ('sword','mace'):return False
    if kind=='player' and not hit_players:return False
    if kind=='mob' and not hit_mobs:return False
    if kind not in ('player','mob'):return False
    if ticker_known:
        if ticker < (34 if weapon=='mace' else 13):return False
    elif last_elapsed < (1670 if weapon=='mace' else 625):return False
    if critical:
        if not crit_known:return False
        if on_ground or fall<=0 or water or eye or vehicle or sprint:return False
        if weapon=='mace' and fall<1.50:return False
    return True
sc=[
('mob sword',{},True),('player sword',{'kind':'player'},True),('mob off',{'hit_mobs':False},False),('player off',{'kind':'player','hit_players':False},False),
('sword12',{'ticker':12},False),('sword13',{'ticker':13},True),('mace33',{'weapon':'mace','ticker':33},False),('mace34',{'weapon':'mace','ticker':34},True),
('sword fallback 624',{'ticker_known':False,'last_elapsed':624},False),('sword fallback 625',{'ticker_known':False,'last_elapsed':625},True),
('mace fallback 1669',{'weapon':'mace','ticker_known':False,'last_elapsed':1669},False),('mace fallback 1670',{'weapon':'mace','ticker_known':False,'last_elapsed':1670},True),
('crit falling no Space',{'critical':True,'fall':.2},True),('crit ground',{'critical':True,'on_ground':True},False),('crit water',{'critical':True,'water':True},False),('crit eye',{'critical':True,'eye':True},False),('crit vehicle',{'critical':True,'vehicle':True},False),('crit sprint',{'critical':True,'sprint':True},False),('crit unknown',{'critical':True,'crit_known':False},False),
('mace 1.49',{'weapon':'mace','ticker':34,'critical':True,'fall':1.49},False),('mace 1.50',{'weapon':'mace','ticker':34,'critical':True,'fall':1.50},True),
]
for n,kw,e in sc: ck(8,'scenario '+n,allow(**kw)==e)

failed=[x for x in checks if not x[2]]
for g in sorted(set(x[0] for x in checks)):
    vals=[x for x in checks if x[0]==g]
    print(f'AUDIT {g}: {sum(x[2] for x in vals)}/{len(vals)} PASS')
    for _,n,v in vals:
        print(('  PASS ' if v else '  FAIL ')+n)
print(f'\nTOTAL {sum(x[2] for x in checks)}/{len(checks)} PASS')
print('SHA256',hashes[0])
if failed:
    print('FAILED:')
    for _,n,_ in failed: print('-',n)
    sys.exit(1)
