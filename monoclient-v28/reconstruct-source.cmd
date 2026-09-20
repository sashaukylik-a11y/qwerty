@echo off
setlocal
if exist MonoClient.cpp del /q MonoClient.cpp
copy /b source-parts\MonoClient.cpp.part00.txt+source-parts\MonoClient.cpp.part01.txt+source-parts\MonoClient.cpp.part02.txt+source-parts\MonoClient.cpp.part03.txt+source-parts\MonoClient.cpp.part04.txt+source-parts\MonoClient.cpp.part05.txt+source-parts\MonoClient.cpp.part06.txt+source-parts\MonoClient.cpp.part07.txt+source-parts\MonoClient.cpp.part08.txt+source-parts\MonoClient.cpp.part09.txt+source-parts\MonoClient.cpp.part10.txt+source-parts\MonoClient.cpp.part11.txt+source-parts\MonoClient.cpp.part12.txt+source-parts\MonoClient.cpp.part13.txt+source-parts\MonoClient.cpp.part14.txt MonoClient.cpp >nul
if errorlevel 1 exit /b 1
echo Reconstructed MonoClient.cpp
