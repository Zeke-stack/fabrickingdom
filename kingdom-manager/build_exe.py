import PyInstaller.__main__

PyInstaller.__main__.run([
    'main.py',
    '--onefile',
    '--windowed',
    '--name=KingdomServerManager',
    '--icon=icon.ico',
    '--add-data=requirements.txt;.',
    '--hidden-import=tkinter',
    '--hidden-import=PIL',
    '--hidden-import=requests',
    '--clean',
    '--noconfirm'
])
