@echo off
echo Checking Java installation...

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK and add it to your PATH
    echo You can download JDK from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

:: Check if javac is available
javac -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java Development Kit (JDK) is not installed or not in PATH
    echo Please install JDK and add it to your PATH
    echo You can download JDK from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Java checks passed successfully
echo Setting up directories...

:: Create required directories
if not exist bin mkdir bin
if not exist dist mkdir dist
if not exist dist\lib mkdir dist\lib

:: Copy all required JAR files to lib directory
echo Copying JAR files...

:: MySQL Connector
if exist "lib\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar" (
    copy "lib\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar" "dist\lib\" > nul
    echo MySQL Connector copied successfully
) else (
    echo ERROR: MySQL Connector JAR not found
    pause
    exit /b 1
)

:: Set the classpath
set CLASSPATH=src;dist\lib\mysql-connector-j-9.2.0.jar

echo Compiling Java files...
:: Compile Java files
javac -d bin -cp "%CLASSPATH%" src\*.java
if errorlevel 1 (
    echo Compilation failed
    pause
    exit /b 1
)

:: Create JAR file
echo Creating JAR file...
cd bin
jar cfm ..\ClubMembershipApp.jar ..\src\MANIFEST.MF *.class
cd ..

:: Copy other required files
echo Copying other required files...
copy ClubMembershipApp.jar dist\
if exist src\logback.xml copy src\logback.xml dist\
if not exist dist\images mkdir dist\images
if exist images\*.* copy images\*.* dist\images\

:: Create run.bat in dist folder
echo Creating run.bat...
echo @echo off > dist\run.bat
echo java -jar ClubMembershipApp.jar >> dist\run.bat
echo pause >> dist\run.bat

echo Done! The executable can be found in the dist folder.
echo To run the application:
echo 1. Go to the dist folder
echo 2. Double-click run.bat
pause 