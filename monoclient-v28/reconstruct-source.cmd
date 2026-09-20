@echo off
setlocal
if exist MonoClient.cpp del /q MonoClient.cpp
copy /b source-parts\MonoClient.cpp.part00.txt+source-parts\MonoClient.cpp.part01.txt+source-parts\MonoClient.cpp.part02.txt+source-parts\MonoClient.cpp.part03.txt+source-parts\MonoClient.cpp.part04.txt+source-parts\MonoClient.cpp.part05.txt+source-parts\MonoClient.cpp.part06.txt MonoClient.cpp >nul
if errorlevel 1 exit /b 1
echo Reconstructed MonoClient.cpp
echo Expected source SHA-256:
echo d06a9bece235f8e87b5da3c59dd62ecd19eccb48c2afccfbeb598bae6f58a778
certutil -hashfile MonoClient.cpp SHA256
