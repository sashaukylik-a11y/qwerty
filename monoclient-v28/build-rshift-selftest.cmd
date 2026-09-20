@echo off
setlocal
clang-cl /nologo /c /O2 /std:c++17 /W4 /WX /GS- /Gs9999999 /GR- /EHs-c- /DUNICODE /D_UNICODE /DMONO_CI_RSHIFT_TEST /Fo:MonoClient-RSHIFT-selftest.obj MonoClient.cpp || exit /b 1
lld-link /nologo /brepro /entry:wWinMainCRTStartup /subsystem:windows /nodefaultlib /machine:x64 /manifest:embed /manifestuac:"level='requireAdministrator' uiAccess='false'" /out:MonoClient-RSHIFT-selftest.exe MonoClient-RSHIFT-selftest.obj kernel32.lib user32.lib gdi32.lib || exit /b 1
echo Built MonoClient-RSHIFT-selftest.exe
