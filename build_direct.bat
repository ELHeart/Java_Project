@echo off
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

:: Set Java paths (MODIFY THESE PATHS TO MATCH YOUR JAVA INSTALLATION)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAR="%JAVA_HOME%\bin\jar.exe"

:: Set the classpath
set CLASSPATH=src;dist\lib\mysql-connector-j-9.2.0.jar

echo Compiling Java files...
:: Compile Java files
%JAVAC% -d bin -cp "%CLASSPATH%" src\*.java
if errorlevel 1 (
    echo Compilation failed
    pause
    exit /b 1
)

:: Create JAR file
echo Creating JAR file...
cd bin
%JAR% cfm ..\ClubMembershipApp.jar ..\src\MANIFEST.MF *.class
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
echo "%JAVA_HOME%\bin\java.exe" -jar ClubMembershipApp.jar >> dist\run.bat
echo pause >> dist\run.bat

echo Done! The executable can be found in the dist folder.
echo To run the application:
echo 1. Go to the dist folder
echo 2. Double-click run.bat
pause 