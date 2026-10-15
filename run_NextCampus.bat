@echo off
setlocal enabledelayedexpansion
title NextCampus – College Student Management System
cd /d "%~dp0java"

echo ========================================================
echo        NextCampus - College Management System
echo            Teacher & Admin Desktop App (Swing)
echo ========================================================
echo.

:: 1. Check portable JDK in project or pen drive root
if exist "%~dp0jdk\bin\java.exe" (
    set "JAVA_EXE=%~dp0jdk\bin\java.exe"
    set "JAVAC_EXE=%~dp0jdk\bin\javac.exe"
    echo [OK] Using portable Java from: !JAVA_EXE!
    goto FOUND_JAVA
)
if exist "%~dp0..\jdk\bin\java.exe" (
    set "JAVA_EXE=%~dp0..\jdk\bin\java.exe"
    set "JAVAC_EXE=%~dp0..\jdk\bin\javac.exe"
    echo [OK] Using portable Java from: !JAVA_EXE!
    goto FOUND_JAVA
)
if exist "%~dp0java\jdk\bin\java.exe" (
    set "JAVA_EXE=%~dp0java\jdk\bin\java.exe"
    set "JAVAC_EXE=%~dp0java\jdk\bin\javac.exe"
    echo [OK] Using portable Java from: !JAVA_EXE!
    goto FOUND_JAVA
)

:: 2. Check standard Java install locations
if exist "C:\Program Files\Java\jdk-26.0.2\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Java\jdk-26.0.2\bin\java.exe"
    set "JAVAC_EXE=C:\Program Files\Java\jdk-26.0.2\bin\javac.exe"
    goto FOUND_JAVA
)

:: 3. Check system PATH
where java >nul 2>&1
if !errorlevel! equ 0 (
    set "JAVA_EXE=java"
    set "JAVAC_EXE=javac"
    goto FOUND_JAVA
)

:: If not found anywhere
echo [ERROR] Java is not installed or not in PATH on this computer!
echo.
echo To run on any PC without installing Java:
echo 1. Download OpenJDK zip (from https://adoptium.net)
echo 2. Extract it into a folder named 'jdk' next to this file.
echo.
pause
exit /b 1

:FOUND_JAVA
echo [OK] Java runtime detected: !JAVA_EXE!
echo.

:: Check if compiler is available
where "!JAVAC_EXE!" >nul 2>&1
if !errorlevel! equ 0 (
    echo Compiling latest source files...
    if not exist out mkdir out
    "!JAVAC_EXE!" -cp "lib/*;src" -d out ^
      src\com\college\sms\*.java ^
      src\com\college\sms\db\*.java ^
      src\com\college\sms\model\*.java ^
      src\com\college\sms\dao\*.java ^
      src\com\college\sms\ui\*.java ^
      src\com\college\sms\util\*.java
    if !errorlevel! neq 0 (
        echo [WARNING] Compilation had warnings/errors, using existing classes...
    ) else (
        echo [OK] Compilation successful!
    )
) else (
    echo [INFO] Compiler 'javac' not found on this machine. Using precompiled classes...
)

:: Check for compiled Main.class
if not exist "out\com\college\sms\Main.class" (
    echo [ERROR] Compiled classes not found in 'java\out'!
    pause
    exit /b 1
)

:: Launch GUI
echo.
echo ========================================================
echo   Launching NextCampus Desktop GUI...
echo ========================================================
"!JAVA_EXE!" --enable-native-access=ALL-UNNAMED -cp "lib/*;out" com.college.sms.Main gui
if !errorlevel! neq 0 (
    echo.
    echo NextCampus exited with an error.
    pause
)
