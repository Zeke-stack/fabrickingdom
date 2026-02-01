from setuptools import setup, find_packages

setup(
    name="KingdomServerManager",
    version="1.0.0",
    description="A powerful Windows application for managing Minecraft Kingdom Server",
    author="Kingdom Server Team",
    author_email="admin@kingdomserver.com",
    packages=find_packages(),
    install_requires=[
        "tkinter",
        "pillow>=9.0.0",
        "requests>=2.25.0",
        "pyinstaller>=4.0"
    ],
    python_requires=">=3.7",
    entry_points={
        'console_scripts': [
            'kingdom-manager=main:main',
        ],
    },
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: System Administrators",
        "License :: OSI Approved :: MIT License",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Operating System :: Microsoft :: Windows",
    ],
)
