@echo off
mvn clean package
copy target\football-fixture-management-1.0-SNAPSHOT.jar .
echo Build complete! You can now run the application using run-jar.bat
pause
