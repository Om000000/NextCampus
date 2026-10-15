@echo off
setlocal enabledelayedexpansion
title NextCampus - Lab PC Launcher
cd /d "%~dp0"

echo ========================================================
echo        NextCampus - College Management System
echo            Examiner Presentation Launcher
echo ========================================================
echo.

:: 1. Check if Docker is installed and running
where docker >nul 2>&1
if !errorlevel! equ 0 (
    echo [OK] Docker detected on this machine!
    echo Checking if Docker engine is running...
    docker info >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Docker engine is running and ready!
        goto RUN_DOCKER
    ) else (
        echo [WARNING] Docker is installed, but Docker Desktop is not started yet.
        echo Starting in Native Windows Mode...
        goto RUN_NATIVE
    )
) else (
    echo [INFO] Docker is NOT installed on this lab PC.
    echo Don't worry! Running in Native Windows Mode...
    goto RUN_NATIVE
)

:RUN_DOCKER
echo.
echo ========================================================
echo   Launching with Docker...
echo ========================================================

:: Check if image is already loaded, otherwise load from tar
docker image inspect nextcampus:latest >nul 2>&1
if !errorlevel! neq 0 (
    if exist nextcampus-image.tar (
        echo Loading portable Docker image from 'nextcampus-image.tar'...
        docker load -i nextcampus-image.tar
    ) else (
        echo [ERROR] 'nextcampus-image.tar' not found in this folder!
        echo Falling back to Native Mode...
        goto RUN_NATIVE
    )
)

:: Stop any old container if running
docker rm -f nextcampus-lab >nul 2>&1

:: Run container
echo Starting web container 'nextcampus-lab'...
docker run -d --name nextcampus-lab -p 5000:5000 -v "%cd%\database:/app/database" nextcampus:latest web

echo.
echo ========================================================
echo   NextCampus Web Portal is LIVE!
echo   Opening in your browser: http://localhost:5000
echo ========================================================
timeout /t 2 >nul
start http://localhost:5000

echo.
set /p launchgui="Do you also want to launch the Teacher Desktop App (Java Swing)? (y/n): "
if /i "!launchgui!"=="y" (
    start "NextCampus Desktop GUI" cmd /c "%~dp0run_NextCampus.bat"
)

echo.
echo To view live Docker logs: docker logs -f nextcampus-lab
echo To stop container:        docker stop nextcampus-lab
echo.
pause
exit /b 0

:RUN_NATIVE
echo.
echo ========================================================
echo   Launching Native Windows Mode (No Docker)
echo ========================================================
echo.
echo 1. Starting Web Attendance Portal on http://localhost:5000...
start "NextCampus Web Server" cmd /k "cd /d "%~dp0python" && python attendance_server.py"
timeout /t 2 >nul
start http://localhost:5000

echo.
echo 2. Launching Teacher Desktop App (Java Swing)...
start "NextCampus Desktop GUI" cmd /c "%~dp0run_NextCampus.bat"

echo.
echo ========================================================
echo   NextCampus is now running!
echo ========================================================
pause
