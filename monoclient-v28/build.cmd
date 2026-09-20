@echo off
setlocal
clang-cl /nologo /c /O2 /std:c++17 /W4 /WX /GS- /Gs9999999 /GR- /EHs-c- /DUNICODE /D_UNICODE /Fo:MonoClient.obj MonoClient.cpp || exit /b 1
lld-link /nologo /brepro /entry:wWinMainCRTStartup /subsystem:windows /nodefaultlib /machine:x64 /manifest:embed /manifestinput:MonoClient.manifest /out:MonoClient-Full-v28.exe MonoClient.obj kernel32.lib user32.lib gdi32.lib || exit /b 1
echo Built MonoClient-Full-v28.exe
