@echo off
REM Launcher script for School Timetable System
echo Building and launching School Timetable System...

REM Check if mvn is directly available in PATH
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    call mvn compile exec:java
) else (
    REM Fallback to IntelliJ bundled Maven if installed
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        call "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" compile exec:java
    ) else (
        echo Error: Maven (mvn) was not found in PATH or standard IntelliJ directory.
        pause
    )
)
