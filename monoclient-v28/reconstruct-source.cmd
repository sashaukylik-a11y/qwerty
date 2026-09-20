@echo off
setlocal
if exist MonoClient.cpp del /q MonoClient.cpp
copy /b source-parts\MonoClient.cpp.part00.txt+source-parts\MonoClient.cpp.part01.txt+source-parts\MonoClient.cpp.part02.txt+source-parts\MonoClient.cpp.part03.txt+source-parts\MonoClient.cpp.part04.txt+source-parts\MonoClient.cpp.part05.txt+source-parts\MonoClient.cpp.part06.txt MonoClient.cpp >nul
if errorlevel 1 exit /b 1
echo Reconstructed MonoClient.cpp
echo Expected source SHA-256:
echo e0f33ff9844a2ab290a5dac4286830f0533d48231c47f414983467b2f929be4e
certutil -hashfile MonoClient.cpp SHA256
