@echo off
setlocal
if exist MonoClient.cpp del /q MonoClient.cpp
copy /b source-parts\MonoClient.cpp.part00.txt+source-parts\MonoClient.cpp.part01.txt+source-parts\MonoClient.cpp.part02.txt+source-parts\MonoClient.cpp.part03.txt+source-parts\MonoClient.cpp.part04.txt+source-parts\MonoClient.cpp.part05.txt+source-parts\MonoClient.cpp.part06.txt MonoClient.cpp >nul
if errorlevel 1 exit /b 1
echo Reconstructed MonoClient.cpp
echo Expected source SHA-256:
echo e38647f87ac681e0c92aef1bc0cb022d8422559f4a82162f0de63b64b3ab54bf
certutil -hashfile MonoClient.cpp SHA256
