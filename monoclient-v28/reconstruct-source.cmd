@echo off
setlocal
if exist MonoClient.cpp del /q MonoClient.cpp
copy /b source-parts\MonoClient.cpp.part00.txt+source-parts\MonoClient.cpp.part01.txt+source-parts\MonoClient.cpp.part02.txt+source-parts\MonoClient.cpp.part03.txt+source-parts\MonoClient.cpp.part04.txt+source-parts\MonoClient.cpp.part05.txt+source-parts\MonoClient.cpp.part06.txt MonoClient.cpp >nul
if errorlevel 1 exit /b 1
echo Reconstructed MonoClient.cpp
echo Expected source SHA-256:
echo 038252f1bd9d8aa3ef83cd1dd0758fdd50bbb14a5da030df4fc7a76691f133fa
certutil -hashfile MonoClient.cpp SHA256
