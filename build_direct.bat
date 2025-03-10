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

:: SLF4J API
if exist "lib\slf4j-api-2.0.9\slf4j-api-2.0.9.jar" (
    copy "lib\slf4j-api-2.0.9\slf4j-api-2.0.9.jar" "dist\lib\" > nul
    echo SLF4J API copied successfully
) else (
    echo ERROR: SLF4J API JAR not found
    pause
    exit /b 1
)

:: Logback Core
if exist "lib\logback-core-1.5.17\logback-core-1.5.17.jar" (
    copy "lib\logback-core-1.5.17\logback-core-1.5.17.jar" "dist\lib\" > nul
    echo Logback Core copied successfully
) else (
    echo ERROR: Logback Core JAR not found
    pause
    exit /b 1
)

:: Logback Classic
if exist "lib\logback-classic-1.5.17\logback-classic-1.5.17.jar" (
    copy "lib\logback-classic-1.5.17\logback-classic-1.5.17.jar" "dist\lib\" > nul
    echo Logback Classic copied successfully
) else (
    echo ERROR: Logback Classic JAR not found
    pause
    exit /b 1
)

:: Set Java paths with your specific JDK installation
set JAVA_HOME=C:\Program Files\Java\jdk-23
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAR="%JAVA_HOME%\bin\jar.exe"

:: Set the classpath with all required libraries
set CLASSPATH=src;dist\lib\mysql-connector-j-9.2.0.jar;dist\lib\slf4j-api-2.0.9.jar;dist\lib\logback-core-1.5.17.jar;dist\lib\logback-classic-1.5.17.jar

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
if not exist dist\images mkdir dist\images
if exist images\*.* copy images\*.* dist\images\

:: Create run.bat in dist folder
echo Creating run.bat...
echo @echo off > dist\run.bat
<<<<<<< HEAD
echo set CLASSPATH=lib\mysql-connector-j-9.2.0.jar >> dist\run.bat
=======
echo set CLASSPATH=lib\mysql-connector-j-9.2.0.jar;lib\slf4j-api-2.0.9.jar;lib\logback-core-1.5.17.jar;lib\logback-classic-1.5.17.jar >> dist\run.bat
>>>>>>> ea43452347540bc0bce8b7ac3563d6262abb4d30
echo "%JAVA_HOME%\bin\java.exe" -cp "%%CLASSPATH%%;ClubMembershipApp.jar" ClubMembershipApp >> dist\run.bat
echo pause >> dist\run.bat

echo Done! The executable can be found in the dist folder.
echo To run the application:
echo 1. Go to the dist folder
echo 2. Double-click run.bat
pause 