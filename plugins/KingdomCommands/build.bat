@echo off
echo Building KingdomCommands plugin...

REM Create directories
if not exist "target\classes" mkdir target\classes
if not exist "target" mkdir target

REM Find PaperMC jar
set PAPER_JAR=..\..\paper-1.21.1-133.jar
if not exist "%PAPER_JAR%" (
    echo ERROR: PaperMC jar not found at %PAPER_JAR%
    echo Please ensure paper-1.21.1-133.jar is in the server root directory
    pause
    exit /b 1
)

REM Compile Java files
echo Compiling Java files...
javac -cp "%PAPER_JAR%" -d target\classes src\main\java\com\kingdom\commands\*.java src\main\java\com\kingdom\commands\commands\*.java src\main\java\com\kingdom\commands\listeners\*.java src\main\java\com\kingdom\commands\utils\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Copy resources
echo Copying resources...
xcopy /E /I /Y "src\main\resources" "target\classes" >nul

REM Create JAR
echo Creating JAR file...
cd target\classes
jar cf ..\KingdomCommands-1.0.0.jar .
cd ..\..

if %ERRORLEVEL% neq 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

echo Build completed successfully!
echo Plugin JAR created: target\KingdomCommands-1.0.0.jar
pause
