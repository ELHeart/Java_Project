@echo off
echo Compiling Java files...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile Java files
javac -d bin -cp "lib/*;src" src/*.java

:: Create JAR file
echo Creating JAR file...
cd bin
jar cfm ../ClubMembershipApp.jar ../src/MANIFEST.MF *.class
cd ..

:: Copy required files
echo Copying required files...
if not exist dist mkdir dist
copy ClubMembershipApp.jar dist\
if not exist dist\lib mkdir dist\lib
copy lib\*.jar dist\lib\
copy src\logback.xml dist\
if not exist dist\images mkdir dist\images
copy images\*.* dist\images\

echo Done! The executable can be found in the dist folder.
pause 