@echo off
title NextCampus - Docker Runner
cd /d "%~dp0"

echo ========================================================
echo   NextCampus - Docker Container Runner
echo ========================================================
echo 1. Start Web Portal (Phone Presenty on port 5000)
echo 2. Launch Interactive Console App (CLI Mode)
echo 3. Start In-Browser Desktop GUI (noVNC on port 6080)
echo 4. Stop and Clean Up Containers
echo 5. View Container Logs
echo 0. Exit
echo ========================================================
set /p choice="Choose an option (1-5, or 0): "

if "%choice%"=="1" (
    echo Starting Web Portal...
    docker compose up -d web
    echo.
    echo Web portal is running at: http://localhost:5000
    echo To view logs, run: docker compose logs -f web
    pause
) else if "%choice%"=="2" (
    echo Launching Interactive Console...
    docker compose run --rm console
) else if "%choice%"=="3" (
    echo Starting In-Browser Desktop GUI...
    docker compose --profile desktop up -d --build desktop
    echo.
    echo Desktop UI is running at: http://localhost:6080/vnc.html?autoconnect=true^&resize=scale
    echo (Or visit: http://localhost:6080)
    echo To view logs, run: docker compose logs -f desktop
    pause
) else if "%choice%"=="4" (
    echo Stopping all NextCampus containers...
    docker compose --profile desktop down
    echo Containers stopped.
    pause
) else if "%choice%"=="5" (
    docker compose logs -f
) else (
    echo Exiting.
)
