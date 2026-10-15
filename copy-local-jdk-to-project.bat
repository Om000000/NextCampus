@echo off
title NextCampus - Copy Local JDK to Project
cd /d "%~dp0"

echo ========================================================
echo        NextCampus - Portable JDK Copier
echo ========================================================
echo.

set "SOURCE_JDK=C:\Program Files\Java\jdk-26.0.2"

if not exist "%SOURCE_JDK%\bin\java.exe" (
    echo [ERROR] Could not find JDK at '%SOURCE_JDK%'
    echo Please verify that Java is installed in C:\Program Files\Java
    pause
    exit /b 1
)

if exist "%~dp0jdk\bin\java.exe" (
    echo [OK] Portable 'jdk' folder already exists in this project!
    echo Location: %~dp0jdk
    echo You are ready to copy the whole project to your pen drive!
    pause
    exit /b 0
)

echo Found installed JDK at: %SOURCE_JDK%
echo Copying to '%~dp0jdk' (~370 MB)...
echo This will take about 20-30 seconds. Please wait...
echo.

robocopy "%SOURCE_JDK%" "%~dp0jdk" /E /NFL /NDL /NJH /NJS /nc /ns /np

if exist "%~dp0jdk\bin\java.exe" (
    echo.
    echo ========================================================
    echo [SUCCESS] Portable JDK successfully copied to 'jdk\'!
    echo.
    echo Now, whenever you copy this project to any Pen Drive,
    echo NextCampus will run on ANY computer even if Java is
    echo not installed on that computer!
    echo ========================================================
) else (
    echo [ERROR] Copy failed. Please check disk space.
)

echo.
pause
