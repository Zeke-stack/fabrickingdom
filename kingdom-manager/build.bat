@echo off
echo Building Kingdom Server Manager EXE...

REM Install PyInstaller if not already installed
pip install pyinstaller pillow requests

REM Run the build script
python build_exe.py

echo.
echo Build complete! 
echo The EXE file is located in the 'dist' folder.
echo.
pause
