@echo off
mkdir "dist\Football Fixture Management"
copy "target\football-fixture-management-1.0-SNAPSHOT.jar" "dist\Football Fixture Management\"
copy "target\FootballFixtureManagement.exe" "dist\Football Fixture Management\"
copy "run.bat" "dist\Football Fixture Management\"
copy "install.bat" "dist\Football Fixture Management\"
copy "README.txt" "dist\Football Fixture Management\"
mkdir "dist\Football Fixture Management\lib"
copy "target\lib\*" "dist\Football Fixture Management\lib\"
echo Distribution package created in 'dist' folder
pause
