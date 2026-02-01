@echo off
echo Creating simple KingdomCommands plugin...

REM Create directories
if not exist "target\classes" mkdir target\classes
if not exist "target" mkdir target

REM Create a simple manifest
echo Manifest-Version: 1.0 > target\manifest.txt
echo Main-Class: com.kingdom.commands.KingdomCommands >> target\manifest.txt

REM Copy resources directly to target
xcopy /E /I /Y "src\main\resources" "target" >nul

REM Create JAR with resources only for now
cd target
jar cfm KingdomCommands-1.0.0.jar manifest.txt plugin.yml config.yml
cd ..

echo Simple JAR created: target\KingdomCommands-1.0.0.jar
echo Note: This is a basic version. Full compilation requires Bukkit API.
pause
