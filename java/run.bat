@echo off
REM Compile and run College SMS (Windows)
cd /d "%~dp0"

if not exist out mkdir out

echo Compiling...
javac -cp "lib/*;src" -d out ^
  src\com\college\sms\*.java ^
  src\com\college\sms\db\*.java ^
  src\com\college\sms\model\*.java ^
  src\com\college\sms\dao\*.java ^
  src\com\college\sms\ui\*.java ^
  src\com\college\sms\util\*.java

if errorlevel 1 (
  echo Compile failed. Check JDK is installed and sqlite-jdbc jar is in java\lib\
  pause
  exit /b 1
)

echo Starting app...
java --enable-native-access=ALL-UNNAMED -cp "lib/*;out" com.college.sms.Main
pause
