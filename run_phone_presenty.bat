@echo off
title Phone Presenty Server - College SMS
cd /d "%~dp0python"
echo ========================================================
echo   Starting Phone Presenty Server...
echo ========================================================
echo Checking Flask requirement...
pip install flask -q
python attendance_server.py
pause
