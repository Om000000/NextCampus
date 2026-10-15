@echo off
title NextCampus - Export Docker Image for Lab PC
cd /d "%~dp0"

echo ========================================================
echo   NextCampus - Export Docker Image for Pen Drive
echo ========================================================
echo This will package the entire application into a single
echo portable file ('nextcampus-image.tar') that you can copy
echo to your pen drive and run on ANY lab PC with Docker!
echo.
echo No internet connection will be needed in the college lab!
echo ========================================================
echo.

if not exist nextcampus-image.tar (
    echo Exporting nextcampus:latest image... (this may take 1-2 minutes)
    docker save -o nextcampus-image.tar nextcampus:latest
    if errorlevel 1 (
        echo [ERROR] Failed to export Docker image. Ensure Docker is running.
        pause
        exit /b 1
    )
    echo.
    echo ========================================================
    echo [SUCCESS] 'nextcampus-image.tar' has been created!
    echo ========================================================
) else (
    echo 'nextcampus-image.tar' already exists.
    set /p re="Do you want to overwrite it with the latest build? (y/n): "
    if /i "%re%"=="y" (
        echo Re-exporting nextcampus:latest image...
        docker save -o nextcampus-image.tar nextcampus:latest
        echo Done!
    )
)

echo.
echo Files to copy to your Pen Drive:
echo   1. nextcampus-image.tar
echo   2. run-on-lab-pc.bat
echo   3. database\ folder
echo.
pause
