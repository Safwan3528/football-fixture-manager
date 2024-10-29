@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

java --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED ^
--add-opens=javafx.controls/javafx.scene.control.skin=ALL-UNNAMED ^
-jar target/football-fixture-management-1.0-SNAPSHOT.jar

pause
