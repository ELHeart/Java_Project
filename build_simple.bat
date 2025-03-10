@echo off
echo Setting up directories...

:: Create required directories
if not exist bin mkdir bin
if not exist dist mkdir dist
if not exist dist\lib mkdir dist\lib

:: Copy MySQL connector
echo Copying MySQL connector...
copy "lib\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar" "dist\lib\" > nul
if errorlevel 1 (
    echo Failed to copy MySQL connector
    pause
    exit /b 1
)

:: Set Java paths
set JAVA_HOME=C:\Program Files\Java\jdk-23
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAR="%JAVA_HOME%\bin\jar.exe"

:: Set classpath
set CLASSPATH=.;src;lib\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar

echo Compiling Java files...
:: List all Java files
dir /s /B src\*.java > sources.txt

:: Compile all Java files
%JAVAC% -d bin -cp "%CLASSPATH%" @sources.txt
if errorlevel 1 (
    echo Compilation failed
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

:: Create JAR file
echo Creating JAR file...
cd bin
%JAR% cfm ..\dist\ClubMembershipApp.jar ..\src\MANIFEST.MF *
cd ..

:: Copy resources
echo Copying resources...
if not exist dist\images mkdir dist\images
if exist images\*.* copy images\*.* dist\images\

:: Create run script
echo Creating run script...
(
echo @echo off
echo set CLASSPATH=lib\mysql-connector-j-9.2.0.jar
echo java -cp "%%CLASSPATH%%;ClubMembershipApp.jar" ClubMembershipApp
echo pause
) > dist\run.bat

echo Build complete! Check the dist folder.
pause 