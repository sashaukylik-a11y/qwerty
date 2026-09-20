#include <windows.h>
#include <tlhelp32.h>
#include <iostream>
#include <string>
#include <algorithm>

std::string ToLower(std::string str) {
    std::transform(str.begin(), str.end(), str.begin(),
                   [](unsigned char c) { return static_cast<char>(std::tolower(c)); });
    return str;
}

bool HasModule(DWORD pid, const std::string& targetModule) {
    HANDLE hModuleSnap = CreateToolhelp32Snapshot(
        TH32CS_SNAPMODULE | TH32CS_SNAPMODULE32, pid);
    if (hModuleSnap == INVALID_HANDLE_VALUE) return false;

    MODULEENTRY32 me32{};
    me32.dwSize = sizeof(me32);
    bool found = false;

    if (Module32First(hModuleSnap, &me32)) {
        do {
            if (ToLower(me32.szModule) == ToLower(targetModule)) {
                found = true;
                break;
            }
        } while (Module32Next(hModuleSnap, &me32));
    }

    CloseHandle(hModuleSnap);
    return found;
}

DWORD GetPulseOrMinecraftPID() {
    HANDLE hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (hProcessSnap == INVALID_HANDLE_VALUE) return 0;

    PROCESSENTRY32 pe32{};
    pe32.dwSize = sizeof(pe32);
    DWORD targetPid = 0;

    if (Process32First(hProcessSnap, &pe32)) {
        do {
            std::string exeName = ToLower(pe32.szExeFile);

            const bool isCandidate =
                exeName.find("pulse") != std::string::npos ||
                exeName.find("minecraft") != std::string::npos ||
                exeName.find("javaw") != std::string::npos ||
                exeName.find("java") != std::string::npos;

            if (isCandidate && HasModule(pe32.th32ProcessID, "jvm.dll")) {
                targetPid = pe32.th32ProcessID;
                break;
            }
        } while (Process32Next(hProcessSnap, &pe32));
    }

    CloseHandle(hProcessSnap);
    return targetPid;
}

int main() {
    std::cout << "[*] Pulse Visual / JNI JVM detector..." << std::endl;

    const DWORD pid = GetPulseOrMinecraftPID();
    if (pid) {
        std::cout << "[+] JVM host PID: " << pid << std::endl;
    } else {
        std::cout << "[-] JVM host not found." << std::endl;
    }

    system("pause");
    return 0;
}
