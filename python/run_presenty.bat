@echo off
cd /d "%~dp0"
echo Installing Flask if needed...
pip install flask -q
python attendance_server.py
pause
