@echo off
echo Setting up directories...

:: Create required directories
if not exist bin mkdir bin
if not exist dist mkdir dist
if not exist dist\lib mkdir dist\lib

:: Copy all required JAR files to lib directory
echo Copying JAR files...
copy "lib\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar" "dist\lib\"
copy "lib\slf4j-api-2.0.9\slf4j-api-2.0.9.jar" "dist\lib\"
copy "lib\logback-core-1.5.17\logback-core-1.5.17.jar" "dist\lib\"
copy "lib\logback-classic-1.5.17\logback-classic-1.5.17.jar" "dist\lib\"

echo Compiling Java files...
:: Compile Java files with explicit classpath
javac -d bin -cp "dist\lib\mysql-connector-j-9.2.0.jar;dist\lib\slf4j-api-2.0.9.jar;dist\lib\logback-core-1.5.17.jar;dist\lib\logback-classic-1.5.17.jar;src" src\*.java

:: Create JAR file
echo Creating JAR file...
cd bin
jar cfm ..\ClubMembershipApp.jar ..\src\MANIFEST.MF *.class
cd ..

:: Copy other required files
echo Copying other required files...
copy ClubMembershipApp.jar dist\
copy src\logback.xml dist\
if not exist dist\images mkdir dist\images
copy images\*.* dist\images\

echo Done! The executable can be found in the dist folder.
pause 