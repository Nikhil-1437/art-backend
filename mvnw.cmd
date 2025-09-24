@echo off
setlocal

set MAVEN_CMD_LINE_ARGS=%*

rem Find the project base dir, i.e. the directory that contains the .mvn directory.
set MAVEN_PROJECT_BASE_DIR=%~dp0
:loop
if not exist "%MAVEN_PROJECT_BASE_DIR%\.mvn" (
    cd ..
    set MAVEN_PROJECT_BASE_DIR=%cd%
)
if "%MAVEN_PROJECT_BASE_DIR%"=="%MAVEN_PROJECT_BASE_DIR%\" (
    echo Neither this directory nor any of its parent directories seem to contain the Maven Wrapper configuration files.
    exit /B 1
)
if exist "%MAVEN_PROJECT_BASE_DIR%\.mvn" goto found
goto loop
:found

set WRAPPER_JAR="%MAVEN_PROJECT_BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECT_BASE_DIR%\.mvn\wrapper\maven-wrapper.properties"

for /F "usebackq delims=" %%i in (%WRAPPER_PROPERTIES%) do (
    set WRAPPER_URL=%%i
)
for /F "tokens=2 delims==" %%i in ("%WRAPPER_URL%") do (
    set WRAPPER_URL=%%i
)

if not exist %WRAPPER_JAR% (
    echo Downloading %WRAPPER_URL%
    powershell -Command "Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %WRAPPER_JAR%"
)

if defined JAVA_HOME (
    set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
) else (
    set JAVA_EXE=java.exe
)

%JAVA_EXE% -jar %WRAPPER_JAR% %MAVEN_CMD_LINE_ARGS%

if ERRORLEVEL 1 exit /B 1
endlocal